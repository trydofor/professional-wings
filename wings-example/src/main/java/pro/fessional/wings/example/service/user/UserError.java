package pro.fessional.wings.example.service.user;

import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.data.CodeEnum;

/**
 * @author trydofor
 * @since 2020-07-02
 */
public enum UserError implements CodeEnum {

    UNKNOWN("ng.unknown", "未知错误"),
    ;


    private final String code;
    private final String hint;

    UserError(String code, String message) {
        this.code = code;
        this.hint = message;
    }

    @NotNull
    @Override
    public String getCode() {
        return code;
    }

    @NotNull
    @Override
    public String getHint() {
        return hint;
    }
}
