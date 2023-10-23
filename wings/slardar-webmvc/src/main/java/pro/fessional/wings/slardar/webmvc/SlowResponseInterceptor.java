package pro.fessional.wings.slardar.webmvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.time.StopWatch.Watch;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.silencer.watch.Watches;
import pro.fessional.wings.slardar.constants.SlardarServletConst;

import java.util.function.BiConsumer;

/**
 * @author trydofor
 * @since 2019-11-16
 */
@Slf4j
@Getter @Setter
public class SlowResponseInterceptor implements AutoRegisterInterceptor {

    public static final int ORDER = WingsOrdered.Lv5Supervisor + 1_000;

    private int order = ORDER;

    /**
     * The slow threshold in ms, `-1` means disable
     */
    private long thresholdMillis = -1;

    /**
     * Instead of logging, handle time-consuming and SQL yourself
     */
    private BiConsumer<Long, HttpServletRequest> costAndReqConsumer = (c, r) -> log.warn("SLOW-RES cost={}ms, uri={}", c, r.getRequestURI());

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {
        if (thresholdMillis < 0) return true;

        final Watch watch = Watches.acquire(request.getRequestURI());
        request.setAttribute(SlardarServletConst.AttrStopWatch, watch);

        return true;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request,
                                @NotNull HttpServletResponse response,
                                @NotNull Object handler, Exception ex) {
        final Watch watch = (Watch) request.getAttribute(SlardarServletConst.AttrStopWatch);
        if (watch == null) return;

        watch.close();
        final long cost = watch.getElapseMs();
        final boolean slow = cost >= thresholdMillis;
        try {
            if (slow) {
                costAndReqConsumer.accept(cost, request);
            }
        }
        finally {
            Watches.release(true, slow ? "SlowResponseInterceptor" : null);
        }
    }
}
