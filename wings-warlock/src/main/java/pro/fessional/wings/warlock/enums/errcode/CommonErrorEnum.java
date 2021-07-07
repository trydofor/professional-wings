package pro.fessional.wings.warlock.enums.errcode;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.data.CodeEnum;

/**
 * @author trydofor
 * @since 2021-03-25
 */
@RequiredArgsConstructor
public enum CommonErrorEnum implements CodeEnum {

    AssertEmpty1("error.common.assert.empty", "{0}不能为空"),
    AssertFormat1("error.common.assert.format", "{0}格式不正确"),
    AssertNotFound1("error.common.assert.notfound", "{0}不存在"),
    AssertExisted1("error.common.assert.existed", "{0}已存在"),
    AssertState2("error.common.assert.state", "状态不正确，名字={0}，值={1}"),
    AssertParam2("error.common.assert.param", "参数不正确，名字={0}，值={1}"),

    DataNotFound("error.common.data.notfound", "数据不存在"),
    DataExisted("error.common.data.existed", "数据已存在"),

    MessageUnreadable("error.common.message.unreadable", "请求数据格式错误"),
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
