package pro.fessional.wings.slardar.security;

import pro.fessional.wings.slardar.security.bind.WingsBindAuthToken;

/**
 * @author trydofor
 * @since 2022-01-18
 */
public interface WingsAuthCheckService {

    /**
     * Perform a post-check after the user information and permissions are loaded.
     *
     * @param userDetails    user detail
     * @param authentication auth token
     * @return ok or not
     */
    boolean check(WingsUserDetails userDetails, WingsBindAuthToken authentication);
}
