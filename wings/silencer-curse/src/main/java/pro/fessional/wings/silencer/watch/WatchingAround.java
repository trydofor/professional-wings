package pro.fessional.wings.silencer.watch;

import lombok.Getter;
import lombok.Setter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import pro.fessional.mirana.time.StopWatch.Watch;
import pro.fessional.wings.silencer.spring.help.WingsBeanOrdered;

/**
 * 基于AOP，对方法进行Watching
 *
 * @author trydofor
 * @since 2022-11-21
 */
@Aspect
@Order(WingsBeanOrdered.WatchingAround)
public class WatchingAround {

    @Getter @Setter
    private long thresholdMillis = -1;

    @Around(value = "@annotation(watching)", argNames = "joinPoint, watching")
    public Object watchAround(ProceedingJoinPoint joinPoint, Watching watching) throws Throwable {
        final long maxm = Math.max(thresholdMillis, watching.threshold());
        if (maxm < 0) {
            return joinPoint.proceed();
        }

        String name = watching.value();
        if (name == null || name.isEmpty()) {
            final Signature sn = joinPoint.getSignature();
            name = sn.getDeclaringType().getSimpleName() + "#" + sn.getName();
        }

        final Watch watch = Watches.acquire().start(name);
        final Object result;
        try {
            result = joinPoint.proceed();
        }
        finally {
            watch.close();
            Watches.release(true, watch.getElapseMs() < thresholdMillis ? null : "WatchingAround");
        }

        return result;
    }
}
