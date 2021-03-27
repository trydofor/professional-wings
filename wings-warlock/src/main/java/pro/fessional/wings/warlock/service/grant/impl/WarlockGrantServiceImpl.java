package pro.fessional.wings.warlock.service.grant.impl;

import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
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
import java.util.List;
import java.util.Set;

/**
 * @author trydofor
 * @since 2021-03-26
 */
public class WarlockGrantServiceImpl implements WarlockGrantService {

    @Setter(onMethod_ = {@Autowired})
    private JournalService journalService;

    @Setter(onMethod_ = {@Autowired})
    private WinRoleGrantDao winRoleGrantDao;
    @Setter(onMethod_ = {@Autowired})
    private WinUserGrantDao winUserGrantDao;

    @Override
    public void grantRole(long refer, @NotNull GrantType type, @NotNull Set<Long> grant) {
        if (grant.isEmpty()) return;
        journalService.commit(Jane.Grant, refer, grant, commit -> {
            List<WinRoleGrantRecord> pos = new ArrayList<>(grant.size());
            for (Long g : grant) {
                WinRoleGrantRecord rd = new WinRoleGrantRecord();
                rd.setReferRole(refer);
                rd.setGrantType(type.getId());
                rd.setGrantEntry(g);
                commit.create(rd);
                pos.add(rd);
            }
            winRoleGrantDao.batchInsert(pos, 100, true);
        });
    }

    @Override
    public void purgeRole(long refer, @NotNull GrantType type, @NotNull Set<Long> grant) {
        if (grant.isEmpty()) return;
        journalService.commit(Jane.Purge, refer, grant, commit -> {
            final WinRoleGrantTable t = winRoleGrantDao.getTable();
            final Condition cond = t.ReferRole.eq(refer)
                                              .and(t.GrantType.eq(type.getId()))
                                              .and(t.GrantEntry.in(grant));
            winRoleGrantDao.delete(cond);
        });
    }


    @Override
    public void grantUser(long refer, @NotNull GrantType type, @NotNull Set<Long> grant) {
        if (grant.isEmpty()) return;
        journalService.commit(Jane.Grant, refer, grant, commit -> {
            List<WinUserGrantRecord> pos = new ArrayList<>(grant.size());
            for (Long g : grant) {
                WinUserGrantRecord rd = new WinUserGrantRecord();
                rd.setReferUser(refer);
                rd.setGrantType(type.getId());
                rd.setGrantEntry(g);
                commit.create(rd);
                pos.add(rd);
            }
            winUserGrantDao.batchInsert(pos, 100, true);
        });
    }

    @Override
    public void purgeUser(long refer, @NotNull GrantType type, @NotNull Set<Long> grant) {
        if (grant.isEmpty()) return;
        journalService.commit(Jane.Purge, refer, grant, commit -> {
            final WinUserGrantTable t = winUserGrantDao.getTable();
            final Condition cond = t.ReferUser.eq(refer)
                                              .and(t.GrantType.eq(type.getId()))
                                              .and(t.GrantEntry.in(grant));
            winUserGrantDao.delete(cond);
        });
    }
}
