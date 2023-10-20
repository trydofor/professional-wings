package pro.fessional.wings.warlock.service.grant.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Record2;
import org.springframework.beans.factory.annotation.Autowired;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.warlock.database.autogen.tables.WinRoleGrantTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserGrantTable;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinRoleGrantDao;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinUserGrantDao;
import pro.fessional.wings.warlock.database.autogen.tables.records.WinRoleGrantRecord;
import pro.fessional.wings.warlock.database.autogen.tables.records.WinUserGrantRecord;
import pro.fessional.wings.warlock.enums.autogen.GrantType;
import pro.fessional.wings.warlock.service.grant.WarlockGrantService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author trydofor
 * @since 2021-03-26
 */
@Slf4j
public class WarlockGrantServiceImpl implements WarlockGrantService {

    @Setter(onMethod_ = {@Autowired})
    protected JournalService journalService;

    @Setter(onMethod_ = {@Autowired})
    protected WinRoleGrantDao winRoleGrantDao;

    @Setter(onMethod_ = {@Autowired})
    protected WinUserGrantDao winUserGrantDao;

    @Override
    public void grantRole(long roleId, @NotNull GrantType type, @NotNull Collection<Long> grant) {
        if (grant.isEmpty()) return;

        journalService.commit(Jane.Grant, roleId, grant, commit -> {
            List<WinRoleGrantRecord> pos = new ArrayList<>(grant.size());
            for (Long g : grant) {
                WinRoleGrantRecord rd = new WinRoleGrantRecord();
                rd.setReferRole(roleId);
                rd.setGrantType(type);
                rd.setGrantEntry(g);
                commit.create(rd);
                pos.add(rd);
            }
            winRoleGrantDao.batchInsert(pos, 100, true);
        });
    }

    @Override
    public void purgeRole(long roleId, @NotNull GrantType type, @NotNull Collection<Long> grant) {
        if (grant.isEmpty()) return;

        journalService.commit(Jane.Purge, roleId, grant, commit -> {
            final WinRoleGrantTable t = winRoleGrantDao.getTable();
            final Condition cond = t.ReferRole.eq(roleId)
                                              .and(t.GrantType.eq(type))
                                              .and(t.GrantEntry.in(grant));
            winRoleGrantDao.delete(t, cond);
        });
    }


    @Override
    public void grantUser(long userId, @NotNull GrantType type, @NotNull Collection<Long> grant) {
        if (grant.isEmpty()) return;

        journalService.commit(Jane.Grant, userId, grant, commit -> {
            List<WinUserGrantRecord> pos = new ArrayList<>(grant.size());
            for (Long g : grant) {
                WinUserGrantRecord rd = new WinUserGrantRecord();
                rd.setReferUser(userId);
                rd.setGrantType(type);
                rd.setGrantEntry(g);
                commit.create(rd);
                pos.add(rd);
            }
            winUserGrantDao.batchInsert(pos, 100, true);
        });
    }

    @Override
    public void purgeUser(long userId, @NotNull GrantType type, @NotNull Collection<Long> grant) {
        if (grant.isEmpty()) return;
        journalService.commit(Jane.Purge, userId, grant, commit -> {
            final WinUserGrantTable t = winUserGrantDao.getTable();
            final Condition cond = t.ReferUser.eq(userId)
                                              .and(t.GrantType.eq(type))
                                              .and(t.GrantEntry.in(grant));
            winUserGrantDao.delete(t, cond);
        });
    }

    @Override
    public Map<Long, Set<Long>> entryUser(@NotNull GrantType type, @NotNull Collection<Long> userId) {
        if (winUserGrantDao.notTableExist()) return Collections.emptyMap();

        final Map<Long, Set<Long>> result = new HashMap<>();
        final WinUserGrantTable t = winUserGrantDao.getTable();
        final var rs = winUserGrantDao
                .ctx()
                .select(t.GrantEntry, t.ReferUser)
                .from(t)
                .where(t.GrantType.eq(type).and(t.ReferUser.in(userId)))
                .fetch();
        for (Record2<Long, Long> r2 : rs) {
            Set<Long> set = result.computeIfAbsent(r2.value1(), (k) -> new HashSet<>());
            set.add(r2.value2());
        }

        return result;
    }

    @Override
    public Map<Long, Set<Long>> entryRole(@NotNull GrantType type, @NotNull Collection<Long> roleId) {
        if (winRoleGrantDao.notTableExist()) return Collections.emptyMap();

        final Map<Long, Set<Long>> result = new HashMap<>();
        final WinRoleGrantTable t = winRoleGrantDao.getTable();
        final var rs = winRoleGrantDao
                .ctx()
                .select(t.GrantEntry, t.ReferRole)
                .from(t)
                .where(t.GrantType.eq(type).and(t.ReferRole.in(roleId)))
                .fetch();

        for (Record2<Long, Long> r2 : rs) {
            Set<Long> set = result.computeIfAbsent(r2.value1(), (k) -> new HashSet<>());
            set.add(r2.value2());
        }

        return result;
    }
}
