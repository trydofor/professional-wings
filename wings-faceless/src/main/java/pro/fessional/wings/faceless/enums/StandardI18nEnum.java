package pro.fessional.wings.faceless.enums;

import pro.fessional.mirana.data.CodeEnum;

/**
 * 支持ctr_standard_i18n的枚举类
 *
 * @author trydofor
 * @since 2019-09-17
 */
public interface StandardI18nEnum extends CodeEnum {
    /**
     * `base`.`kind`
     *
     * @return 前缀
     */
    String getPrefix();

    String getName();

    default String getI18nKey() {
        return getPrefix() + "." + getCode();
    }

    @Override
    default String getMessage() {
        return getName();
    }
}
