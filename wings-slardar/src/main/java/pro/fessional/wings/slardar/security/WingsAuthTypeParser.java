package pro.fessional.wings.slardar.security;

import org.jetbrains.annotations.Nullable;

/**
 * @author trydofor
 * @since 2021-02-08
 */
public interface WingsAuthTypeParser {
    /**
     * 获取 auth type
     *
     * @param authType authType
     * @return auth type
     */
    @Nullable
    Enum<?> parse(String authType);
}
