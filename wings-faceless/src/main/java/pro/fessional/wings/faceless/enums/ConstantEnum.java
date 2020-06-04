package pro.fessional.wings.faceless.enums;

/**
 * 常量枚举，方便编程，要与数据库一致。
 * <p>
 * SUPER，是约定值，代表本组，特征有3个
 * - 名字为SUPER
 * - id以0000结尾
 * - type和code相同
 *
 * @author trydofor
 * @since 2019-09-17
 */
public interface ConstantEnum extends StandardI18nEnum {

    long getId();

    String getType();

    String getDesc();

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

    default long getSuper() {
        return (getId() / 100) * 100;
    }
}
