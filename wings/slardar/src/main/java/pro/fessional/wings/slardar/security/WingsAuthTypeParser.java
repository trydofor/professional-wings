package pro.fessional.wings.slardar.security;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author trydofor
 * @since 2021-02-08
 */
public interface WingsAuthTypeParser {

    /**
     * 获取通过字符串别名，解析成enum类，null返回Null.Enm
     *
     * @param authType authType
     * @return 枚举类
     */
    @NotNull
    Enum<?> parse(String authType);

    /**
     * 把enum类，变成字符串别名，无法转换时PC异常
     *
     * @param authType authType
     * @return 字符串型
     * @throws IllegalArgumentException 无法转换时
     */
    @NotNull
    String parse(Enum<?> authType);

    /**
     * 获取全部映射关系
     */
    @NotNull
    Map<String, Enum<?>> types();
}
