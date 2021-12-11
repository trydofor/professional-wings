package pro.fessional.wings.faceless.service.journal;

import lombok.Data;

/**
 * 以CommitId作为乐观锁
 *
 * @author trydofor
 * @since 2019-09-17
 */
@Data
public class JournalLock<T> {

    private final T id;
    private final Long commitId;

    // ////
    public static <U> JournalLock<U> of(U id, Long commitId) {
        return new JournalLock<>(id, commitId);
    }
}
