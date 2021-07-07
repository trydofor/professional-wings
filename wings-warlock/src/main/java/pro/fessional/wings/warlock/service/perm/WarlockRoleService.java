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
     * 一次性获得所有id和角色码name，并进行变准化处理
     *
     * @return 角色码
     */
    Map<Long, String> loadRoleAll();

    /**
     * 创建role，如果同名存在则失败
     *
     * @param name   名字
     * @param remark 备注
     * @return id
     */
    long create(@NotNull String name, String remark);

    /**
     * 修改备注
     * 备注不能同时为空
     *
     * @param roleId id
     * @param remark 备注
     */
    void modify(long roleId, String remark);
}
