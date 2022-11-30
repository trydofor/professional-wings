package pro.fessional.wings.faceless.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * spring-wings-enabled-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties(FlywaveEnabledProp.Key)
public class FlywaveEnabledProp {

    public static final String Key = "spring.wings.faceless.flywave.enabled";

    /**
     * 是否注入flywave
     *
     * @see #Key$module
     * @see #Key$module
     */
    private boolean module = false;
    public static final String Key$module = Key + ".module";

    /**
     * flywave是否进行数据库的版本检查
     *
     * @see #Key$checker
     */
    private boolean checker = true;
    public static final String Key$checker = Key + ".checker";
}
