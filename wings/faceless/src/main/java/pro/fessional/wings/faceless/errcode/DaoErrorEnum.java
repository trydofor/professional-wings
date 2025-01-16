package pro.fessional.wings.faceless.errcode;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.i18n.CodeEnum;

/**
 * @author trydofor
 * @since 2025-01-10
 */
@Getter
@RequiredArgsConstructor
public enum DaoErrorEnum implements CodeEnum {
    AssertSelectOne("error.dao.assert.selectOne", ""),
    AssertUpdateOne("error.dao.assert.updateOne", ""),
    AssertDeleteOne("error.dao.assert.deleteOne", ""),
    ;

    private final @NotNull String code;
    private final @NotNull String hint;
}
