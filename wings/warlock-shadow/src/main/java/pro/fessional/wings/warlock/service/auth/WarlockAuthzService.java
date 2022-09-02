package pro.fessional.wings.warlock.service.auth;

import pro.fessional.wings.slardar.security.impl.DefaultWingsUserDetails;

/**
 * 授权
 *
 * @author trydofor
 * @since 2021-02-23
 */
public interface WarlockAuthzService {

    void auth(DefaultWingsUserDetails details);
}
