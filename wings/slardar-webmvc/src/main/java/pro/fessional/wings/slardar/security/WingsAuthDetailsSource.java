package pro.fessional.wings.slardar.security;

import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import pro.fessional.mirana.data.Null;

/**
 * Construct details for further validation
 *
 * @author trydofor
 * @see Authentication#getDetails()
 * @see AbstractAuthenticationProcessingFilter#setAuthenticationDetailsSource(AuthenticationDetailsSource)
 * @since 2021-02-08
 */
public interface WingsAuthDetailsSource<T extends WingsAuthDetails> extends AuthenticationDetailsSource<HttpServletRequest, T> {

    @Override
    default T buildDetails(@NotNull HttpServletRequest request) {
        return buildDetails(Null.Enm, request);
    }

    /**
     * build details, null if not accept
     *
     * @param authType supported enum
     * @param request  request
     * @return detail or null
     */
    T buildDetails(@NotNull Enum<?> authType, @NotNull HttpServletRequest request);
}
