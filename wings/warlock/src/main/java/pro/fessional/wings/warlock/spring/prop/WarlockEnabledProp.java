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
     * whether to enable auto config.
     *
     * @see #Key$autoconf
     */
    private boolean autoconf = true;
    public static final String Key$autoconf = Key + ".autoconf";

    /**
     * whether to support "warlock security web and http".
     *
     * @see #Key$securityAuto
     */
    private boolean securityAuto = true;
    public static final String Key$securityAuto = Key + ".security-auto";

    /**
     * whether to enable Web auto config, eg. firewall, debug, etc.
     *
     * @see #Key$securityWebAutos
     */
    private boolean securityWebAutos = true;
    public static final String Key$securityWebAutos = Key + ".security-web-autos";

    /**
     * whether to support "warlock security http wing bind".
     *
     * @see #Key$securityHttpBind
     */
    private boolean securityHttpBind = true;
    public static final String Key$securityHttpBind = Key + ".security-http-bind";

    /**
     * whether to support "warlock security http wing auth".
     *
     * @see #Key$securityHttpAuth
     */
    private boolean securityHttpAuth = true;
    public static final String Key$securityHttpAuth = Key + ".security-http-auth";

    /**
     * whether to support "warlock security http base auth".
     *
     * @see #Key$securityHttpBase
     */
    private boolean securityHttpBase = true;
    public static final String Key$securityHttpBase = Key + ".security-http-base";

    /**
     * whether to support "warlock security http auto".
     *
     * @see #Key$securityHttpAuto
     */
    private boolean securityHttpAuto = true;
    public static final String Key$securityHttpAuto = Key + ".security-http-auto";

    /**
     * whether to support SecurityFilterChain.
     *
     * @see #Key$securityHttpChain
     */
    private boolean securityHttpChain = true;
    public static final String Key$securityHttpChain = Key + ".security-http-chain";

    /**
     * whether to support warlock security Bean.
     *
     * @see #Key$securityBean
     */
    private boolean securityBean = true;
    public static final String Key$securityBean = Key + ".security-bean";


    /**
     * whether to inject wings global lock.
     *
     * @see #Key$globalLock
     */
    private boolean globalLock = true;
    public static final String Key$globalLock = Key + ".global-lock";

    /**
     * whether to inject jooq dao.
     *
     * @see #Key$jooqAutogen
     */
    private boolean jooqAutogen = true;
    public static final String Key$jooqAutogen = Key + ".jooq-autogen";

    /**
     * whether to inject ListAllLoginPageCombo.
     *
     * @see #Key$comboListAllLoginPage
     */
    private boolean comboListAllLoginPage = true;
    public static final String Key$comboListAllLoginPage = Key + ".combo-list-all-login-page";

    /**
     * whether to inject NonceUserDetailsCombo.
     *
     * @see #Key$comboNonceUserDetails
     */
    private boolean comboNonceUserDetails = true;
    public static final String Key$comboNonceUserDetails = Key + ".combo-nonce-user-details";

    /**
     * whether to support just auth.
     *
     * @see #Key$justAuth
     */
    private boolean justAuth = true;
    public static final String Key$justAuth = Key + ".just-auth";

    /**
     * whether to inject JustAuthLoginPageCombo.
     *
     * @see #Key$comboJustAuthLoginPage
     */
    private boolean comboJustAuthLoginPage = true;
    public static final String Key$comboJustAuthLoginPage = Key + ".combo-just-auth-login-page";

    /**
     * whether to inject JustAuthUserDetailsCombo.
     *
     * @see #Key$comboJustAuthUserDetails
     */
    private boolean comboJustAuthUserDetails = true;
    public static final String Key$comboJustAuthUserDetails = Key + ".combo-just-auth-user-details";

    /**
     * whether to inject JustAuthUserAuthnAutoReg.
     *
     * @see #Key$comboJustAuthAutoreg
     */
    private boolean comboJustAuthAutoreg = true;
    public static final String Key$comboJustAuthAutoreg = Key + ".combo-just-auth-autoreg";

    /**
     * whether to support AuthZonePermChecker.
     *
     * @see #Key$zonePermCheck
     */
    private boolean zonePermCheck = false;
    public static final String Key$zonePermCheck = Key + ".zone-perm-check";

    /**
     * whether to support AuthAppPermChecker.
     *
     * @see #Key$appPermCheck
     */
    private boolean appPermCheck = false;
    public static final String Key$appPermCheck = Key + ".app-perm-check";

    /**
     * whether to inject DefaultExceptionResolver.
     *
     * @see #Key$defaultExceptionHandler
     */
    private boolean defaultExceptionHandler = true;
    public static final String Key$defaultExceptionHandler = Key + ".default-exception-handler";


    /**
     * whether to inject BindExceptionAdvice.
     *
     * @see #Key$bindExceptionAdvice
     */
    private boolean bindExceptionAdvice = true;
    public static final String Key$bindExceptionAdvice = Key + ".bind-exception-advice";

    /**
     * whether to check mysql and local timezone compatibility.
     *
     * @see #Key$checkDatabase
     */
    private boolean checkDatabase = true;
    public static final String Key$checkDatabase = Key + ".check-database";

    /**
     * whether to support global inject AlternateTypeRule into Docket.
     *
     * @see #Key$swaggerRule
     */
    private boolean swaggerRule = true;
    public static final String Key$swaggerRule = Key + ".swagger-rule";

    /**
     * whether to support global inject "java.time.Local*" into Docket.
     *
     * @see #Key$swaggerJsr310
     */
    private boolean swaggerJsr310 = true;
    public static final String Key$swaggerJsr310 = Key + ".swagger-jsr-310";

    /**
     * whether to enable table CUD listener.
     *
     * @see #Key$tableChange
     */
    private boolean tableChange = true;
    public static final String Key$tableChange = Key + ".table-change";

    /**
     * whether to enable the default auth Controller.
     *
     * @see #Key$controllerAuth
     */
    private boolean controllerAuth = true;
    public static final String Key$controllerAuth = Key + ".controller-auth";

    /**
     * whether to enable document-only login/out proc that processed by filter.
     *
     * @see #Key$controllerProc
     */
    private boolean controllerProc = true;
    public static final String Key$controllerProc = Key + ".controller-proc";

    /**
     * whether to enable the default user Controller.
     *
     * @see #Key$controllerUser
     */
    private boolean controllerUser = true;
    public static final String Key$controllerUser = Key + ".controller-user";

    /**
     * whether to enable the default mock Controller.
     *
     * @see #Key$controllerMock
     */
    private boolean controllerMock = true;
    public static final String Key$controllerMock = Key + ".controller-mock";

    /**
     * whether to enable the default test Controller.
     *
     * @see #Key$controllerTest
     */
    private boolean controllerTest = true;
    public static final String Key$controllerTest = Key + ".controller-test";

    /**
     * whether to enable the default TweakController.
     *
     * @see #Key$controllerTest
     */
    private boolean controllerTweak = false;
    public static final String Key$controllerTweak = Key + ".controller-tweak";

    /**
     * whether to enable the default OauthController.
     *
     * @see #Key$controllerOauth
     */
    private boolean controllerOauth = true;
    public static final String Key$controllerOauth = Key + ".controller-oauth";


    /**
     * whether to enable timing watching and analysis.
     *
     * @see #Key$watching
     */
    private boolean watching = false;
    public static final String Key$watching = Key + ".watching";

    /**
     * whether to support wings union login.
     *
     * @see #Key$uniauth
     */
    private boolean uniauth = false;
    public static final String Key$uniauth = Key + ".uniauth";
}
