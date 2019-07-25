package pro.fessional.wings.slardar.spring.bean;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * @author trydofor
 * @since 2019-07-23
 */

@Configuration
@ConditionalOnProperty(prefix = "spring.wings.filter.overload", name = "enabled", havingValue = "true")
@ConditionalOnClass(Filter.class)
public class WingsOverloadFilterConfiguration {

    private final Log logger = LogFactory.getLog(WingsOverloadFilterConfiguration.class);
    private final AtomicBoolean inactive = new AtomicBoolean(false);

    @Component
    public class SafelyShutdown implements ApplicationListener<ContextClosedEvent> {
        @Override
        public void onApplicationEvent(ContextClosedEvent event) {
            inactive.set(true);
            logger.warn("safely shutdown, deny any request");
        }
    }

    @Bean
    @ConditionalOnMissingBean(FallBack.class)
    public FallBack overloadFallback(OverloadConfig config) {
        return (request, response) -> {
            try {
                if (response instanceof HttpServletResponse) {
                    HttpServletResponse res = (HttpServletResponse) response;
                    res.setStatus(config.getFallbackCode());
                }
                PrintWriter writer = response.getWriter();
                writer.println(config.getFallbackBody());
                writer.flush();
            } catch (IOException e) {
                // ignore
            }
        };
    }

    @Bean
    public FilterRegistrationBean overloadFilter(OverloadConfig config, FallBack fallBack) {
        FilterRegistrationBean<OverloadFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new OverloadFilter(fallBack, config));
        bean.setOrder(1);
        return bean;
    }

    @Bean
    @ConfigurationProperties("spring.wings.filter.overload")
    public OverloadConfig circuitBreakerFilterConfig() {
        return new OverloadConfig();
    }

    @RequiredArgsConstructor
    public class OverloadFilter implements Filter {

        private final FallBack fallBack;
        private final OverloadConfig config;
        private final ConcurrentHashMap<String, ResponsiveStat> uriStat = new ConcurrentHashMap<>();

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            if (inactive.get()) {
                fallBack.fallback(request, response);
                return;
            }

            if (!(request instanceof HttpServletRequest)) {
                chain.doFilter(request, response);
                return;
            }

            // 只能处理http的，目前的情况
            final HttpServletRequest req = (HttpServletRequest) request;
            final String key = req.getRequestURI();
            final ResponsiveStat stat = uriStat.get(key);
            final long bgn = System.nanoTime();
            if (stat == null) {
                chain.doFilter(request, response);
                checkCapacityForNext(response, null, key, bgn);
                return;
            }

            //
            final AtomicLong nowReqs = stat.nowReqs;
            if (nowReqs.get() < stat.maxReqs.get()) {
                nowReqs.incrementAndGet();
                try {
                    chain.doFilter(request, response);
                    checkCapacityForNext(response, stat, key, bgn);
                } finally {
                    nowReqs.decrementAndGet();
                }
            } else {
                fallBack.fallback(request, response);
                if (logger.isWarnEnabled()) {
                    long now = System.currentTimeMillis();
                    if (now > config.getLoggerInterval() + stat.logTime.get()) {
                        logger.warn("wings-slow-response, max=" + stat.maxReqs.get() + ", now=" + stat.nowReqs + ", uri=" + key);
                        stat.logTime.set(now);
                    }
                }
            }
        }

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            inactive.set(false);
            logger.info("WingsCircuitBreakerFilter init");
        }

        @Override
        public void destroy() {
            logger.info("WingsCircuitBreakerFilter destroy");
        }

        //
        private void checkCapacityForNext(ServletResponse response, ResponsiveStat stat, String key, long bgn) {
            // 只处理成功的，其他的忽略。
            if (!(response instanceof HttpServletResponse)) return;

            HttpServletResponse res = (HttpServletResponse) response;
            if (res.getStatus() != 200) return;

            long end = System.nanoTime();
            long cost = NANOSECONDS.toMicros(end - bgn);

            // 跳过预热，不考虑重复设置
            if (stat == null) {
                uriStat.putIfAbsent(key, new ResponsiveStat()); // 不考虑
                return;
            }

            long sumReqs = stat.sumReqs.incrementAndGet();
            long sumCost = stat.sumCost.addAndGet(cost);
            long avgCost = sumCost / sumReqs;
            if (avgCost < 0) {// overflow
                stat.sumReqs.set(0);
                stat.sumCost.set(0);
                return;
            }

            // 第二次或每10次，调整一下容量
            if (sumCost % 10 == 1) {
                long maxReqs = MILLISECONDS.toMicros(config.responseLatency) / avgCost;
                maxReqs = maxReqs + maxReqs * config.responseOverrate / 100;
                stat.maxReqs.set(maxReqs);
            }

            if (logger.isWarnEnabled() && cost > avgCost + avgCost * config.responseOverrate / 100) {
                long now = System.currentTimeMillis();
                if (now > config.getLoggerInterval() + stat.logTime.get()) {
                    logger.warn("wings-slow-response, avg=" + avgCost + ", uri=" + key);
                    stat.logTime.set(now);
                }
            }

            if (logger.isInfoEnabled() && sumReqs % config.responseInfofreq == 0) {
                logger.info("wings-stat-response, avg=" + avgCost + ", sum=" + sumReqs + ", max=" + stat.maxReqs.get() + ", uri=" + key);
            }
        }
    }


    @Data
    public class OverloadConfig {
        private long loggerInterval = 3000;

        private int fallbackCode = 200;
        private String fallbackBody = "";

        private long responseLatency = 3000;
        private int responseOverrate = 30;
        private int responseInfofreq = 1000;
    }

    public class ResponsiveStat {
        private final AtomicLong logTime = new AtomicLong(0);
        private final AtomicLong sumCost = new AtomicLong(0);
        private final AtomicLong sumReqs = new AtomicLong(0);
        private final AtomicLong nowReqs = new AtomicLong(0);
        private final AtomicLong maxReqs = new AtomicLong(30);
    }

    public interface FallBack {
        void fallback(ServletRequest request, ServletResponse response);
    }
}
