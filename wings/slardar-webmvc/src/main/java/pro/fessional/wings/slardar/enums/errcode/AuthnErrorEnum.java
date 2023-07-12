package pro.fessional.wings.slardar.enums.errcode;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import pro.fessional.mirana.data.CodeEnum;

/**
 * @author trydofor
 * @see AbstractUserDetailsAuthenticationProvider
 * @see AccountStatusUserDetailsChecker
 * @since 2021-03-25
 */
@RequiredArgsConstructor
public enum AuthnErrorEnum implements CodeEnum {

    OnlySupports("error.authn.onlySupports", "仅支持账号密码方式登录"),
    BadCredentials("error.authn.badCredentials", "密码错误"),
    Locked("error.authn.locked", "账号已锁定"),
    Disabled("error.authn.disabled", "账号已禁用"),
    Expired("error.authn.expired", "账号已过期"),
    CredentialsExpired("error.authn.credentialsExpired", "密码已过期"),
    FailureWaiting("error.authn.failureWaiting", "密码错误，请{0}秒后重试"),
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
