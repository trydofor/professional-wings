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

    AssertEmpty1("error.common.assert.empty1", "{0} should not be empty"),
    AssertFormat1("error.common.assert.format1", "invalid format for {0}"),
    AssertNotFound1("error.common.assert.notfound1", "{0} not found"),
    AssertExisted1("error.common.assert.existed1", "{0} already exists"),
    AssertState2("error.common.assert.state2", "invalid state: name={0}, value={1}"),
    AssertParam2("error.common.assert.param2", "invalid parameter: name={0}, value={1}"),
    DataNotFound("error.common.data.notfound", "data not found"),
    DataExisted("error.common.data.existed", "data already exists"),
    // HttpMessageNotReadableException
    MessageUnreadable("error.common.message.unreadable", "message not readable"),
    ;

    private final @NotNull String code;
    private final @NotNull String hint;
}
