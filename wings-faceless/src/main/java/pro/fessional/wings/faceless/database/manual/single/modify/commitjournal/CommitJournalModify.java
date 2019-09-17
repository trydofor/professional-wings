package pro.fessional.wings.faceless.database.manual.single.modify.commitjournal;

import pro.fessional.wings.faceless.service.journal.JournalService;

/**
 * @author trydofor
 * @since 2019-09-12
 */
public interface CommitJournalModify {
    int insert(JournalService.Journal journal);
}
