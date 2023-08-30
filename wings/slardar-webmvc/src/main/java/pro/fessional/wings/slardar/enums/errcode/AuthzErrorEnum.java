package pro.fessional.wings.slardar.enums.errcode;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.data.CodeEnum;

/**
 * spring-security-core-6.0.5.jar!/org/springframework/security/messages.properties
 *
 * @author trydofor
 * @since 2021-03-25
 */
@RequiredArgsConstructor
public enum AuthzErrorEnum implements CodeEnum {

    accessDenied("error.authz.accessDenied", "Access is denied"),
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
