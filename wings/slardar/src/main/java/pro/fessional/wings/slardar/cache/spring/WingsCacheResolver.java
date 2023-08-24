package pro.fessional.wings.slardar.cache.spring;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.AbstractCacheResolver;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.SimpleCacheResolver;
import pro.fessional.wings.slardar.cache.WingsCache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Support for auto-expansion to implementation class
 *
 * @author trydofor
 * @see SimpleCacheResolver
 * @since 2023-01-28
 */
@RequiredArgsConstructor
public class WingsCacheResolver extends AbstractCacheResolver {

    public WingsCacheResolver(CacheManager cacheManager) {
        super(cacheManager);
    }

    @Override
    protected Collection<String> getCacheNames(@NotNull CacheOperationInvocationContext<?> context) {
        final Set<String> names = context.getOperation().getCacheNames();

        final ArrayList<String> temp = new ArrayList<>(names.size());
        boolean got = false;
        for (String name : names) {
            if (name.endsWith(WingsCache.Extend)) {
                final String impl = context.getTarget().getClass().getName();
                temp.add(name + impl);
                got = true;
            }
            else {
                temp.add(name);
            }
        }

        return got ? temp : names;
    }
}
