package pro.fessional.wings.slardar.errcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.data.CodeEnum;

/**
 * spring-security-core-6.0.5.jar!/org/springframework/security/messages.properties
 *
 * @author trydofor
 * @since 2021-03-25
 */
@Getter
@RequiredArgsConstructor
public enum AuthzErrorEnum implements CodeEnum {

    AccessDenied("error.authz.accessDenied", "Access is denied"),
    ;

    private final @NotNull String code;
    private final @NotNull String hint;
}
