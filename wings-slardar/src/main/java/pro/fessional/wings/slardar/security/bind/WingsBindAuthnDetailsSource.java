package pro.fessional.wings.slardar.security.bind;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationDetailsSource;

import javax.servlet.http.HttpServletRequest;

/**
 * @author trydofor
 * @since 2021-02-08
 */
public interface WingsBindAuthnDetailsSource<T> extends AuthenticationDetailsSource<HttpServletRequest, T> {

    @Override
    default T buildDetails(@NotNull HttpServletRequest request) {
        return buildDetails(null, request);
    }

    T buildDetails(@Nullable Enum<?> authType, @NotNull HttpServletRequest request);

}
