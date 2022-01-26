package pro.fessional.wings.slardar.security;

import pro.fessional.wings.slardar.security.bind.WingsBindAuthToken;

/**
 * @author trydofor
 * @since 2022-01-18
 */
public interface WingsAuthCheckService {

    /**
     * 在用户信息及权限加载完毕后，进行后置检查。
     *
     * @param userDetails    user detail
     * @param authentication token
     * @return ok
     */
    boolean check(WingsUserDetails userDetails, WingsBindAuthToken authentication);
}
