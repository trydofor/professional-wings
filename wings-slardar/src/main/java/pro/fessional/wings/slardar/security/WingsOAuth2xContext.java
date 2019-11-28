package pro.fessional.wings.slardar.security;

import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author trydofor
 * @since 2019-11-17
 */
public class WingsOAuth2xContext {

    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_ID_ALIAS = "client_id_alias";
    public static final String GRANT_TYPE = "grant_type";
    public static final String GRANT_TYPE_ALIAS = "grant_type_alias";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String CLIENT_SECRET_ALIAS = "client_secret_alias";
    public static final String OAUTH_PASSWORD_ALIAS = "oauth_password_alias";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String RESPONSE_TYPE = "response_type";
    public static final String SCOPE = "scope";
    public static final String STATE = "state";

    public static final ThreadLocal<Context> context = new ThreadLocal<>();

    @Nullable
    public static Context get() {
        return context.get();
    }

    public static Context set(Map<String, String> param) {
        if (param == null) return null;

        Context ctx = new Context();
        ctx.clientId = param.get(CLIENT_ID);
        ctx.clientIdAlias = param.get(CLIENT_ID_ALIAS);
        ctx.clientSecretAlias = param.get(CLIENT_SECRET_ALIAS);
        ctx.grantType = param.get(GRANT_TYPE);
        ctx.grantTypeAlias = param.get(GRANT_TYPE_ALIAS);
        ctx.oauthPasswordAlias = param.get(OAUTH_PASSWORD_ALIAS);
        ctx.redirectUri = param.get(REDIRECT_URI);
        ctx.responseType = param.get(RESPONSE_TYPE);
        ctx.scope = param.get(SCOPE);
        ctx.state = param.get(STATE);
        ctx.requestParam = param;

        context.set(ctx);
        return ctx;
    }

    public static void clear() {
        context.remove();
    }

    @Getter
    @ToString
    public static class Context {

        private String clientIdAlias;
        private String grantTypeAlias;
        private String clientSecretAlias;
        private String oauthPasswordAlias;

        private String clientId;
        private String grantType;
        private String responseType;
        private String redirectUri;
        private String scope;
        private String state;

        private Map<String, String> requestParam;
    }
}
