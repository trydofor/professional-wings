package pro.fessional.wings.slardar.security;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * 构造用于进一步验证的details
 *
 * @author trydofor
 * @see Authentication#getDetails()
 * @since 2021-02-08
 */
public interface WingsAuthDetailsSource<T> extends AuthenticationDetailsSource<HttpServletRequest, T> {

    @Override
    default T buildDetails(@NotNull HttpServletRequest request) {
        return buildDetails(null, request);
    }

    /**
     * build details, null if not accept
     *
     * @param authType supported enum
     * @param request  request
     * @return detail or null
     */
    T buildDetails(@Nullable Enum<?> authType, @NotNull HttpServletRequest request);

}
