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

    /**
     * Need to check scope, redirectUri and session (3rd user's sessionId)
     */
    @NotNull
    OAuth authorizeCode(@NotNull String clientId, String scope, String redirectUri, String session);

    /**
     * client_credentials if token is empty, otherwise authorization_code
     */
    @NotNull
    OAuth accessToken(@NotNull String clientId, @NotNull String clientSecret, String token);

    @NotNull
    OAuth revokeToken(@NotNull String clientId, @NotNull String token);

    @JacksonXmlRootElement(localName = "OAuth")
    class OAuth extends HashMap<String, Object> {
    }
}
