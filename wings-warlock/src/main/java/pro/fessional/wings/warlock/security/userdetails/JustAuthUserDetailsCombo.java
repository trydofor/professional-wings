package pro.fessional.wings.warlock.security.userdetails;

import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthSource;

/**
 * JustAuth UserDetailsService，不存在用户时，自动创建
 *
 * @author trydofor
 * @since 2021-02-22
 */
@Slf4j
public class JustAuthUserDetailsCombo extends CommonUserDetailsCombo {

    public static final int ORDER = CommonUserDetailsCombo.ORDER - 10;

    public JustAuthUserDetailsCombo() {
        setOrder(ORDER);
    }

    @Override
    protected boolean accept(Enum<?> authType) {
        return authType instanceof AuthSource;
    }

    @Override
    protected boolean authed(Enum<?> authType) {
        return true;
    }
}
