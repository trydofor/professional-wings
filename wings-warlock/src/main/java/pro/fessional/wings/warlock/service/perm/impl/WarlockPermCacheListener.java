package pro.fessional.wings.warlock.service.perm.impl;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pro.fessional.wings.slardar.cache.WingsCache;
import pro.fessional.wings.warlock.database.autogen.tables.WinPermEntryTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinRoleEntryTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinRoleRoleMapTable;
import pro.fessional.wings.warlock.event.cache.TableChangeEvent;

/**
 * @author trydofor
 * @since 2021-03-07
 */
@Service
public class WarlockPermCacheListener {

    public static final String CacheName = WingsCache.Level.Service + "WarlockPermRoleCache";
    public static final String ManagerName = WingsCache.Manager.Memory;

    @Setter(onMethod_ = {@Autowired})
    private WarlockPermServiceImpl warlockPermServer;
    @Setter(onMethod_ = {@Autowired})
    private WarlockRoleServiceImpl warlockRoleService;


    @Async
    @EventListener
    public void cleanCache(TableChangeEvent event) {
        final Class<?> table = event.getTable();
        if (WinPermEntryTable.class.equals(table)) {
            warlockPermServer.evictPermAllCache();
        } else if (WinRoleEntryTable.class.equals(table)) {
            warlockRoleService.evictRoleAllCache();
        } else if (WinRoleRoleMapTable.class.equals(table)) {
            warlockRoleService.evictRoleMapCache();
        }
    }
}
