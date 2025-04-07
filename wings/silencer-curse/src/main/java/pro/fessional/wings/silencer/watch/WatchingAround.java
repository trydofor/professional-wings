package pro.fessional.wings.silencer.watch;

import lombok.Getter;
import lombok.Setter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.util.AntPathMatcher;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.silencer.tweak.AntMatcherMap;

import java.util.Map;

/**
 * AOP-based, stopwatch timing of methods
 *
 * @author trydofor
 * @since 2022-11-21
 */
@Aspect
@Order(WingsOrdered.Lv5Supervisor)

public class WatchingAround {

    @Getter @Setter
    private volatile long thresholdMillis = -1;

    private final AntMatcherMap<Long> antThreshold = new AntMatcherMap<>(new AntPathMatcher("."));

    public void setThresholdName(Map<String, Long> thresholds) {
        antThreshold.putAll(thresholds);
    }

    public void setThresholdName(String name, long ms) {
        antThreshold.put(name, ms);
    }

    @Around(value = "@annotation(watching)", argNames = "joinPoint, watching")
    public Object watchAround(ProceedingJoinPoint joinPoint, Watching watching) throws Throwable {
        long maxMs = watching.threshold();
        if (maxMs == 0) {
            maxMs = thresholdMillis;
        }
        if (maxMs < 0) {
            return joinPoint.proceed();
        }

        String name = watching.value();
        if (name == null || name.isEmpty()) {
            final Signature sn = joinPoint.getSignature();
            name = sn.getDeclaringType().getSimpleName() + "#" + sn.getName();
        }

        // key threshold over ant matcher
        final Long keyMs = antThreshold.get(name);
        if (keyMs != null) {
            if (keyMs < 0) {
                return joinPoint.proceed();
            }
            else {
                maxMs = keyMs;
            }
        }

        final Watches.Threshold threshold = Watches.threshold(name, maxMs);
        try {
            return joinPoint.proceed();
        }
        finally {
            boolean slow = threshold.reach();
            Watches.release(true, slow ? "WatchingAround" : null);
        }
    }
}
