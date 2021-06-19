package pro.fessional.wings.warlock.service.perm.impl;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pro.fessional.wings.slardar.cache.WingsCache;
import pro.fessional.wings.warlock.event.cache.TableChangeEvent;

import static pro.fessional.wings.warlock.database.autogen.tables.WinPermEntryTable.WinPermEntry;
import static pro.fessional.wings.warlock.database.autogen.tables.WinRoleEntryTable.WinRoleEntry;

/**
 * @author trydofor
 * @since 2021-03-07
 */
@Service
public class WarlockPermCacheListener {

    public static final String KeyPermAll = "'KeyPermAll'";
    public static final String KeyRoleAll = "'KeyRoleAll'";
    public static final String KeyRoleGrant = "'KeyRoleGrant'";

    public static final String CacheName = WingsCache.Level.Service + "WarlockPermRoleCache";
    public static final String ManagerName = WingsCache.Manager.Memory;

    @Setter(onMethod_ = {@Autowired})
    private WarlockPermServiceImpl warlockPermServer;
    @Setter(onMethod_ = {@Autowired})
    private WarlockRoleServiceImpl warlockRoleService;

    @Async
    @EventListener
    public void cleanCache(TableChangeEvent event) {
        final String table = event.getTable();

        if (WinPermEntry.getName().equalsIgnoreCase(table)) {
            warlockPermServer.evictPermAllCache();
        }
        else if (WinRoleEntry.getName().equalsIgnoreCase(table)) {
            warlockRoleService.evictRoleAllCache();
        }
    }
}
