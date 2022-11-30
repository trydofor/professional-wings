package pro.fessional.wings.slardar.servlet.filter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.slardar.constants.SlardarOrderConst;
import pro.fessional.wings.slardar.servlet.resolver.WingsRemoteResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author trydofor
 * @since 2019-11-14
 */
public class WingsOverloadFilter implements OrderedFilter {

    private final Log log = LogFactory.getLog(WingsOverloadFilter.class);

    @Setter @Getter
    private int order = SlardarOrderConst.OrderFilterOverload;

    private final AtomicInteger requestCapacity = new AtomicInteger(0);
    private final AtomicInteger requestProcess = new AtomicInteger(0);

    private final ConcurrentHashMap<String, Long> lastWarnSlow = new ConcurrentHashMap<>();
    private final AtomicLong lastInfoStat = new AtomicLong(0);


    private final int costStep = 20;
    private final AtomicLong[] responseCost; // 10s
    private final AtomicLong responseTotal = new AtomicLong(0);

    private final FallBack fallBack;
    private final Config config;
    private final WingsRemoteResolver terminalResolver;
    private final Cache<String, CalmDown> spiderCache;

    public WingsOverloadFilter(FallBack fallBack, Config config, WingsRemoteResolver terminalResolver) {
        this.fallBack = fallBack;
        this.config = config;
        this.terminalResolver = terminalResolver;

        if (config.requestInterval <= 0 || config.requestCalmdown <= 0) {
            this.spiderCache = null;
        }
        else {
            int capacity = initCapacity(config);
            requestCapacity.set(capacity);

            this.spiderCache = Caffeine.newBuilder()
                                       .maximumSize(capacity)
                                       .expireAfterAccess(config.requestInterval * config.requestCalmdown * 2, MILLISECONDS)
                                       .build();
        }
        if (config.responseInfoStat <= 0) {
            responseCost = new AtomicLong[0];
        }
        else {
            responseCost = new AtomicLong[500];
        }
        for (int i = 0; i < responseCost.length; i++) {
            responseCost[i] = new AtomicLong(0);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (requestProcess.get() > requestCapacity.get()) {
            fallBack.fallback(request, response);
            return;
        }

        if (!(request instanceof HttpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }

        // 只能处理http的，目前的情况
        final long now = ThreadNow.millis();
        final HttpServletRequest httpReq = (HttpServletRequest) request;
        final CalmDown calmDown = letCalmDown(httpReq);

        // 快请求，累积后清零
        if (calmDown != null) {
            final int rqs = calmDown.heardRequest.incrementAndGet(); // 等待处理的请求
            final boolean isCnt = rqs >= config.requestCalmdown;
            final boolean isFst = now - calmDown.firstRequest.get() < config.requestInterval;
            if (isCnt && isFst) {
                fallBack.fallback(request, response);
                if (log.isWarnEnabled() && now > config.getLogInterval() + lastWarnSlow.getOrDefault(calmDown.ip, 0L)) {
                    log.warn("wings-clam-request, now=" + rqs + ", ip=" + calmDown.ip + ", uri=" + httpReq.getRequestURI());
                    lastWarnSlow.put(calmDown.ip, now);
                }
                return; // 直接返回
            }
            else {
                if (!isFst) calmDown.firstRequest.set(now);
                if (isCnt) calmDown.heardRequest.addAndGet(-rqs);
            }
        }

        requestProcess.incrementAndGet();
        try {
            chain.doFilter(request, response);
            checkAndStats(httpReq, response, now, ThreadNow.millis());
        }
        finally {
            requestProcess.decrementAndGet();
        }
    }

    public void setRequestCapacity(int capacity) {
        requestCapacity.set(capacity);
    }

    public int getRequestProcess() {
        return requestProcess.get();
    }

    //
    @RequiredArgsConstructor
    private static class CalmDown {
        private final String ip;
        private final AtomicLong firstRequest = new AtomicLong(0);
        private final AtomicInteger heardRequest = new AtomicInteger(0);
    }

    @Data
    public static class Config {
        /**
         * 日志的记录间隔（毫秒）
         */
        private long logInterval = 3000;

        /**
         * 过载时，默认http status code
         */
        private int fallbackCode = 200;
        /**
         * 过载时，默认http body 内容
         */
        private String fallbackBody = "";

        /**
         * 注意，共享ip的容易误判
         * 处理中的的最大请求数量，`<0`表示无限制；
         * `>0`表示用户根据压力测试结果推算的值。
         * `0`表示自动调整，初始值为 cpu核心数*300
         */
        private int requestCapacity = 9000;
        /**
         * 在`interval`毫秒内，同ip的处理中的请求不能超过`calmdown`个。`<=0`表示无限制
         */
        private long requestInterval = 1000;
        /**
         * 在`interval`毫秒内，同ip的处理中的请求不能超过`calmdown`个。`<=0`表示无限制
         */
        private int requestCalmdown = 50;
        /**
         * 请求ip白名单，分号分割，前部匹配 127. 192.
         */
        private Map<String, String> requestPermit = Collections.emptyMap();

        /**
         * 满响应（毫秒数），超过时，记录WARN日志，小于0表示关闭
         */
        private long responseWarnSlow = 5000;
        /**
         * 每多少个请求记录一次INFO日志，小于0表示关闭
         */
        private long responseInfoStat = 1000;
    }

    public interface FallBack {
        void fallback(ServletRequest request, ServletResponse response);
    }

    //
    private CalmDown letCalmDown(HttpServletRequest httpReq) {
        if (spiderCache == null) return null; // 不需要处理ip问题

        final String ip = terminalResolver.resolveRemoteIp(httpReq);

        for (String p : config.requestPermit.values()) {
            if (ip.startsWith(p) && !p.isEmpty()) {
                return null; // 白名单，不需要处理。
            }
        }

        return spiderCache.get(ip, CalmDown::new);
    }

    private int initCapacity(Config config) {
        if (config.requestCapacity > 0) return config.requestCapacity;
        if (config.requestCapacity < 0) return Integer.MAX_VALUE;

        int cnt = Runtime.getRuntime().availableProcessors() * 300;
        return Math.max(cnt, 2000);
    }

    private void checkAndStats(HttpServletRequest request, ServletResponse response, long bgn, long end) {
        // 只处理成功的，其他的忽略。
        if (!(response instanceof HttpServletResponse)) return;

        HttpServletResponse res = (HttpServletResponse) response;
        if (res.getStatus() != 200) return;

        // 慢响应
        final long cost = end - bgn;
        final long warnSlow = config.responseWarnSlow;
        if (log.isWarnEnabled() && warnSlow > 0 && cost > warnSlow) {
            String uri = request.getRequestURI();
            if (end > config.getLogInterval() + lastWarnSlow.getOrDefault(uri, 0L)) {
                log.warn("wings-slow-response, slow=" + warnSlow + ", cost=" + cost + ", uri=" + uri);
                lastWarnSlow.put(uri, end);
            }
        }

        // 统计，已完成的请求，忽略并发误差。
        if (responseCost.length > 0) {
            int idx = (int) (cost % costStep);
            if (idx >= responseCost.length) {
                responseCost[responseCost.length - 1].incrementAndGet();
            }
            else {
                responseCost[idx].incrementAndGet();
            }
            long total = responseTotal.incrementAndGet();
            if (log.isInfoEnabled()
                && (config.responseInfoStat == 0 || total % config.responseInfoStat == 0)
                && end - lastInfoStat.get() > config.responseInfoStat) {

                long sum = 0;
                int p99 = 0;
                int p95 = 0;
                int p90 = 0;
                for (int i = 0; i < responseCost.length; i++) {
                    sum += responseCost[i].get();
                    long rate = sum * 100 / total;
                    if (rate >= 99 && p99 == 0) {
                        p99 = costStep * i;
                    }
                    if (rate >= 95 && p95 == 0) {
                        p95 = costStep * i;
                    }
                    if (rate >= 90 && p90 == 0) {
                        p90 = costStep * i;
                    }
                }

                log.info("wings-snap-response "
                            + ", total-resp=" + total
                            + ", p99=" + p99
                            + ", p95=" + p95
                            + ", p90=" + p90
                            + ", process=" + requestProcess.get()
                            + ", capacity=" + requestCapacity.get());
                lastInfoStat.set(end);
            }
        }
    }

}
