package pro.fessional.wings.faceless.service.journal;

import javax.persistence.Column;
import java.time.LocalDateTime;

/**
 * @author trydofor
 * @since 2019-05-17
 */
public interface JournalAware {

    void setCreateDt(LocalDateTime value);

    @Column(name = "create_dt", nullable = false)
    LocalDateTime getCreateDt();

    void setModifyDt(LocalDateTime value);

    @Column(name = "modify_dt", nullable = false)
    LocalDateTime getModifyDt();

    void setCommitId(Long value);

    @Column(name = "commit_id", nullable = false)
    Long getCommitId();
}
