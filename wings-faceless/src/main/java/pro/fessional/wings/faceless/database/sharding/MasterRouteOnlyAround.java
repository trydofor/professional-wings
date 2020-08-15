package pro.fessional.wings.faceless.database.sharding;

import org.apache.shardingsphere.api.hint.HintManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import pro.fessional.wings.faceless.database.FacelessDataSources;

/**
 * @author trydofor
 * @since 2019-08-16
 */
@Aspect
@Component
public class MasterRouteOnlyAround {

    private final boolean hasSlave;

    public MasterRouteOnlyAround(ObjectProvider<FacelessDataSources> FacelessDataSources) {
        FacelessDataSources ds = FacelessDataSources.getIfAvailable();
        hasSlave = ds != null && ds.isSplit();
    }

    @Around("@annotation(MasterRouteOnly)")
    public Object masterRouteOnly(ProceedingJoinPoint joinPoint) throws Throwable {
        if (hasSlave) {
            try (HintManager hint = HintManager.getInstance()) {
                hint.setMasterRouteOnly();
                return joinPoint.proceed();
            }
        } else {
            return joinPoint.proceed();
        }
    }
}
