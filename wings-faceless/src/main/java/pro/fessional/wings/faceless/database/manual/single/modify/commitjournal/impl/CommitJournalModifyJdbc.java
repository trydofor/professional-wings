package pro.fessional.wings.faceless.database.manual.single.modify.commitjournal.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.wings.faceless.database.manual.single.modify.commitjournal.CommitJournalModify;
import pro.fessional.wings.faceless.service.journal.JournalService;

import static pro.fessional.wings.faceless.sugar.funs.EmptySugar.nullToEmpty;

/**
 * @author trydofor
 * @since 2019-09-12
 */
@RequiredArgsConstructor
public class CommitJournalModifyJdbc implements CommitJournalModify {

    private final JdbcTemplate template;

    private static final String INS_SQL = "INSERT INTO sys_commit_journal (id, create_dt, event_name, target_key, login_info, other_info) VALUES (?,?,?,?,?,?)";

    @Override
    public int insert(JournalService.Journal vo) {
        return template.update(INS_SQL,
                vo.getId(),
                vo.getCreateDt(),
                nullToEmpty(vo.getEventName()),
                nullToEmpty(vo.getTargetKey()),
                nullToEmpty(vo.getLoginInfo()),
                nullToEmpty(vo.getOtherInfo()));
    }
}
