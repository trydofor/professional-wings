package pro.fessional.wings.faceless.database.manual.single.modify.commitjournal.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import pro.fessional.wings.faceless.database.autogen.tables.SysCommitJournalTable;
import pro.fessional.wings.faceless.database.autogen.tables.records.SysCommitJournalRecord;
import pro.fessional.wings.faceless.database.manual.single.modify.commitjournal.CommitJournalModify;
import pro.fessional.wings.faceless.service.journal.JournalService;

import static pro.fessional.wings.faceless.sugar.funs.EmptySugar.nullToEmpty;

/**
 * @author trydofor
 * @since 2019-09-12
 */
@RequiredArgsConstructor
public class CommitJournalModifyJooq implements CommitJournalModify {

    private final DSLContext dsl;

    @Override
    public int insert(JournalService.Journal vo) {
        SysCommitJournalRecord rc = dsl.newRecord(SysCommitJournalTable.SysCommitJournal);
        rc.setId(vo.getId());
        rc.setCreateDt(vo.getCommitDt());
        rc.setEventName(nullToEmpty(vo.getEventName()));
        rc.setTargetKey(nullToEmpty(vo.getTargetKey()));
        rc.setLoginInfo(nullToEmpty(vo.getLoginInfo()));
        rc.setOtherInfo(nullToEmpty(vo.getOtherInfo()));
        return rc.insert();
    }
}
