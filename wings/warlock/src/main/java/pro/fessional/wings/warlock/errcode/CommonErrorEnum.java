package pro.fessional.wings.warlock.errcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.i18n.CodeEnum;

/**
 * @author trydofor
 * @since 2021-03-25
 */
@Getter
@RequiredArgsConstructor
public enum CommonErrorEnum implements CodeEnum {

    AssertEmpty1("error.common.assert.empty", "{0} should not empty"),
    AssertFormat1("error.common.assert.format", "{0} is bad format"),
    AssertNotFound1("error.common.assert.notfound", "{0} not found"),
    AssertExisted1("error.common.assert.existed", "{0} existed"),
    AssertState2("error.common.assert.state", "bad state, name={0}, value={1}"),
    AssertParam2("error.common.assert.param", "bad parameter, name={0}, value={1}"),

    DataNotFound("error.common.data.notfound", "data not found"),
    DataExisted("error.common.data.existed", "data existed"),

    MessageUnreadable("error.common.message.unreadable", "message not readable"),
    ;

    private final @NotNull String code;
    private final @NotNull String hint;
}
