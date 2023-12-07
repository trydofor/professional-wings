package pro.fessional.wings.warlock.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.silencer.spring.boot.WingsEnabledCondition;

/**
 * wings-enabled-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties(WarlockEnabledProp.Key)
public class WarlockEnabledProp {

    public static final String Key = WingsEnabledCondition.Prefix + ".warlock";

    /**
     * whether to enable timing watching and analysis.
     *
     * @see #Key$watching
     */
    private boolean watching = false;
    public static final String Key$watching = Key + ".watching";


    /**
     * whether to check security url conflict
     *
     * @see #Key$secCheckUrl
     */
    private boolean secCheckUrl = true;
    public static final String Key$secCheckUrl = Key + ".sec-check-url";

    /**
     * whether to enable Web auto config, eg. firewall, debug, etc.
     *
     * @see #Key$secWebAuto
     */
    private boolean secWebAuto = true;
    public static final String Key$secWebAuto = Key + ".sec-web-auto";

    /**
     * whether to support "warlock security http wing auth".
     *
     * @see #Key$secHttpAuth
     */
    private boolean secHttpAuth = true;
    public static final String Key$secHttpAuth = Key + ".sec-http-auth";

    /**
     * whether to support "warlock security http auto".
     *
     * @see #Key$secHttpAuto
     */
    private boolean secHttpAuto = true;
    public static final String Key$secHttpAuto = Key + ".sec-http-auto";

    /**
     * whether to support "warlock security http wing bind".
     *
     * @see #Key$secHttpBind
     */
    private boolean secHttpBind = true;
    public static final String Key$secHttpBind = Key + ".sec-http-bind";

    /**
     * whether to support "warlock security http base auth".
     *
     * @see #Key$secHttpBase
     */
    private boolean secHttpBase = true;
    public static final String Key$secHttpBase = Key + ".sec-http-base";

    /**
     * whether to support SecurityFilterChain.
     *
     * @see #Key$secHttpChain
     */
    private boolean secHttpChain = true;
    public static final String Key$secHttpChain = Key + ".sec-http-chain";

    /**
     * whether to enable security dummy service
     *
     * @see #Key$dummyService
     */
    private boolean dummyService = false;
    public static final String Key$dummyService = Key + ".dummy-service";

    /**
     * whether to enable the default TweakController.
     *
     * @see #Key$mvcTest
     */
    private boolean mvcTweak = false;
    public static final String Key$mvcTweak = Key + ".mvc-tweak";

    /**
     * whether to enable the default login page Controller.
     *
     * @see #Key$mvcLogin
     */
    private boolean mvcLogin = true;
    public static final String Key$mvcLogin = Key + ".mvc-login";

    /**
     * whether to enable document-only login/out proc that processed by filter.
     *
     * @see #Key$mvcProc
     */
    private boolean mvcProc = true;
    public static final String Key$mvcProc = Key + ".mvc-proc";

    /**
     * whether to enable the default OauthController.
     *
     * @see #Key$mvcOauth
     */
    private boolean mvcOauth = true;
    public static final String Key$mvcOauth = Key + ".mvc-oauth";

    /**
     * whether to enable the default mock Controller.
     *
     * @see #Key$mvcMock
     */
    private boolean mvcMock = true;
    public static final String Key$mvcMock = Key + ".mvc-mock";

    /**
     * whether to enable the default test Controller.
     *
     * @see #Key$mvcTest
     */
    private boolean mvcTest = true;
    public static final String Key$mvcTest = Key + ".mvc-test";

    /**
     * whether to enable the default user Controller.
     *
     * @see #Key$mvcUser
     */
    private boolean mvcUser = true;
    public static final String Key$mvcUser = Key + ".mvc-user";


    /**
     * whether to enable the default auth Controller.
     *
     * @see #Key$mvcAuth
     */
    private boolean mvcAuth = true;
    public static final String Key$mvcAuth = Key + ".mvc-auth";
}
