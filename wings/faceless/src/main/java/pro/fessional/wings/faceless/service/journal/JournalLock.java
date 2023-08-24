package pro.fessional.wings.faceless.service.journal;

import lombok.Data;

/**
 * Use CommitId as optimistic lock
 *
 * @author trydofor
 * @since 2019-09-17
 */
@Data
public class JournalLock<T> {

    private final Long commitId;
    private final T id;

    // ////
    public static <U> JournalLock<U> of(long commitId, U id) {
        return new JournalLock<>(commitId, id);
    }
}
