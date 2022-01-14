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
     * 一次性获得所有id和权限码(scopes + '.' + action)
     *
     * @return 权限码
     */
    Map<Long, String> loadPermAll();

    @Data
    class Act {
        private String action;
        private String remark;
    }

    /**
     * 级联创建多个权限
     *
     * @param scopes 范围
     * @param acts   动作
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
     * 修改权限码备注
     *
     * @param permId id
     * @param remark 备注
     */
    void modify(long permId, @NotNull String remark);
}
