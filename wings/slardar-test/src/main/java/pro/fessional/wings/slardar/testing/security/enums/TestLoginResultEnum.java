package pro.fessional.wings.slardar.testing.security.enums;

import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.data.CodeEnum;

/**
 * @author trydofor
 * @since 2021-02-01
 */
public enum TestLoginResultEnum implements CodeEnum {
    Success("200", "Login Success"),
    Failure("401", "Login Failure");

    private final String code;
    private final String hint;

    TestLoginResultEnum(String code, String hint) {
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

