package pro.fessional.wings.warlock.caching;

import pro.fessional.wings.slardar.cache.WingsCache;

import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.singletonList;

/**
 * Unified registration and management of cached information, including classes and methods
 *
 * @author trydofor
 * @since 2022-04-20
 */
public interface CacheConst {

    interface RuntimeConfService {
        String CacheName = WingsCache.Level.Service + "RuntimeConfService" + WingsCache.Extend;
        String CacheManager = WingsCache.Manager.Memory;
        String CacheResolver = WingsCache.Resolver.Memory;
        Set<String> EventTables = new HashSet<>(singletonList("win_conf_runtime"));
    }

    interface WarlockPermService {
        String CacheName = WingsCache.Level.Service + "WarlockPermService" + WingsCache.Extend;
        String CacheManager = WingsCache.Manager.Memory;
        String CacheResolver = WingsCache.Resolver.Memory;
        Set<String> EventTables = new HashSet<>();
    }

    interface WarlockRoleService {
        String CacheName = WingsCache.Level.Service + "WarlockRoleService" + WingsCache.Extend;
        String CacheManager = WingsCache.Manager.Memory;
        String CacheResolver = WingsCache.Resolver.Memory;
        Set<String> EventTables = new HashSet<>();
    }
}
