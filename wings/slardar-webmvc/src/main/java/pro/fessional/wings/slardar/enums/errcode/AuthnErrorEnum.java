package pro.fessional.wings.slardar.enums.errcode;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import pro.fessional.mirana.data.CodeEnum;

/**
 * @author trydofor
 * @see AbstractUserDetailsAuthenticationProvider
 * @since 2021-03-25
 */
@RequiredArgsConstructor
public enum AuthnErrorEnum implements CodeEnum {

    OnlySupports("AbstractUserDetailsAuthenticationProvider.onlySupports", "仅支持账号密码方式登录"),
    BadCredentials("AbstractUserDetailsAuthenticationProvider.badCredentials", "密码错误"),
    Locked("AbstractUserDetailsAuthenticationProvider.locked", "账号已锁定"),
    Disabled("AbstractUserDetailsAuthenticationProvider.disabled", "账号已禁用"),
    Expired("AbstractUserDetailsAuthenticationProvider.expired", "账号已过期"),
    CredentialsExpired("AbstractUserDetailsAuthenticationProvider.credentialsExpired", "密码已过期"),
    ;

    private final String code;
    private final String hint;

    @Override
    public @NotNull String getCode() {
        return code;
    }

    @Override
    public @NotNull String getHint() {
        return hint;
    }
}
