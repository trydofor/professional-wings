package pro.fessional.wings.faceless.database.manual.single.modify.commitjournal.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.wings.faceless.database.manual.single.modify.commitjournal.CommitJournalModify;
import pro.fessional.wings.faceless.service.journal.JournalService;

import static pro.fessional.wings.faceless.convention.EmptySugar.nullToEmpty;

/**
 * @author trydofor
 * @since 2019-09-12
 */
@RequiredArgsConstructor
public class CommitJournalModifyJdbc implements CommitJournalModify {

    private final JdbcTemplate template;

    private static final String INS_SQL = "INSERT INTO sys_commit_journal (id, create_dt, parent_id, event_name, target_key, login_info, other_info) VALUES (?,?,?,?,?,?,?)";
    private static final String ELS_SQL = "UPDATE sys_commit_journal SET elapse_ms = ? WHERE id = ?";

    @Override
    public int insert(JournalService.Journal vo) {
        return template.update(INS_SQL,
            vo.getId(),
            vo.getCommitDt(),
            vo.getParentId(),
            nullToEmpty(vo.getEventName()),
            nullToEmpty(vo.getTargetKey()),
            nullToEmpty(vo.getLoginInfo()),
            nullToEmpty(vo.getOtherInfo()));
    }

    @Override
    public int elapse(long mills, long id) {
        return template.update(ELS_SQL, mills, id);
    }
}
