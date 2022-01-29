package pro.fessional.wings.faceless.database.sharding;

import org.apache.shardingsphere.infra.hint.HintManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * @author trydofor
 * @since 2019-08-16
 */
@Aspect
public class WriteRouteOnlyAround {

    @Around("@annotation(WriteRouteOnly)")
    public Object masterRouteOnly(ProceedingJoinPoint joinPoint) throws Throwable {
        try (HintManager hint = HintManager.getInstance()) {
            hint.setWriteRouteOnly();
            return joinPoint.proceed();
        }
    }
}
