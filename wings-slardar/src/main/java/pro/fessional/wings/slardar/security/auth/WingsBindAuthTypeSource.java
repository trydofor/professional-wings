package pro.fessional.wings.slardar.security.auth;

import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;

/**
 * @author trydofor
 * @since 2021-02-08
 */
public interface WingsBindAuthTypeSource {
    /**
     * 获取 auth type
     *
     * @param request HttpServletRequest
     * @return auth type
     */
    @Nullable
    Enum<?> buildAuthType(HttpServletRequest request);
}
