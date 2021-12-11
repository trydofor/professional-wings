package pro.fessional.wings.warlock.service.perm.impl;

import pro.fessional.wings.slardar.cache.WingsCache;

/**
 * @author trydofor
 * @since 2021-03-07
 */
public class WarlockPermCacheConst {

    public static final String KeyPermAll = "KeyPermAll";
    public static final String KeyRoleAll = "KeyRoleAll";

    public static final String SpelPermAll = "'" + KeyPermAll + "'";
    public static final String SpelRoleAll = "'" + KeyRoleAll + "'";

    public static final String CacheName = WingsCache.Level.Service + "WarlockPermRoleCache";
    public static final String ManagerName = WingsCache.Manager.Memory;
}
