package pro.fessional.wings.faceless.service.journal;

import javax.persistence.Column;
import java.time.LocalDateTime;

/**
 * @author trydofor
 * @since 2019-05-17
 */
public interface JournalAware {

    /**
     * Setter for <code>CREATE_DT</code>.
     */
    void setCreateDt(LocalDateTime value);

    /**
     * Getter for <code>CREATE_DT</code>.
     */
    @Column(name = "CREATE_DT", nullable = false)
    LocalDateTime getCreateDt();

    /**
     * Setter for <code>MODIFY_DT</code>.
     */
    void setModifyDt(LocalDateTime value);

    /**
     * Getter for <code>MODIFY_DT</code>.
     */
    @Column(name = "MODIFY_DT", nullable = false)
    LocalDateTime getModifyDt();

    /**
     * Setter for <code>COMMIT_ID</code>.
     */
    void setCommitId(Long value);

    /**
     * Getter for <code>COMMIT_ID</code>.
     */
    @Column(name = "COMMIT_ID", nullable = false)
    Long getCommitId();
}
