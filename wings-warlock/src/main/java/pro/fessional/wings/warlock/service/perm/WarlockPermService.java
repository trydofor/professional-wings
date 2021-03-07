package pro.fessional.wings.warlock.service.perm;

import java.util.Map;

/**
 * @author trydofor
 * @since 2021-03-05
 */
public interface WarlockPermService {

    /**
     * 一次性获得所有id和权限码(scopes +'.'+action)
     *
     * @return 权限码
     */
    Map<Long, String> loadPermAll();
}
