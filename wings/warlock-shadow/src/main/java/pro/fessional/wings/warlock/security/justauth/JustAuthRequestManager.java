package pro.fessional.wings.warlock.security.justauth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.cache.AuthStateCache;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.config.AuthSource;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import pro.fessional.mirana.best.AssertArgs;
import pro.fessional.mirana.flow.FlowEnum;
import pro.fessional.mirana.text.StringTemplate;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.slardar.security.WingsAuthHelper;
import pro.fessional.wings.slardar.security.impl.ComboWingsAuthDetailsSource;
import pro.fessional.wings.slardar.security.impl.DefaultWingsAuthDetails;
import pro.fessional.wings.warlock.errcode.CommonErrorEnum;
import pro.fessional.wings.warlock.security.session.NonceTokenSessionHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 * if accept() then
 * (0) buildRequest()
 * (1) authorize()
 * (2) login()
 * </pre>
 *
 * @author trydofor
 * @since 2021-02-17
 */
@Slf4j
@Getter
public class JustAuthRequestManager implements ComboWingsAuthDetailsSource.Combo<DefaultWingsAuthDetails> {

    public static final int ORDER = WingsOrdered.Lv4Application;
    @Setter
    private int order = ORDER;
    @Setter
    private Set<String> safeRedirectHost = Collections.emptySet();
    @Setter
    private Map<Enum<?>, AuthConfig> authConfigMap = Collections.emptyMap();

    @Setter(onMethod_ = { @Autowired })
    protected AuthStateCache authStateCache;
    @Setter(onMethod_ = { @Autowired, @Lazy })
    protected List<SuccessHandler> successHandlers = new ArrayList<>();

    public boolean accept(@NotNull Enum<?> authType) {
        return authType instanceof AuthSource && authConfigMap.containsKey(authType);
    }

    /**
     * should check accept() first
     */
    @NotNull
    public String authorize(@NotNull Enum<?> authType, @NotNull HttpServletRequest request, @NotNull String state) {
        AuthRequest authRequest = buildRequest(authType, request, state);
        return authRequest.authorize(state);
    }

    /**
     * should check accept() first
     */
    @NotNull
    public AuthResponse<AuthUser> login(@NotNull Enum<?> authType, @NotNull HttpServletRequest request) {
        AuthCallback callback = new AuthCallback();
        callback.setAuth_code(request.getParameter("auth_code"));
        callback.setAuthorization_code(request.getParameter("authorization_code"));
        callback.setCode(request.getParameter("code"));
        callback.setOauth_token(request.getParameter("oauth_token"));
        callback.setOauth_verifier(request.getParameter("oauth_verifier"));
        callback.setState(request.getParameter("state"));

        AuthRequest authRequest = buildRequest(authType, request, callback.getState());
        return authRequest.login(callback);
    }

    @Override
    public DefaultWingsAuthDetails buildDetails(@NotNull Enum<?> authType, @NotNull HttpServletRequest request) {
        if (!accept(authType)) return null;
        try {
            AuthResponse<AuthUser> response = login(authType, request);
            final AuthUser authUser = response.getData();
            final DefaultWingsAuthDetails detail = new DefaultWingsAuthDetails(authUser);
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
        catch (InternalAuthenticationServiceException e) {
            throw e;
        }
        catch (Exception e) {
            NonceTokenSessionHelper.invalidNonce(request.getParameter("state"));
            throw e;
        }
    }

    /**
     * should check accept() first, and there are
     * two seperated request: .authorize() and .login()
     * and apple.login() change the AuthConf.clientSecret
     *
     * @see me.zhyd.oauth.AuthRequestBuilder
     * @see me.zhyd.oauth.request.AuthAppleRequest
     */
    @SneakyThrows
    @NotNull
    public AuthRequest buildRequest(@NotNull Enum<?> authType, @NotNull HttpServletRequest request, @NotNull String state) {
        AssertArgs.notEmpty(state, "state", CommonErrorEnum.AssertEmpty1, "state");

        AuthConfig config = $$.of(authConfigMap.get(authType));
        buildRedirectUri(config, request, state);

        var targetClass = ((AuthSource) authType).getTargetClass();
        return targetClass.getDeclaredConstructor(AuthConfig.class, AuthStateCache.class)
                          .newInstance(config, authStateCache);
    }

    /**
     * auto generated by `wgme` live template
     */
    @Mapper
    public interface $$ {

        void map(AuthConfig a, @MappingTarget AuthConfig b);

        $$ INSTANCE = Mappers.getMapper($$.class);

        @NotNull
        static AuthConfig of(@Nullable AuthConfig source) {
            final AuthConfig target = new AuthConfig();
            INSTANCE.map(source, target);
            return target;
        }

        static void to(@Nullable AuthConfig source, @NotNull AuthConfig target) {
            INSTANCE.map(source, target);
        }
    }

    public static final String RedirectUriScheme = "{scheme}";
    public static final String RedirectUriHost = "{host}";
    public static final String RedirectUriAuthType = "{" + WingsAuthHelper.AuthType + "}";
    public static final String RedirectUriAuthZone = "{" + WingsAuthHelper.AuthZone + "}";

    protected void buildRedirectUri(AuthConfig config, HttpServletRequest request, String state) {
        final String uri = config.getRedirectUri();
        if (!(uri.contains(RedirectUriScheme) || uri.contains(RedirectUriHost) || uri.contains(RedirectUriAuthType) || uri.contains(RedirectUriAuthZone))) {
            return;
        }

        final String key = RedirectUriHost.concat(state);
        String cached = authStateCache.get(key);
        if(cached != null){
            config.setRedirectUri(cached);
            return;
        }

        String host = request.getHeader("Host");
        if (!safeRedirectHost.isEmpty()) {
            final String hst = request.getParameter("host");
            if (hst != null && safeRedirectHost.contains(hst)) {
                host = hst;
            }
        }

        String redirectUri = StringTemplate
            .dyn(uri)
            .bindStr(RedirectUriHost, host)
            .bindStr(RedirectUriScheme, request.getScheme())
            .bindStr(RedirectUriAuthType, request.getParameter(WingsAuthHelper.AuthType))
            .bindStr(RedirectUriAuthZone, request.getParameter(WingsAuthHelper.AuthZone))
            .toString();

        config.setRedirectUri(redirectUri);
        authStateCache.cache(key, redirectUri);
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
