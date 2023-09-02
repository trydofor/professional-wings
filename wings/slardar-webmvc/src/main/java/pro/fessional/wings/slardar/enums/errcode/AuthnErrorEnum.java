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

    OnlyUserPass("error.authn.onlyUserPass", "Support username password only"),
    BadCredentials("error.authn.badCredentials", "Bad credentials"),
    Locked("error.authn.locked", "User account is locked"),
    Disabled("error.authn.disabled", "User account is disabled"),
    Expired("error.authn.expired", "User account has expired"),
    CredentialsExpired("error.authn.credentialsExpired", "User credentials have expired"),
    FailureWaiting("error.authn.failureWaiting", "Bad credentials, retry after {0}s"),
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
