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
     * @see #Key$securityConf
     */
    private boolean securityConf = true;
    public static final String Key$securityConf = Key + ".security-conf";

    /**
     * 是否支持 warlock security 默认Bean
     *
     * @see #Key$securityBean
     */
    private boolean securityBean = true;
    public static final String Key$securityBean = Key + ".security-bean";


    /**
     * 是否注入 wings的全局锁
     *
     * @see #Key$globalLock
     */
    private boolean globalLock = true;
    public static final String Key$globalLock = Key + ".global-lock";

    /**
     * 是否注入 jooq dao
     *
     * @see #Key$jooqAutogen
     */
    private boolean jooqAutogen = true;
    public static final String Key$jooqAutogen = Key + ".jooq-autogen";

    /**
     * 是否注入 ListAllLoginPageCombo
     *
     * @see #Key$comboListAllLoginPage
     */
    private boolean comboListAllLoginPage = true;
    public static final String Key$comboListAllLoginPage = Key + ".combo-list-all-login-page";

    /**
     * 是否注入 NonceUserDetailsCombo
     *
     * @see #Key$comboNonceUserDetails
     */
    private boolean comboNonceUserDetails = true;
    public static final String Key$comboNonceUserDetails = Key + ".combo-nonce-user-details";

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
     * @see #Key$comboJustAuthLoginPage
     */
    private boolean comboJustAuthLoginPage = true;
    public static final String Key$comboJustAuthLoginPage = Key + ".combo-just-auth-login-page";

    /**
     * 是否注入 JustAuthUserDetailsCombo
     *
     * @see #Key$comboJustAuthUserDetails
     */
    private boolean comboJustAuthUserDetails = true;
    public static final String Key$comboJustAuthUserDetails = Key + ".combo-just-auth-user-details";

    /**
     * 是否注入 JustAuthUserAuthnAutoReg
     *
     * @see #Key$comboJustAuthAutoreg
     */
    private boolean comboJustAuthAutoreg = true;
    public static final String Key$comboJustAuthAutoreg = Key + ".combo-just-auth-autoreg";

    /**
     * @see #Key$allExceptionHandler
     */
    private boolean allExceptionHandler = true;
    public static final String Key$allExceptionHandler = Key + ".all-exception-handler";

    /**
     * @see #Key$codeExceptionHandler
     */
    private boolean codeExceptionHandler = true;
    public static final String Key$codeExceptionHandler = Key + ".code-exception-handler";

    /**
     * 是否注入 BindExceptionAdvice
     *
     * @see #Key$bindExceptionAdvice
     */
    private boolean bindExceptionAdvice = true;
    public static final String Key$bindExceptionAdvice = Key + ".bind-exception-advice";

    /**
     * 是否检查mysql和本机timezone兼容性
     *
     * @see #Key$checkDatabase
     */
    private boolean checkDatabase = true;
    public static final String Key$checkDatabase = Key + ".check-database";

    /**
     * 是否支持为Docket全局注入AlternateTypeRule
     * @see #Key$swaggerRule
     */
    private boolean swaggerRule = true;
    public static final String Key$swaggerRule = Key + ".swagger-rule";

    /**
     * 是否支持为Docket全局注入java.time.Local*
     * @see #Key$swaggerJsr310
     */
    private boolean swaggerJsr310 = true;
    public static final String Key$swaggerJsr310 = Key + ".swagger-jsr-310";
}
