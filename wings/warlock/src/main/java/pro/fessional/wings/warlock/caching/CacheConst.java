package pro.fessional.wings.warlock.caching;

import pro.fessional.wings.slardar.cache.WingsCache;
import pro.fessional.wings.warlock.database.autogen.tables.WinConfRuntimeTable;

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
        String CacheName = WingsCache.Level.Service + "RuntimeConfService";
        String CacheManager = WingsCache.Manager.Memory;
        Set<String> EventTables = new HashSet<>(singletonList(WinConfRuntimeTable.WinConfRuntime.getName()));
    }

    interface WarlockAuthnService {
        String CacheName = WingsCache.Level.Service + "WarlockAuthnService";
        String CacheManager = WingsCache.Manager.Memory;
        Set<String> EventTables = new HashSet<>();
    }

    interface WarlockPermService {
        String CacheName = WingsCache.Level.Service + "WarlockPermService";
        String CacheManager = WingsCache.Manager.Memory;
        Set<String> EventTables = new HashSet<>();
    }

    interface WarlockRoleService {
        String CacheName = WingsCache.Level.Service + "WarlockRoleService";
        String CacheManager = WingsCache.Manager.Memory;
        Set<String> EventTables = new HashSet<>();
    }
}
