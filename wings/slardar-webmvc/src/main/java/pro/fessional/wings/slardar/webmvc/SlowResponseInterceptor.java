package pro.fessional.wings.slardar.webmvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.AntPathMatcher;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.silencer.tweak.AntMatcherMap;
import pro.fessional.wings.silencer.watch.Watches;
import pro.fessional.wings.silencer.watch.Watches.Threshold;

import java.util.Map;
import java.util.function.BiConsumer;

import static pro.fessional.wings.slardar.constants.SlardarServletConst.AttrStopWatch;

/**
 * @author trydofor
 * @since 2019-11-16
 */
@Slf4j
public class SlowResponseInterceptor implements AutoRegisterInterceptor {

    public static final int ORDER = WingsOrdered.Lv5Supervisor + 1_000;

    @Getter @Setter
    private int order = ORDER;

    /**
     * The slow threshold in ms, `-1` means disable
     */
    @Getter @Setter
    private volatile long thresholdMillis = -1;
    private final AntMatcherMap<Long> antThreshold = new AntMatcherMap<>(new AntPathMatcher());

    public void setThresholdUri(Map<String, Long> thresholds) {
        antThreshold.putAll(thresholds);
    }

    public void setThresholdUri(String name, long ms) {
        antThreshold.put(name, ms);
    }

    /**
     * Instead of logging, handle time-consuming and SQL yourself
     */
    @Setter
    private BiConsumer<Long, HttpServletRequest> costAndReqConsumer = (c, r) -> log.warn("SLOW-RES cost={}ms, uri={}", c, r.getRequestURI());

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {
        final String uri = request.getRequestURI();
        long maxMs = thresholdMillis;
        if (maxMs < 0) return true;

        // key threshold over ant matcher
        final Long keyMs = antThreshold.get(uri);
        if (keyMs != null) {
            if (keyMs < 0) {
                return true;
            }
            else {
                maxMs = keyMs;
            }
        }

        final Threshold threshold = Watches.threshold(uri, maxMs);
        request.setAttribute(AttrStopWatch.value, threshold);

        return true;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request,
                                @NotNull HttpServletResponse response,
                                @NotNull Object handler, Exception ex) {
        final Threshold threshold = (Threshold) request.getAttribute(AttrStopWatch.value);
        if (threshold == null) return;

        final boolean slow = threshold.reach();
        try {
            if (slow) {
                costAndReqConsumer.accept(threshold.elapse(), request);
            }
        }
        finally {
            Watches.release(true, slow ? "SlowResponseInterceptor" : null);
        }
    }
}
