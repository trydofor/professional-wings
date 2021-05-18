package pro.fessional.wings.warlock.service.perm.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.warlock.database.autogen.tables.WinPermEntryTable;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinPermEntryDao;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.WinPermEntry;
import pro.fessional.wings.warlock.service.perm.WarlockPermService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static pro.fessional.wings.warlock.service.grant.PermGrantHelper.unitePermit;
import static pro.fessional.wings.warlock.service.perm.impl.WarlockPermCacheListener.KeyPermAll;

/**
 * @author trydofor
 * @since 2021-03-07
 */
@Service
@Slf4j
@CacheConfig(cacheNames = WarlockPermCacheListener.CacheName, cacheManager = WarlockPermCacheListener.ManagerName)
public class WarlockPermServiceImpl implements WarlockPermService {

    @Setter(onMethod_ = {@Autowired})
    private WinPermEntryDao winPermEntryDao;

    @Setter(onMethod_ = {@Autowired})
    private LightIdService lightIdService;
    @Setter(onMethod_ = {@Autowired})
    private JournalService journalService;

    @Override
    @Cacheable(key = KeyPermAll)
    public Map<Long, String> loadPermAll() {
        final WinPermEntryTable t = winPermEntryDao.getTable();

        final Map<Long, String> all = winPermEntryDao
                                              .ctx()
                                              .select(t.Id, t.Scopes, t.Action)
                                              .from(t)
                                              .where(t.onlyLiveData)
                                              .fetch()
                                              .intoMap(Record3::value1, it -> unitePermit(it.value2(), it.value3()));
        log.info("loadPermAll size={}", all.size());
        return all;
    }

    @CacheEvict(key = KeyPermAll)
    public void evictPermAllCache() {
        log.info("evict cache {}", KeyPermAll);
    }

    @Override
    @CacheEvict(key = KeyPermAll)
    public void create(@NotNull String scopes, @NotNull Collection<Act> acts) {
        if (acts.isEmpty()) return;

        journalService.commit(Jane.Create, scopes, commit -> {
            List<WinPermEntry> pos = new ArrayList<>(acts.size());
            final WinPermEntryTable t = winPermEntryDao.getTable();
            for (Act act : acts) {
                WinPermEntry po = new WinPermEntry();
                po.setId(lightIdService.getId(t));
                po.setScopes(scopes);
                po.setAction(act.getAction());
                po.setRemark(act.getRemark());
                commit.create(po);
                pos.add(po);
            }
            log.info("insert perm scope={}, action count={}", scopes, acts.size());
            winPermEntryDao.insert(pos);
        });
    }

    @Override
    public void modify(long permId, @NotNull String remark) {
        journalService.commit(Jane.Modify, permId, commit -> {
            final WinPermEntryTable t = winPermEntryDao.getTable();
            final int rc = winPermEntryDao
                                   .ctx()
                                   .update(t)
                                   .set(t.CommitId, commit.getCommitId())
                                   .set(t.ModifyDt, commit.getCommitDt())
                                   .set(t.Remark, remark)
                                   .where(t.Id.eq(permId))
                                   .execute();
            log.info("modify perm remark. permId={}, affect={}", permId, rc);
        });
    }
}
