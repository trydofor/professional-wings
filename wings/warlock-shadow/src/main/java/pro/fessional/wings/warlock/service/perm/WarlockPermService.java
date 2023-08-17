package pro.fessional.wings.warlock.service.perm;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * @author trydofor
 * @since 2021-03-05
 */
public interface WarlockPermService {

    enum Jane {
        Create,
        Modify,
    }

    /**
     * Load all Perm id and code (scopes + `.` + action) on time
     *
     * @return map of id and code
     */
    Map<Long, String> loadPermAll();

    @Data
    class Act {
        private String action;
        private String remark;
    }

    /**
     * Create multiple perm at scopes from actions
     *
     * @param scopes scopes
     * @param acts   action
     */
    void create(@NotNull String scopes, @NotNull Collection<Act> acts);

    default void create(@NotNull String scopes, Act... acts) {
        create(scopes, Arrays.asList(acts));
    }

    default void create(@NotNull String scopes, @NotNull String action, @NotNull String remark) {
        Act act = new Act();
        act.setAction(action);
        act.setRemark(remark);
        create(scopes, act);
    }

    /**
     * Modify the remark/comment of Perm
     *
     * @param permId id
     * @param remark comment
     */
    void modify(long permId, @NotNull String remark);
}
