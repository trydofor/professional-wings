package pro.fessional.wings.slardar.errcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.data.CodeEnum;

/**
 * org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
 * org.springframework.security.authentication.AccountStatusUserDetailsChecker
 *
 * @author trydofor
 * @since 2021-03-25
 */
@Getter
@RequiredArgsConstructor
public enum AuthnErrorEnum implements CodeEnum {

    Unauthorized("error.authn.unauthorized", "Unauthorized request"),
    OnlyUserPass("error.authn.onlyUserPass", "Support username password only"),
    BadCredentials("error.authn.badCredentials", "Bad credentials"),
    Locked("error.authn.locked", "User account is locked"),
    Disabled("error.authn.disabled", "User account is disabled"),
    Expired("error.authn.expired", "User account has expired"),
    CredentialsExpired("error.authn.credentialsExpired", "User credentials have expired"),
    FailureWaiting("error.authn.failureWaiting", "Bad credentials, retry after {0}s"),
    ;

    private final @NotNull String code;
    private final @NotNull String hint;
}
