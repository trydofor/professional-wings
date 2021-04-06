package pro.fessional.wings.warlock.service.perm;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

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
     * 一次性获得所有id和角色码name
     *
     * @return 角色码
     */
    Map<Long, String> loadRoleAll();


    /**
     * 一次性获得所有role之间的继承关系。
     * key拥有Set中所有role的权限
     *
     * @return 拥有关系
     */
    Map<Long, Set<Long>> loadRoleGrant();

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
