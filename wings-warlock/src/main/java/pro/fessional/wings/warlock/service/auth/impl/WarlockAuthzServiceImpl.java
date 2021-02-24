package pro.fessional.wings.warlock.service.auth.impl;

import org.springframework.stereotype.Service;
import pro.fessional.wings.slardar.security.impl.DefaultWingsUserDetails;
import pro.fessional.wings.warlock.service.auth.WarlockAuthzService;

/**
 * @author trydofor
 * @since 2021-02-23
 */
@Service
public class WarlockAuthzServiceImpl implements WarlockAuthzService {
    @Override
    public void auth(DefaultWingsUserDetails details) {
        if(details == null) return;
    }
}
