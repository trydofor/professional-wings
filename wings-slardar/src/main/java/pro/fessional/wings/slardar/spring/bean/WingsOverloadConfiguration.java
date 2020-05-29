package pro.fessional.wings.slardar.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.fessional.wings.slardar.servlet.WingsFilterOrder;
import pro.fessional.wings.slardar.servlet.WingsOverloadFilter;
import pro.fessional.wings.slardar.servlet.WingsRemoteResolver;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 自动计算单线程和全局请求数。
 * 收到TERM信号时，阻止所有请求。
 *
 * @author trydofor
 * @since 2019-07-23
 */

@Configuration
@ConditionalOnProperty(prefix = "spring.wings.slardar.overload", name = "enabled", havingValue = "true")
@ConditionalOnClass(Filter.class)
public class WingsOverloadConfiguration {

    private final Log logger = LogFactory.getLog(WingsOverloadConfiguration.class);

    @Component
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @RequiredArgsConstructor
    public class SafelyShutdown implements ApplicationListener<ContextClosedEvent> {
        private final WingsOverloadFilter overloadFilter;

        @Override
        public void onApplicationEvent(ContextClosedEvent event) {
            overloadFilter.setRequestCapacity(Integer.MIN_VALUE);
            logger.warn("Wings shutting down, deny new request, current=" + overloadFilter.getRequestProcess());
            while (overloadFilter.getRequestProcess() > 0) {
                try {
                    Thread.sleep(30); // 忙等
                } catch (InterruptedException e) {
                    // ignore
                }
            }
            logger.warn("Wings safely shutting down, no request in processing");
        }
    }

    @Bean
    @ConditionalOnMissingBean(WingsOverloadFilter.FallBack.class)
    public WingsOverloadFilter.FallBack overloadFallback(WingsOverloadFilter.Config config) {
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
    public WingsOverloadFilter wingsOverloadFilter(WingsOverloadFilter.Config config,
                                                   WingsOverloadFilter.FallBack fallBack,
                                                   WingsRemoteResolver resolver) {
        logger.info("Wings conf Overload filter");
        WingsOverloadFilter filter = new WingsOverloadFilter(fallBack, config, resolver);
        filter.setOrder(WingsFilterOrder.OVERLOAD);
        return filter;
    }

    @Bean
    @ConfigurationProperties("wings.slardar.overload")
    public WingsOverloadFilter.Config wingsOverloadFilterConfig() {
        return new WingsOverloadFilter.Config();
    }
}
