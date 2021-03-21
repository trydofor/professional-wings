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
     * 使用cookie解析的cookie的name
     *
     * @see #Key$cookieName
     */
    private String cookieName = "";
    public static final String Key$cookieName = Key + ".cookie-name";

    /**
     * 使用header解析session的name，空表示不解析
     *
     * @see #Key$headerName
     */
    private String headerName = "";
    public static final String Key$headerName = Key + ".header-name";

}
