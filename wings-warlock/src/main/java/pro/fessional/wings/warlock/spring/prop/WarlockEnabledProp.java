package pro.fessional.wings.warlock.spring.prop;

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
@ConfigurationProperties(WarlockEnabledProp.Key)
public class WarlockEnabledProp {

    public static final String Key = "spring.wings.warlock.enabled";

    /**
     * 是否支持 warlock security 默认配置
     *
     * @see #Key$security
     */
    private boolean security = true;
    public static final String Key$security = Key + ".security";

    /**
     * 是否支持 just auth登录
     *
     * @see #Key$justAuth
     */
    private boolean justAuth = true;
    public static final String Key$justAuth = Key + ".just-auth";

    /**
     * 是否注入 justAuthLoginPageCombo
     *
     * @see #Key$justAuthLoginPageCombo
     */
    private boolean justAuthLoginPageCombo = true;
    public static final String Key$justAuthLoginPageCombo = Key + ".just-auth-login-page-combo";

    /**
     * @see #Key$jooqDao
     */
    private boolean jooqDao = true;
    public static final String Key$jooqDao = Key + ".jooq-dao";
}
