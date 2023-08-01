package pro.fessional.wings.faceless.enums;

import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.data.CodeEnum;

/**
 * Enum from sys_standard_i18n table.
 *
 * @author trydofor
 * @since 2019-09-17
 */
public interface StandardI18nEnum extends CodeEnum {

    @NotNull
    String getBase();

    @NotNull
    String getKind();

    @NotNull
    String getUkey();

    @Override
    @NotNull
    default String getI18nCode() {
        return getBase() + "." + getKind() + "." + getUkey();
    }
}
