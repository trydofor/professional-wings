package pro.fessional.wings.slardar.security;

import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;

/**
 * @author trydofor
 * @since 2021-02-08
 */
public interface WingsAuthTypeSource {
    /**
     * 获取 auth type
     *
     * @param request HttpServletRequest
     * @return auth type
     */
    @NotNull
    Enum<?> buildAuthType(HttpServletRequest request);
}
