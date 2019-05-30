package pro.fessional.wings.oracle.database;

import java.time.LocalDateTime;

/**
 * @author trydofor
 * @since 2019-05-17
 */
public interface JournalPo {
    /**
     * Setter for <code>MODIFY_DT</code>.
     */
    void setModifyDt(LocalDateTime value);

    /**
     * Getter for <code>MODIFY_DT</code>.
     */
    LocalDateTime getModifyDt();

    /**
     * Setter for <code>COMMIT_ID</code>.
     */
    void setCommitId(Long value);

    /**
     * Getter for <code>COMMIT_ID</code>.
     */
    Long getCommitId();
}
