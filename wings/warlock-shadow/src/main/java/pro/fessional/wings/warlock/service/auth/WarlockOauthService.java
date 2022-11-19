package pro.fessional.wings.warlock.service.auth;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * @author trydofor
 * @since 2022-11-18
 */
public interface WarlockOauthService {

    String ClientId = "client_id";
    String ClientSecret = "client_secret";
    String RedirectUri = "redirect_uri";
    String Scope = "scope";
    String Code = "code";
    String State = "state";
    String Error = "error";
    String ErrorDescription = "error_description";
    String ExpireIn = "expires_in";
    String AccessToken = "access_token";

    @NotNull
    OAuth authorizeCode(@NotNull String clientId, String scope, String session);

    @NotNull
    OAuth accessToken(@NotNull String clientId, @NotNull String clientSecret, @NotNull String token);

    @NotNull
    OAuth revokeToken(@NotNull String clientId, @NotNull String token);

    /**
     * 检查 scope是否合法
     */
    default boolean checkScope(long uid, String scope) {
        return true;
    }

    @JacksonXmlRootElement(localName = "OAuth")
    class OAuth extends HashMap<String, Object> {
    }
}
