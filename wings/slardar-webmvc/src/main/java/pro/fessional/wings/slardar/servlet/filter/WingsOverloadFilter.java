package pro.fessional.wings.slardar.servlet.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.cache2k.Cache;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.slardar.cache.cache2k.WingsCache2k;
import pro.fessional.wings.slardar.servlet.resolver.WingsRemoteResolver;
import pro.fessional.wings.spring.consts.OrderedSlardarConst;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Not recommended. Implementation is too simple for complexity.
 *
 * @author trydofor
 * @since 2019-11-14
 */
@Slf4j
@Deprecated
public class WingsOverloadFilter implements OrderedFilter {

    @Setter @Getter
    private int order = OrderedSlardarConst.WebFilterOverload;

    private final AtomicInteger requestCapacity = new AtomicInteger(0);
    private final AtomicInteger requestProcess = new AtomicInteger(0);
    private final AtomicLong lastInfoStat = new AtomicLong(0);

    private final int costStep = 20;
    private final AtomicLong[] responseCost; // 10s
    private final AtomicLong responseTotal = new AtomicLong(0);

    private final FallBack fallBack;
    private final Config config;
    private final WingsRemoteResolver terminalResolver;
    private final Cache<String, CalmDown> spiderCache;
    private final Cache<String, Long> lastWarnSlow;

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
            final Duration ttl = Duration.ofMillis(config.requestInterval * config.requestCalmdown * 2);
            this.spiderCache = WingsCache2k.builder(WingsOverloadFilter.class, "spiderCache", capacity, ttl, null, String.class, CalmDown.class)
                                           .build();
        }

        lastWarnSlow = WingsCache2k.builder(WingsOverloadFilter.class, "lastWarnSlow", 2000, Duration.ofHours(2), null, String.class, Long.class)
                                   .build();

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

        if (!(request instanceof final HttpServletRequest httpReq)) {
            chain.doFilter(request, response);
            return;
        }

        // Only handle http
        final long now = ThreadNow.millis();
        final CalmDown calmDown = letCalmDown(httpReq);

        // fast request, reset after calmdown
        if (calmDown != null) {
            final int rqs = calmDown.heardRequest.incrementAndGet(); // request waiting to handle
            final boolean isCnt = rqs >= config.requestCalmdown;
            final boolean isFst = now - calmDown.firstRequest.get() < config.requestInterval;
            if (isCnt && isFst) {
                fallBack.fallback(request, response);
                final Long lw = lastWarnSlow.get(calmDown.ip);
                final long lwl = lw == null ? 0L : lw;
                if (log.isWarnEnabled() && now > config.getLogInterval() + lwl) {
                    log.warn("wings-clam-request, now={}, ip={}, uri={}", rqs, calmDown.ip, httpReq.getRequestURI());
                    lastWarnSlow.put(calmDown.ip, now);
                }
                return; //
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
         * Logging interval in millis.
         */
        private long logInterval = 3000;

        /**
         * http status of response when overload
         */
        private int fallbackCode = 200;
        /**
         * body of response when overload
         */
        private String fallbackBody = "";

        /**
         * <pre>
         * fast request capacity, note that shared IP's can be easily misjudged.
         * `<0` - unlimited, max number of requests to process
         * `>0` - user defined value based on stress test results
         * `0` - auto-tuning, initial value is cpu cores x 300
         * </pre>
         */
        private int requestCapacity = 9000;
        /**
         * within `interval` milliseconds, no more than `calmdown` requests
         * can be processed for the same ip. `<=0` means no limit.
         */
        private long requestInterval = 1000;
        /**
         * within `interval` milliseconds, no more than `calmdown` requests
         * can be processed for the same ip. `<=0` means no limit.
         */
        private int requestCalmdown = 50;
        /**
         * request ip whitelist, match by start-with
         */
        private Map<String, String> requestPermit = Collections.emptyMap();

        /**
         * slow response in millis, if exceeded, log WARN, `<0` means disable
         */
        private long responseWarnSlow = 5000;
        /**
         * log INFO once for each number of requests, `<0` means disable
         */
        private long responseInfoStat = 1000;
    }

    public interface FallBack {
        void fallback(ServletRequest request, ServletResponse response);
    }

    //
    private CalmDown letCalmDown(HttpServletRequest httpReq) {
        if (spiderCache == null) return null; // ip without handle

        final String ip = terminalResolver.resolveRemoteIp(httpReq);

        for (String p : config.requestPermit.values()) {
            if (ip.startsWith(p) && !p.isEmpty()) {
                return null; // allow list
            }
        }

        return spiderCache.computeIfAbsent(ip, CalmDown::new);
    }

    private int initCapacity(Config config) {
        if (config.requestCapacity > 0) return config.requestCapacity;
        if (config.requestCapacity < 0) return Integer.MAX_VALUE;

        int cnt = Runtime.getRuntime().availableProcessors() * 300;
        return Math.max(cnt, 2000);
    }

    private void checkAndStats(HttpServletRequest request, ServletResponse response, long bgn, long end) {
        // Only handle response OK, ignore others
        if (!(response instanceof HttpServletResponse res)) return;

        if (res.getStatus() != 200) return;

        // slow response
        final long cost = end - bgn;
        final long warnSlow = config.responseWarnSlow;
        if (log.isWarnEnabled() && warnSlow > 0 && cost > warnSlow) {
            String uri = request.getRequestURI();
            final Long lw = lastWarnSlow.get(uri);
            final long lwl = lw == null ? 0L : lw;
            if (end > config.getLogInterval() + lwl) {
                log.warn("wings-slow-response, slow={}, cost={}, uri={}", warnSlow, cost, uri);
                lastWarnSlow.put(uri, end);
            }
        }

        // Statistics, completed requests, ignoring concurrency errors.
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
