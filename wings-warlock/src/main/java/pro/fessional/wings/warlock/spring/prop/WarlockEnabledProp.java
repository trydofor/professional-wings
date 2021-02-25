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
     * @see #Key$jooqDao
     */
    private boolean jooqDao = true;
    public static final String Key$jooqDao = Key + ".jooq-dao";


    /**
     * 是否注入 justAuthLoginPageCombo
     *
     * @see #Key$comboJustAuthLoginPage
     */
    private boolean comboJustAuthLoginPage = true;
    public static final String Key$comboJustAuthLoginPage = Key + ".combo-just-auth-login-page";

    /**
     * 是否注入 ListAllLoginPageCombo
     *
     * @see #Key$comboListAllLoginPage
     */
    private boolean comboListAllLoginPage = true;
    public static final String Key$comboListAllLoginPage = Key + ".combo-list-all-login-page";


    /**
     * 是否注入 JustAuthUserDetailsCombo
     *
     * @see #Key$comboJustAuthUserDetails
     */
    private boolean comboJustAuthUserDetails = true;
    public static final String Key$comboJustAuthUserDetails = Key + ".combo-just-auth-user-details";

    /**
     * 是否注入 CommonUserDetailsCombo
     *
     * @see #Key$comboCommonUserDetails
     */
    private boolean comboCommonUserDetails = true;
    public static final String Key$comboCommonUserDetails = Key + ".combo-common-user-details";


    /**
     * 是否注入 JustAuthUserAuthnSaver
     *
     * @see #Key$saverJustAuthUserAuthn
     */
    private boolean saverJustAuthUserAuthn = true;
    public static final String Key$saverJustAuthUserAuthn = Key + ".saver-just-auth-user-authn";

}
