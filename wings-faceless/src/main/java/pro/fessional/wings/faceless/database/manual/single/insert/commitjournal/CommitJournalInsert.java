package pro.fessional.wings.faceless.database.manual.single.insert.commitjournal;

import pro.fessional.wings.faceless.service.journal.JournalService;

/**
 * @author trydofor
 * @since 2019-09-12
 */
public interface CommitJournalInsert {
    int insert(JournalService.Journal journal);
}
