package pro.fessional.wings.warlock.caching;

import pro.fessional.wings.slardar.cache.WingsCache;

import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.singletonList;

/**
 * 统一注册和管理缓存的信息，包括类和方法
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
