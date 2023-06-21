package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Swagger document.
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SlardarSessionProp.Key)
public class SlardarSessionProp {

    public static final String Key = "wings.slardar.session";

    /**
     * Use header (e.g. X-Auth-Token) to hold the session id. empty means disabled
     *
     * @see #Key$headerName
     */
    private String headerName = "";
    public static final String Key$headerName = Key + ".header-name";

    /**
     * Use cookie to hold the session id. empty means disabled. reuse server.servlet.session
     *
     * @see #Key$cookieName
     */
    private String cookieName = "";
    public static final String Key$cookieName = Key + ".cookie-name";

    /**
     * Whether to base64 encode the cookie, default false
     *
     * @see #Key$cookieBase64
     */
    private boolean cookieBase64 = false;
    public static final String Key$cookieBase64 = Key + ".cookie-base64";

    /**
     * Append jvm route to cookie, empty means disabled
     *
     * @see #Key$cookieRoute
     */
    private String cookieRoute = "";
    public static final String Key$cookieRoute = Key + ".cookie-route";
}
