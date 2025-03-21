package pro.fessional.wings.warlock.security.justauth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.cache.AuthStateCache;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.config.AuthDefaultSource;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import pro.fessional.mirana.best.AssertArgs;
import pro.fessional.mirana.flow.FlowEnum;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.slardar.security.impl.ComboWingsAuthDetailsSource;
import pro.fessional.wings.slardar.security.impl.DefaultWingsAuthDetails;
import pro.fessional.wings.warlock.errcode.CommonErrorEnum;
import pro.fessional.wings.warlock.security.session.NonceTokenSessionHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author trydofor
 * @since 2021-02-17
 */
@Slf4j
@Getter
public class JustAuthRequestBuilder implements ComboWingsAuthDetailsSource.Combo<DefaultWingsAuthDetails> {

    public static final int ORDER = WingsOrdered.Lv4Application;
    @Setter
    private int order = ORDER;
    @Setter
    private Map<Enum<?>, AuthConfig> authConfigMap = Collections.emptyMap();

    @Setter(onMethod_ = { @Autowired })
    private AuthStateCache authStateCache;
    @Setter(onMethod_ = { @Autowired, @Lazy })
    private List<SuccessHandler> successHandlers = new ArrayList<>();

    @Override
    public DefaultWingsAuthDetails buildDetails(@NotNull Enum<?> authType, @NotNull HttpServletRequest request) {
        AuthRequest ar = buildRequest(authType, request);
        if (ar == null) return null;
        final String state = request.getParameter("state");
        AssertArgs.notEmpty(state, "state", CommonErrorEnum.AssertNotFound1, "state");

        AuthCallback callback = new AuthCallback();
        callback.setAuth_code(request.getParameter("auth_code"));
        callback.setAuthorization_code(request.getParameter("authorization_code"));
        callback.setCode(request.getParameter("code"));
        callback.setOauth_token(request.getParameter("oauth_token"));
        callback.setOauth_verifier(request.getParameter("oauth_verifier"));
        callback.setState(state);

        try {
            AuthResponse<?> response = ar.login(callback);
            final Object data = response.getData();
            if (data instanceof AuthUser authUser) {
                final DefaultWingsAuthDetails detail = new DefaultWingsAuthDetails(data);
                for (SuccessHandler hdl : successHandlers) {
                    final FlowEnum flw = hdl.handle(authType, request, authUser, detail);
                    if (flw == FlowEnum.Break) {
                        break;
                    }
                    else if (flw == FlowEnum.Return) {
                        return detail;
                    }
                    else if (flw == FlowEnum.Throw) {
                        throw new InternalAuthenticationServiceException(hdl.getClass().getName() + " want throw");
                    }
                }
                return detail;
            }
            else {
                log.warn("failed to Oauth authType={}, response type={}", authType, data == null ? "null" : data.getClass().getName());
                throw new InsufficientAuthenticationException("failed to Oauth authType=" + authType);
            }
        }
        catch (InternalAuthenticationServiceException e) {
            throw e;
        }
        catch (Exception e) {
            NonceTokenSessionHelper.invalidNonce(state);
            throw e;
        }
    }

    /**
     * @see me.zhyd.oauth.AuthRequestBuilder
     * @return  null if not support
     */
    @Nullable
    public AuthRequest buildRequest(Enum<?> authType, HttpServletRequest request) {
        AuthConfig config = buildConfig(authType, request);
        if (config == null) return null;

        if (!(authType instanceof AuthDefaultSource justAuthType)) return null;
        var targetClass = justAuthType.getTargetClass();
        if (null == targetClass) return null;

        try {
            return targetClass.getDeclaredConstructor(AuthConfig.class, AuthStateCache.class)
                       .newInstance(config, authStateCache);
        }
        catch (Exception e) {
            log.error("failed to build auth request, type=" + authType, e);
            return null;
        }
    }

    @Nullable
    protected AuthConfig buildConfig(Enum<?> authType, HttpServletRequest request){
        AuthConfig config = authConfigMap.get(authType);
        if (config instanceof AuthConfigWrapper) {
            config = ((AuthConfigWrapper) config).wrap(request);
        }
        return config;
    }

    public interface SuccessHandler extends Ordered {
        @Override
        default int getOrder() {
            return Ordered.LOWEST_PRECEDENCE;
        }

        /**
         * handle AuthUser to set detail.
         *
         * @throws InternalAuthenticationServiceException will not NonceTokenSessionHelper.invalidNonce
         */
        FlowEnum handle(@NotNull Enum<?> authType, @NotNull HttpServletRequest request, @NotNull AuthUser authUser, @NotNull DefaultWingsAuthDetails detail);
    }
}
