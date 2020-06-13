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

    int getId();

    @NotNull
    String getType();

    @NotNull
    String getInfo();

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

    default long getSuperId() {
        return (getId() / 100) * 100;
    }
}
