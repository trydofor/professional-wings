package pro.fessional.wings.warlock.service.perm;

import java.util.Map;
import java.util.Set;

/**
 * @author trydofor
 * @since 2021-03-05
 */
public interface WarlockRoleService {

    /**
     * 一次性获得所有id和角色码name
     *
     * @return 角色码
     */
    Map<Long, String> loadRoleAll();


    /**
     * 一次性获得所有role之间的继承关系
     *
     * @return 继承关系
     */
    Map<Long, Set<Long>> loadRoleMap();
}
