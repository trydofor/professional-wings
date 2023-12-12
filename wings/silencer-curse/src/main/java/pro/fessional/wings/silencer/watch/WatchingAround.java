package pro.fessional.wings.silencer.watch;

import lombok.Getter;
import lombok.Setter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import pro.fessional.mirana.time.StopWatch.Watch;
import pro.fessional.wings.silencer.spring.WingsOrdered;

/**
 * AOP-based, stopwatch timing of methods
 *
 * @author trydofor
 * @since 2022-11-21
 */
@Aspect
@Order(WingsOrdered.Lv5Supervisor)
@Getter @Setter
public class WatchingAround {

    private long thresholdMillis = -1;

    @Around(value = "@annotation(watching)", argNames = "joinPoint, watching")
    public Object watchAround(ProceedingJoinPoint joinPoint, Watching watching) throws Throwable {
        long maxm = watching.threshold();
        if (maxm == 0) {
            maxm = thresholdMillis;
        }
        if (maxm < 0) {
            return joinPoint.proceed();
        }

        String name = watching.value();
        if (name == null || name.isEmpty()) {
            final Signature sn = joinPoint.getSignature();
            name = sn.getDeclaringType().getSimpleName() + "#" + sn.getName();
        }

        final Watch watch = Watches.acquire(name);
        try {
            return joinPoint.proceed();
        }
        finally {
            watch.close();
            Watches.release(true, watch.getElapseMs() < thresholdMillis ? null : "WatchingAround");
        }
    }
}
