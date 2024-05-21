package pro.fessional.wings.faceless.service.journal;

import pro.fessional.wings.faceless.convention.EmptySugar;
import pro.fessional.wings.faceless.convention.EmptyValue;

import java.time.LocalDateTime;

/**
 * @author trydofor
 * @since 2019-05-17
 */
public interface JournalAware {

    default void setCreateDt(LocalDateTime value) {}

    default LocalDateTime getCreateDt() {
        return EmptyValue.DATE_TIME;
    }

    default void setModifyDt(LocalDateTime value) { }

    default LocalDateTime getModifyDt() {
        return EmptyValue.DATE_TIME;
    }

    default void setDeleteDt(LocalDateTime value) { }

    default LocalDateTime getDeleteDt() {
        return EmptyValue.DATE_TIME;
    }

    default void setCommitId(Long value) { }

    default Long getCommitId() {
        return EmptyValue.BIGINT;
    }

    default boolean isDeleted() {
        return EmptySugar.nonEmptyValue(getDeleteDt());
    }
}
