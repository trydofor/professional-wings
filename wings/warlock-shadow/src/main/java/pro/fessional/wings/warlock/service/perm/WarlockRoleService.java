package pro.fessional.wings.warlock.service.perm;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author trydofor
 * @since 2021-03-05
 */
public interface WarlockRoleService {

    enum Jane {
        Create,
        Modify,
    }

    /**
     * Load all Role id and its Normalized code
     *
     * @return map of id and code
     */
    Map<Long, String> loadRoleAll();

    /**
     * Create role, fail if exist the same name
     *
     * @param name   name
     * @param remark comment
     * @return id
     */
    long create(@NotNull String name, String remark);

    /**
     * Modify the remark/comment of ROle
     *
     * @param roleId id
     * @param remark comment
     */
    void modify(long roleId, String remark);
}
