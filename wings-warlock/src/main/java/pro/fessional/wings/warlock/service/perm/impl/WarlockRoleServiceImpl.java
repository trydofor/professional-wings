package pro.fessional.wings.warlock.service.perm.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.pain.CodeException;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.warlock.database.autogen.tables.WinRoleEntryTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinRoleGrantTable;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinRoleEntryDao;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinRoleGrantDao;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.WinRoleEntry;
import pro.fessional.wings.warlock.enums.errcode.CommonErrorEnum;
import pro.fessional.wings.warlock.service.perm.WarlockRoleService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static pro.fessional.wings.warlock.service.perm.impl.WarlockPermCacheListener.KeyRoleAll;
import static pro.fessional.wings.warlock.service.perm.impl.WarlockPermCacheListener.KeyRoleGrant;

/**
 * @author trydofor
 * @since 2021-03-07
 */
@Service
@Slf4j
@CacheConfig(cacheNames = WarlockPermCacheListener.CacheName, cacheManager = WarlockPermCacheListener.ManagerName)
public class WarlockRoleServiceImpl implements WarlockRoleService {

    @Setter(onMethod_ = {@Autowired})
    private WinRoleEntryDao winRoleEntryDao;
    @Setter(onMethod_ = {@Autowired})
    private WinRoleGrantDao winRoleGrantDao;

    @Setter(onMethod_ = {@Autowired})
    private LightIdService lightIdService;
    @Setter(onMethod_ = {@Autowired})
    private JournalService journalService;

    @Override
    @Cacheable(key = KeyRoleAll)
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
    @Cacheable(key = KeyRoleGrant)
    public Map<Long, Set<Long>> loadRoleGrant() {
        final WinRoleGrantTable t = winRoleGrantDao.getTable();

        val list = winRoleEntryDao
                           .ctx()
                           .select(t.ReferRole, t.GrantEntry)
                           .from(t)
                           .where(t.GrantEntry.eq(1L))
                           .fetch();

        log.info("loadRoleMap size={}", list.size());

        Map<Long, Set<Long>> all = new HashMap<>();
        for (Record2<Long, Long> rcd : list) {
            final Set<Long> grd = all.computeIfAbsent(rcd.value1(), k -> new HashSet<>());
            grd.add(rcd.value2());
        }

        return all;
    }

    @CacheEvict(key = KeyRoleAll)
    public void evictRoleAllCache() {
        log.info("evict cache {}", KeyRoleAll);
    }

    @CacheEvict(key = KeyRoleGrant)
    public void evictRoleGrantCache() {
        log.info("evict cache {}", KeyRoleGrant);
    }


    @Override
    @CacheEvict(key = KeyRoleAll)
    public long create(@NotNull String name, String remark) {
        if (!StringUtils.hasText(name)) {
            throw new CodeException(CommonErrorEnum.AssertEmpty1, "role.name");
        }

        return journalService.submit(Jane.Create, name, remark, commit -> {
            final WinRoleEntryTable t = winRoleEntryDao.getTable();
            long id = lightIdService.getId(t);
            WinRoleEntry po = new WinRoleEntry();
            po.setId(id);
            po.setName(name.toLowerCase());
            po.setRemark(Null.notNull(remark));
            commit.create(po);

            try {
                winRoleEntryDao.insert(po);
            }
            catch (Exception e) {
                log.error("failed to insert role entry. name=" + name + ", remark=" + remark, e);
                throw new CodeException(e, CommonErrorEnum.AssertState2, "role.name", name);
            }

            return id;
        });
    }

    @Override
    public void modify(long roleId, String remark) {
        journalService.commit(Jane.Modify, roleId, remark, commit -> {
            final WinRoleEntryTable t = winRoleEntryDao.getTable();
            final int rc = winRoleEntryDao
                                   .ctx()
                                   .update(t)
                                   .set(t.CommitId, commit.getCommitId())
                                   .set(t.ModifyDt, commit.getCommitDt())
                                   .set(t.Remark, remark)
                                   .where(t.Id.eq(roleId))
                                   .execute();
            log.info("modify perm remark. roleId={}, affect={}", roleId, rc);
        });
    }
}
