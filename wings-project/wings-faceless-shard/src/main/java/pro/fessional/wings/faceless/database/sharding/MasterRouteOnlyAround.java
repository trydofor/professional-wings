package pro.fessional.wings.faceless.database.sharding;

import org.apache.shardingsphere.api.hint.HintManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * @author trydofor
 * @since 2019-08-16
 */
@Aspect
public class MasterRouteOnlyAround {

    @Around("@annotation(MasterRouteOnly)")
    public Object masterRouteOnly(ProceedingJoinPoint joinPoint) throws Throwable {
        try (HintManager hint = HintManager.getInstance()) {
            hint.setMasterRouteOnly();
            return joinPoint.proceed();
        }
    }
}
