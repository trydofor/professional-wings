package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SlardarSwaggerProp.Key)
public class SlardarSwaggerProp {

    public static final String Key = "wings.slardar.swagger.api-info";

    /**
     * docket的标题
     *
     * @see #Key$title
     */
    private String title;
    public static final String Key$title = Key + ".title";

    /**
     * docket的描述
     *
     * @see #Key$description
     */
    private String description;
    public static final String Key$description = Key + ".description";

    /**
     * docket的版本
     *
     * @see #Key$version
     */
    private String version;
    public static final String Key$version = Key + ".version";
}
