package pro.fessional.wings.slardar.cache.spring;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.cache.interceptor.CacheOperation;
import pro.fessional.wings.slardar.cache.WingsCacheHelper;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author trydofor
 * @since 2022-04-19
 */
public class WingsCacheAnnoOprSource extends AnnotationCacheOperationSource {

    @Override
    protected Collection<CacheOperation> findCacheOperations(@NotNull Method method) {
        final Collection<CacheOperation> ops = super.findCacheOperations(method);
        WingsCacheHelper.setOperation(method, ops);
        return ops;
    }
}
