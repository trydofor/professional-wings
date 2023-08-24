package pro.fessional.wings.warlock.service.perm.impl;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Record3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import pro.fessional.wings.faceless.database.WingsTableCudHandler;
import pro.fessional.wings.faceless.database.WingsTableCudHandler.Cud;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.warlock.caching.CacheEventHelper;
import pro.fessional.wings.warlock.database.autogen.tables.WinPermEntryTable;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinPermEntryDao;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.WinPermEntry;
import pro.fessional.wings.warlock.event.cache.TableChangeEvent;
import pro.fessional.wings.warlock.service.perm.WarlockPermService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pro.fessional.wings.warlock.caching.CacheConst.WarlockPermService.CacheManager;
import static pro.fessional.wings.warlock.caching.CacheConst.WarlockPermService.CacheName;
import static pro.fessional.wings.warlock.caching.CacheConst.WarlockPermService.EventTables;
import static pro.fessional.wings.warlock.service.grant.PermGrantHelper.unitePermit;

/**
 * @author trydofor
 * @since 2021-03-07
 */
@Slf4j
@RequiredArgsConstructor
public class WarlockPermServiceImpl implements WarlockPermService {

    protected final Caching warlockPermServiceCaching;

    @Override
    public Map<Long, String> loadPermAll() {
        return warlockPermServiceCaching.loadPermAll();
    }

    @Override
    public void create(@NotNull String scopes, @NotNull Collection<Act> acts) {
        warlockPermServiceCaching.create(scopes, acts);
    }

    @Override
    public void modify(long permId, @NotNull String remark) {
        warlockPermServiceCaching.modify(permId, remark);
    }

    /**
     * @author trydofor
     * @since 2021-03-07
     */
    @Slf4j
    @CacheConfig(cacheNames = CacheName, cacheManager = CacheManager)
    public static class Caching {

        @Setter(onMethod_ = {@Autowired})
        protected WinPermEntryDao winPermEntryDao;

        @Setter(onMethod_ = {@Autowired})
        protected LightIdService lightIdService;

        @Setter(onMethod_ = {@Autowired})
        protected JournalService journalService;

        @Setter(onMethod_ = {@Autowired})
        protected WingsTableCudHandler wingsTableCudHandler;

        @Cacheable
        public Map<Long, String> loadPermAll() {
            if (winPermEntryDao.notTableExist()) return Collections.emptyMap();

            final WinPermEntryTable t = winPermEntryDao.getTable();

            final Map<Long, String> all = winPermEntryDao
                    .ctx()
                    .select(t.Id, t.Scopes, t.Action)
                    .from(t)
                    .where(t.getOnlyLive())
                    .fetch()
                    .intoMap(Record3::value1, it -> unitePermit(it.value2(), it.value3()));
            log.info("loadPermAll size={}", all.size());
            return all;
        }

        /**
         * Async evict all cache, event can be null
         */
        @EventListener
        @CacheEvict(allEntries = true, condition = "#result")
        public boolean evictPermAllCache(@Nullable TableChangeEvent event) {
            final String tb = CacheEventHelper.receiveTable(event, EventTables);
            if (tb != null) {
                log.info("evictPermAllCache by {}, {}", tb, event);
                return true;
            }

            return false;
        }

        public void create(@NotNull String scopes, @NotNull Collection<Act> acts) {
            if (acts.isEmpty()) return;

            final WinPermEntryTable t = winPermEntryDao.getTable();
            List<WinPermEntry> pos = new ArrayList<>(acts.size());
            journalService.commit(Jane.Create, scopes, commit -> {
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

            wingsTableCudHandler.handle(this.getClass(), Cud.Create, t, () -> {
                Map<String, List<?>> field = new HashMap<>();
                field.put(t.Id.getName(), pos.stream().map(WinPermEntry::getId).toList());
                return field;
            });
        }

        public void modify(long permId, @NotNull String remark) {
            final WinPermEntryTable t = winPermEntryDao.getTable();
            int rct = journalService.submit(Jane.Modify, permId, commit -> {
                final int rc = winPermEntryDao
                        .ctx()
                        .update(t)
                        .set(t.CommitId, commit.getCommitId())
                        .set(t.ModifyDt, commit.getCommitDt())
                        .set(t.Remark, remark)
                        .where(t.Id.eq(permId))
                        .execute();
                log.info("modify perm remark. permId={}, affect={}", permId, rc);
                return rc;
            });

            if (rct > 0) {
                wingsTableCudHandler.handle(this.getClass(), Cud.Update, t, () -> {
                    Map<String, List<?>> field = new HashMap<>();
                    field.put(t.Id.getName(), List.of(permId));
                    return field;
                });
            }
        }
    }
}
