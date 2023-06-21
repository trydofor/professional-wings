package pro.fessional.wings.warlock.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * wings-warlock-urlmap-77.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-10-09
 */
@Data
@ConfigurationProperties(WarlockUrlmapProp.Key)
public class WarlockUrlmapProp {

    public static final String Key = "wings.warlock.urlmap";

    /**
     * integrated login default page,
     * list supported auth-type by default, requires PathVar `{extName}`
     *
     * @see #Key$authLoginList
     */
    private String authLoginList = "";
    public static final String Key$authLoginList = Key + ".auth-login-list";

    /**
     * specific auth-type login default page,
     * automatically response based on content-type, requires PathVar `{extName}` `{authType}`
     *
     * @see #Key$authLoginPage
     */
    private String authLoginPage = "";
    public static final String Key$authLoginPage = Key + ".auth-login-page";

    /**
     * specific auth-type login default page, get authType by RequestParam
     *
     * @see #Key$authLoginPage
     */
    private String authLoginPage2 = "";
    public static final String Key$authLoginPage2 = Key + ".auth-login-page2";

    /**
     * to verify the one-time token is valid, oauth2 uses state as token
     *
     * @see #Key$authNonceCheck
     */
    private String authNonceCheck = "";
    public static final String Key$authNonceCheck = Key + ".auth-nonce-check";

    /**
     * simple authorization code.
     *
     * @see #Key$oauthAuthorize
     */
    private String oauthAuthorize = "";
    public static final String Key$oauthAuthorize = Key + ".oauth-authorize";

    /**
     * simple get access-token.
     *
     * @see #Key$oauthAccessToken
     */
    private String oauthAccessToken = "";
    public static final String Key$oauthAccessToken = Key + ".oauth-access-token";

    /**
     * revoke authorize or access-token.
     *
     * @see #Key$oauthRevokeToken
     */
    private String oauthRevokeToken = "";
    public static final String Key$oauthRevokeToken = Key + ".oauth-revoke-token";

    /**
     * get basic information of the current login user itself.
     *
     * @see #Key$userAuthedUser
     */
    private String userAuthedUser = "";
    public static final String Key$userAuthedUser = Key + ".user-authed-user";

    /**
     * Check login user permissions, case-insensitive comparison.
     *
     * @see #Key$userAuthedPerm
     */
    private String userAuthedPerm = "";
    public static final String Key$userAuthedPerm = Key + ".user-authed-perm";


    /**
     * list all session of login user.
     *
     * @see #Key$userListSession
     */
    private String userListSession = "";
    public static final String Key$userListSession = Key + ".user-list-session";

    /**
     * dop the session of login user.
     *
     * @see #Key$userDropSession
     */
    private String userDropSession = "";
    public static final String Key$userDropSession = Key + ".user-drop-session";

    /**
     * mock response captcha.
     *
     * @see #Key$mockCaptcha
     */
    private String mockCaptcha = "";
    public static final String Key$mockCaptcha = Key + ".mock-captcha";

    /**
     * mock double kill in 30 seconds.
     *
     * @see #Key$mockDoubler
     */
    private String mockDoubler = "";
    public static final String Key$mockDoubler = Key + ".mock-doubler";

    /**
     * mock anti forgery editing.
     *
     * @see #Key$mockRighter
     */
    private String mockRighter = "";
    public static final String Key$mockRighter = Key + ".mock-righter";

    /**
     * mock echo to response what input.
     *
     * @see #Key$mockEcho0o0
     */
    private String mockEcho0o0 = "";
    public static final String Key$mockEcho0o0 = Key + ".mock-echo0o0";

    /**
     * query run mode, return Product, Test, Develop, Local.
     *
     * @see #Key$testRunMode
     */
    private String testRunMode = "";
    public static final String Key$testRunMode = Key + ".test-run-mode";

    /**
     * query system timestamp, mills from 1970.
     *
     * @see #Key$testSystemMills
     */
    private String testSystemMills = "";
    public static final String Key$testSystemMills = Key + ".test-system-mills";

    /**
     * query current thread timestamp, mills from 1970.
     *
     * @see #Key$testThreadMills
     */
    private String testThreadMills = "";
    public static final String Key$testThreadMills = Key + ".test-thread-mills";

    /**
     * tweak log level of user in thread-level.
     *
     * @see #Key$adminTweakLogger
     */
    private String adminTweakLogger = "";
    public static final String Key$adminTweakLogger = Key + ".admin-tweak-logger";

    /**
     * tweak stacktrace of user in thread-level.
     *
     * @see #Key$adminTweakStack
     */
    private String adminTweakStack = "";
    public static final String Key$adminTweakStack = Key + ".admin-tweak-stack";

    /**
     * tweak clock of user in thread-level.
     *
     * @see #Key$adminTweakClock
     */
    private String adminTweakClock = "";
    public static final String Key$adminTweakClock = Key + ".admin-tweak-clock";

    /**
     * toggle user danger status, and reset failed count
     *
     * @see #Key$adminAuthnDanger
     */
    private String adminAuthnDanger = "";
    public static final String Key$adminAuthnDanger = Key + ".admin-authn-danger";

}
