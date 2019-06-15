package pro.fessional.wings.oracle.database;

/**
 * @author trydofor
 * @since 2019-05-17
 */
public interface JournalPo {

    /**
     * Setter for <code>COMMIT_ID</code>.
     */
    void setCommitId(Long value);

    /**
     * Getter for <code>COMMIT_ID</code>.
     */
    Long getCommitId();
}
