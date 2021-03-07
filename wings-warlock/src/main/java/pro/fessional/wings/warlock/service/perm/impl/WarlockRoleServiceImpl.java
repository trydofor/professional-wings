package pro.fessional.wings.warlock.service.perm.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jooq.Record2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pro.fessional.wings.warlock.database.autogen.tables.WinRoleEntryTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinRoleRoleMapTable;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinRoleEntryDao;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinRoleRoleMapDao;
import pro.fessional.wings.warlock.service.perm.WarlockRoleService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author trydofor
 * @since 2021-03-07
 */
@Service
@Slf4j
@CacheConfig(cacheNames = WarlockPermCacheListener.CacheName, cacheManager = WarlockPermCacheListener.ManagerName)
public class WarlockRoleServiceImpl implements WarlockRoleService {

    private static final String RoleAllSpEL = "'KeyAllRole'";
    private static final String RoleMapSpEL = "'KeyRoleMap'";

    @Setter(onMethod_ = {@Autowired})
    private WinRoleEntryDao winRoleEntryDao;
    @Setter(onMethod_ = {@Autowired})
    private WinRoleRoleMapDao winRoleRoleMapDao;

    @Override
    @Cacheable(key = RoleAllSpEL)
    public Map<Long, String> loadRoleAll() {
        final WinRoleEntryTable t = winRoleEntryDao.getTable();

        final Map<Long, String> all = winRoleEntryDao
                .ctx()
                .select(t.Id, t.Name)
                .from(t)
                .where(t.onlyLiveData)
                .fetch()
                .intoMap(Record2::value1, Record2::value2);
        log.info("loadRoleAll size={}", all.size());
        return all;
    }

    @Override
    @Cacheable(key = RoleMapSpEL)
    public Map<Long, Set<Long>> loadRoleMap() {
        final WinRoleRoleMapTable t = winRoleRoleMapDao.getTable();

        val list = winRoleEntryDao
                .ctx()
                .select(t.ReferRole, t.GrantRole)
                .from(t)
                .where(t.onlyLiveData)
                .fetch();

        log.info("loadRoleMap size={}", list.size());

        Map<Long, Set<Long>> all = new HashMap<>();
        for (Record2<Long, Long> rcd : list) {
            final Set<Long> grd = all.computeIfAbsent(rcd.value1(), k -> new HashSet<>());
            grd.add(rcd.value2());
        }

        return all;
    }

    @CacheEvict(key = RoleAllSpEL)
    public void evictRoleAllCache() {
        log.info("evictRoleAllCache");
    }

    @CacheEvict(key = RoleMapSpEL)
    public void evictRoleMapCache() {
        log.info("evictRoleMapCache");
    }
}
