package pro.fessional.wings.slardar.webmvc;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.time.StopWatch.Watch;
import pro.fessional.wings.silencer.watch.Watches;
import pro.fessional.wings.slardar.constants.SlardarOrderConst;
import pro.fessional.wings.slardar.constants.SlardarServletConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.BiConsumer;

/**
 * @author trydofor
 * @since 2019-11-16
 */
@Slf4j
public class SlowResponseInterceptor implements AutoRegisterInterceptor {

    /**
     * slow阈值的毫秒数，-1表示关闭此功能
     */
    @Getter @Setter
    private long thresholdMillis = -1;

    /**
     * 取代日志，自行处理耗时与SQL
     */
    @Getter @Setter
    private BiConsumer<Long, HttpServletRequest> costAndReqConsumer = (c, r) -> log.warn("SLOW-RES cost={}ms, uri={}", c, r.getRequestURI());

    @Getter @Setter
    private int order = SlardarOrderConst.OrderSlowResponseInterceptor;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {
        if (thresholdMillis < 0) return true;

        final Watch watch = Watches.acquire().start(request.getRequestURI());
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
