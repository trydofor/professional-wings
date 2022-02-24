package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SlardarSessionProp.Key)
public class SlardarSessionProp {

    public static final String Key = "wings.slardar.session";

    /**
     * 使用header解析session的name，空表示不解析
     *
     * @see #Key$headerName
     */
    private String headerName = "";
    public static final String Key$headerName = Key + ".header-name";

    /**
     * 使用cookie解析的cookie的name
     *
     * @see #Key$cookieName
     */
    private String cookieName = "";
    public static final String Key$cookieName = Key + ".cookie-name";

    /**
     * 是否对session进行base64编码，默认false
     *
     * @see #Key$cookieBase64
     */
    private boolean cookieBase64 = false;
    public static final String Key$cookieBase64 = Key + ".cookie-base64";

    /**
     * 增加session的jvm route，空表示否
     *
     * @see #Key$cookieRoute
     */
    private String cookieRoute = "";
    public static final String Key$cookieRoute = Key + ".cookie-route";
}
