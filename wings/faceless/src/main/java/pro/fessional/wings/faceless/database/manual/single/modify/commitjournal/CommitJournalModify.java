package pro.fessional.wings.faceless.database.manual.single.modify.commitjournal;

import pro.fessional.wings.faceless.service.journal.JournalService;

/**
 * @author trydofor
 * @since 2019-09-12
 */
public interface CommitJournalModify {

    /**
     * insert new journal
     */
    int insert(JournalService.Journal journal);

    /**
     * update elapse mills by id
     */
    int elapse(long mills, long id);
}
