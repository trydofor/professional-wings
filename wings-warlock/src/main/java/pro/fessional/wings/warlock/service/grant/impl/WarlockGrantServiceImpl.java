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
import java.util.List;
import java.util.Map;

/**
 * @author trydofor
 * @since 2021-03-26
 */
@Slf4j
public class WarlockGrantServiceImpl implements WarlockGrantService {

    @Setter(onMethod_ = {@Autowired})
    private JournalService journalService;

    @Setter(onMethod_ = {@Autowired})
    private WinRoleGrantDao winRoleGrantDao;

    @Setter(onMethod_ = {@Autowired})
    private WinUserGrantDao winUserGrantDao;

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
            winRoleGrantDao.delete(cond);
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
            winUserGrantDao.delete(cond);
        });
    }

    @Override
    public Map<Long, Long> entryUser(@NotNull GrantType type, @NotNull Collection<Long> userId) {
        final WinUserGrantTable t = winUserGrantDao.getTable();
        return winUserGrantDao
                .ctx()
                .select(t.GrantEntry, t.ReferUser)
                .from(t)
                .where(t.GrantType.eq(type).and(t.ReferUser.in(userId)))
                .fetch()
                .intoMap(Record2::value1, Record2::value2);
    }

    @Override
    public Map<Long, Long> entryRole(@NotNull GrantType type, @NotNull Collection<Long> roleId) {
        final WinRoleGrantTable t = winRoleGrantDao.getTable();
        return winUserGrantDao
                .ctx()
                .select(t.GrantEntry, t.ReferRole)
                .from(t)
                .where(t.GrantType.eq(type).and(t.ReferRole.in(roleId)))
                .fetch()
                .intoMap(Record2::value1, Record2::value2);
    }
}
