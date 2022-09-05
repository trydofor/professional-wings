package pro.fessional.wings.faceless.enums;

import org.jetbrains.annotations.NotNull;

/**
 * sys_constant_enum 常量枚举，方便编程，要与数据库一致。
 * <p>
 * SUPER，是约定值，代表本组，特征有3个
 * - 名字为SUPER
 * - id以00结尾
 * - code为id或code
 *
 * @author trydofor
 * @since 2019-09-17
 */
public interface ConstantEnum {

    /**
     * id:动态9位数起，静态8位以下；建议3-2-2分段（表-组-值）；00结尾为SUPER
     *
     * @return id
     */
    int getId();

    /**
     * enum分组:相同type为同一Enum，自动Pascal命名
     *
     * @return type
     */
    @NotNull
    default String getType() {
        return "";
    }

    @NotNull
    default String getInfo() {
        return "";
    }

    /**
     * 00 结尾的是组别
     *
     * @return 是否00结尾
     */
    default boolean isSuper() {
        return getId() % 100 == 0;
    }

    /**
     * 是否统一组别
     *
     * @param id 其他id
     * @return 是否统一组别
     */
    default boolean sameSuper(long id) {
        return id % 100 == getId() % 100;
    }

    default boolean sameSuper(ConstantEnum e) {
        return e != null && sameSuper(e.getId());
    }

    default int getSuperId() {
        return (getId() / 100) * 100;
    }

    /**
     * 是否为标致的数据库兼容的x-2-2格式
     *
     * @return 是否标准
     */
    default boolean isStandard() {
        return getId() >= 100_00_00;
    }
}
