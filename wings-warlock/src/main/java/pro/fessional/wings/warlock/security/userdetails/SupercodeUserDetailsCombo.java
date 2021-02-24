package pro.fessional.wings.warlock.security.userdetails;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import pro.fessional.wings.slardar.security.impl.ComboWingsUserDetailsService;
import pro.fessional.wings.warlock.constants.WarlockOrderConst;

/**
 * JustAuth UserDetailsService，不存在用户时，自动创建
 *
 * @author trydofor
 * @since 2021-02-22
 */
@Getter
@Setter
public class SupercodeUserDetailsCombo implements ComboWingsUserDetailsService.Combo<UserDetails> {

    private int order = WarlockOrderConst.UserDetailsCombo + 10;

    @Override
    public UserDetails loadOrNull(String username, @Nullable Enum<?> authType, @Nullable Object authDetail) {
        return null;
    }
}
