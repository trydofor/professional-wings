package pro.fessional.wings.slardar.security.enums;

import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.data.CodeEnum;

/**
 * @author trydofor
 * @since 2021-02-01
 */
public enum LoginTypeEnum implements CodeEnum {
    User("200", "Password"),
    Sms("401", "Sms Code");

    private final String code;
    private final String hint;

    LoginTypeEnum(String code, String hint) {
        this.code = code;
        this.hint = hint;
    }

    @Override
    public @NotNull String getCode() {
        return code;
    }

    @Override
    public @NotNull String getHint() {
        return hint;
    }
}

