package pro.fessional.wings.slardar.security.impl;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.Ordered;
import pro.fessional.wings.slardar.security.WingsAuthCheckService;
import pro.fessional.wings.slardar.security.WingsUserDetails;
import pro.fessional.wings.slardar.security.bind.WingsBindAuthToken;

import java.util.Collections;
import java.util.List;

/**
 * @author trydofor
 * @since 2022-01-18
 */
public class ComboWingsAuthCheckService implements WingsAuthCheckService {

    @Getter @Setter
    private List<Combo> combos = Collections.emptyList();

    @Override
    public boolean check(WingsUserDetails userDetails, WingsBindAuthToken authentication) {
        for (Combo combo : combos) {
            if (!combo.check(userDetails, authentication)) {
                return false;
            }
        }
        return true;
    }

    public interface Combo extends Ordered {
        boolean check(WingsUserDetails userDetails, WingsBindAuthToken authentication);
    }
}
