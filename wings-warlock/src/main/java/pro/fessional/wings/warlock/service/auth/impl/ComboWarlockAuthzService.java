package pro.fessional.wings.warlock.service.auth.impl;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;
import pro.fessional.wings.slardar.security.impl.DefaultWingsUserDetails;
import pro.fessional.wings.warlock.service.auth.WarlockAuthzService;

import java.util.Collections;
import java.util.List;

/**
 * @author trydofor
 * @since 2021-02-23
 */
@Service
@Slf4j
public class ComboWarlockAuthzService implements WarlockAuthzService {

    private List<Combo> authCombos = Collections.emptyList();

    @Autowired(required = false)
    public void setAuthCombos(List<Combo> authCombos) {
        log.info("inject auth combo, count={}", authCombos.size());
        this.authCombos = authCombos;
    }

    @Override
    public void auth(DefaultWingsUserDetails details) {
        if (details == null) return;
        for (Combo combo : authCombos) {
            combo.auth(details);
        }
    }

    // /////
    public interface Combo extends Ordered {
        void auth(@NotNull DefaultWingsUserDetails details);
    }
}
