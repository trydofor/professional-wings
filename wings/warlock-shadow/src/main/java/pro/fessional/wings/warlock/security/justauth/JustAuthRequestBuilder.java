package pro.fessional.wings.warlock.security.justauth;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.cache.AuthStateCache;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.config.AuthDefaultSource;
import me.zhyd.oauth.config.AuthSource;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthAlipayRequest;
import me.zhyd.oauth.request.AuthAliyunRequest;
import me.zhyd.oauth.request.AuthBaiduRequest;
import me.zhyd.oauth.request.AuthCodingRequest;
import me.zhyd.oauth.request.AuthDingTalkRequest;
import me.zhyd.oauth.request.AuthDouyinRequest;
import me.zhyd.oauth.request.AuthElemeRequest;
import me.zhyd.oauth.request.AuthFacebookRequest;
import me.zhyd.oauth.request.AuthFeishuRequest;
import me.zhyd.oauth.request.AuthGiteeRequest;
import me.zhyd.oauth.request.AuthGithubRequest;
import me.zhyd.oauth.request.AuthGitlabRequest;
import me.zhyd.oauth.request.AuthGoogleRequest;
import me.zhyd.oauth.request.AuthHuaweiRequest;
import me.zhyd.oauth.request.AuthJdRequest;
import me.zhyd.oauth.request.AuthKujialeRequest;
import me.zhyd.oauth.request.AuthLinkedinRequest;
import me.zhyd.oauth.request.AuthMeituanRequest;
import me.zhyd.oauth.request.AuthMiRequest;
import me.zhyd.oauth.request.AuthMicrosoftRequest;
import me.zhyd.oauth.request.AuthOschinaRequest;
import me.zhyd.oauth.request.AuthPinterestRequest;
import me.zhyd.oauth.request.AuthQqRequest;
import me.zhyd.oauth.request.AuthRenrenRequest;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.request.AuthStackOverflowRequest;
import me.zhyd.oauth.request.AuthTaobaoRequest;
import me.zhyd.oauth.request.AuthTeambitionRequest;
import me.zhyd.oauth.request.AuthToutiaoRequest;
import me.zhyd.oauth.request.AuthTwitterRequest;
import me.zhyd.oauth.request.AuthWeChatEnterpriseQrcodeRequest;
import me.zhyd.oauth.request.AuthWeChatEnterpriseWebRequest;
import me.zhyd.oauth.request.AuthWeChatMpRequest;
import me.zhyd.oauth.request.AuthWeChatOpenRequest;
import me.zhyd.oauth.request.AuthWeiboRequest;
import me.zhyd.oauth.request.AuthXmlyRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import pro.fessional.wings.slardar.security.WingsAuthHelper;
import pro.fessional.wings.slardar.security.impl.ComboWingsAuthDetailsSource;
import pro.fessional.wings.slardar.security.impl.DefaultWingsAuthDetails;
import pro.fessional.wings.slardar.servlet.resolver.WingsRemoteResolver;
import pro.fessional.wings.warlock.constants.WarlockOrderConst;
import pro.fessional.wings.warlock.security.session.NonceTokenSessionHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

/**
 * @author trydofor
 * @since 2021-02-17
 */
@Slf4j
@Setter @Getter
public class JustAuthRequestBuilder implements ComboWingsAuthDetailsSource.Combo<DefaultWingsAuthDetails> {

    private Map<Enum<?>, AuthConfig> authConfigMap = Collections.emptyMap();
    private AuthStateCache authStateCache;
    private AuthStateBuilder authStateBuilder;
    private WingsRemoteResolver remoteResolver;
    private int order = WarlockOrderConst.JustAuthRequestBuilder;

    @Override
    public DefaultWingsAuthDetails buildDetails(@NotNull Enum<?> authType, @NotNull HttpServletRequest request) {
        AuthRequest ar = buildRequest(authType, request);
        if (ar == null) return null;
        AuthCallback callback = new AuthCallback();

        callback.setAuth_code(request.getParameter("auth_code"));
        callback.setAuthorization_code(request.getParameter("authorization_code"));
        callback.setCode(request.getParameter("code"));
        callback.setOauth_token(request.getParameter("oauth_token"));
        callback.setOauth_verifier(request.getParameter("oauth_verifier"));
        final String state = request.getParameter("state");
        callback.setState(state);

        try {
            AuthResponse<?> response = ar.login(callback);
            final Object data = response.getData();
            if (data instanceof AuthUser) {
                final DefaultWingsAuthDetails detail = new DefaultWingsAuthDetails(data);
                final Map<String, String> meta = detail.getMetaData();
                meta.put(WingsAuthHelper.AuthZone, authStateBuilder.parseAuthZone(request));
                meta.put(WingsAuthHelper.AuthAddr, remoteResolver.resolveRemoteIp(request));
                meta.put(WingsAuthHelper.AuthAgent, remoteResolver.resolveAgentInfo(request));
                return detail;
            }
            else {
                NonceTokenSessionHelper.invalidNonce(state);
                log.warn("failed to Oauth authType={}, response type={}", authType, data == null ? "null" : data.getClass().getName());
                throw new InsufficientAuthenticationException("failed to Oauth authType=" + authType);
            }
        }
        catch (Exception e) {
            NonceTokenSessionHelper.invalidNonce(state);
            throw e;
        }
    }

    public AuthRequest buildRequest(Enum<?> authType, HttpServletRequest request) {
        if (!(authType instanceof AuthSource)) return null;

        AuthConfig config = authConfigMap.get(authType);
        if (config == null) return null;

        if (config instanceof AuthConfigWrapper) {
            config = ((AuthConfigWrapper) config).wrap(request);
        }

        switch ((AuthDefaultSource) authType) {
            case GITHUB:
                return new AuthGithubRequest(config, authStateCache);
            case WEIBO:
                return new AuthWeiboRequest(config, authStateCache);
            case GITEE:
                return new AuthGiteeRequest(config, authStateCache);
            case DINGTALK:
                return new AuthDingTalkRequest(config, authStateCache);
            case BAIDU:
                return new AuthBaiduRequest(config, authStateCache);
            case CODING:
                return new AuthCodingRequest(config, authStateCache);
            case OSCHINA:
                return new AuthOschinaRequest(config, authStateCache);
            case ALIPAY:
                return new AuthAlipayRequest(config, authStateCache);
            case QQ:
                return new AuthQqRequest(config, authStateCache);
            case WECHAT_MP:
                return new AuthWeChatMpRequest(config, authStateCache);
            case WECHAT_OPEN:
                return new AuthWeChatOpenRequest(config, authStateCache);
            case WECHAT_ENTERPRISE:
                return new AuthWeChatEnterpriseQrcodeRequest(config, authStateCache);
            case WECHAT_ENTERPRISE_WEB:
                return new AuthWeChatEnterpriseWebRequest(config, authStateCache);
            case TAOBAO:
                return new AuthTaobaoRequest(config, authStateCache);
            case GOOGLE:
                return new AuthGoogleRequest(config, authStateCache);
            case FACEBOOK:
                return new AuthFacebookRequest(config, authStateCache);
            case DOUYIN:
                return new AuthDouyinRequest(config, authStateCache);
            case LINKEDIN:
                return new AuthLinkedinRequest(config, authStateCache);
            case MICROSOFT:
                return new AuthMicrosoftRequest(config, authStateCache);
            case MI:
                return new AuthMiRequest(config, authStateCache);
            case TOUTIAO:
                return new AuthToutiaoRequest(config, authStateCache);
            case TEAMBITION:
                return new AuthTeambitionRequest(config, authStateCache);
            case RENREN:
                return new AuthRenrenRequest(config, authStateCache);
            case PINTEREST:
                return new AuthPinterestRequest(config, authStateCache);
            case STACK_OVERFLOW:
                return new AuthStackOverflowRequest(config, authStateCache);
            case HUAWEI:
                return new AuthHuaweiRequest(config, authStateCache);
            case GITLAB:
                return new AuthGitlabRequest(config, authStateCache);
            case KUJIALE:
                return new AuthKujialeRequest(config, authStateCache);
            case ELEME:
                return new AuthElemeRequest(config, authStateCache);
            case MEITUAN:
                return new AuthMeituanRequest(config, authStateCache);
            case TWITTER:
                return new AuthTwitterRequest(config, authStateCache);
            case FEISHU:
                return new AuthFeishuRequest(config, authStateCache);
            case JD:
                return new AuthJdRequest(config, authStateCache);
            case ALIYUN:
                return new AuthAliyunRequest(config, authStateCache);
            case XMLY:
                return new AuthXmlyRequest(config, authStateCache);
            default:
                return null;
        }
    }
}
