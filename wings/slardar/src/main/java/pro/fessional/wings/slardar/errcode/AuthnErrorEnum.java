package pro.fessional.wings.slardar.errcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.i18n.CodeEnum;

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

    Unauthorized("error.authn.unauthorized", "unauthorized request"),
    OnlyUserPass("error.authn.onlyUserPass", "support username password only"),
    BadCredentials("error.authn.badCredentials", "bad credentials"),
    Locked("error.authn.locked", "user account is locked"),
    Disabled("error.authn.disabled", "user account is disabled"),
    Expired("error.authn.expired", "user account has expired"),
    CredentialsExpired("error.authn.credentialsExpired", "user credentials have expired"),
    FailureWaiting1("error.authn.failureWaiting1", "bad credentials, retry after {0}s"),
    ;

    private final @NotNull String code;
    private final @NotNull String hint;
}
