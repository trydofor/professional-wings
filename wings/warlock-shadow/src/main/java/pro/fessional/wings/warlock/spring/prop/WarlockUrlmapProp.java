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
     * 集成登录默认页，默认返回支持的type类表，需要PathVar {extName}
     *
     * @see #Key$authLoginList
     */
    private String authLoginList = "";
    public static final String Key$authLoginList = Key + ".auth-login-list";

    /**
     * 具体验证登录默认页，根据content-type自动返回，需要PathVar {extName} {authType}
     *
     * @see #Key$authLoginPage
     */
    private String authLoginPage = "";
    public static final String Key$authLoginPage = Key + ".auth-login-page";
    /**
     * 具体验证登录默认页，根据content-type自动返回，需要PathVar {extName}
     *
     * @see #Key$authLoginPage
     */
    private String authLoginPage2 = "";
    public static final String Key$authLoginPage2 = Key + ".auth-login-page2";

    /**
     * 验证一次性token是否有效，oauth2使用state作为token
     *
     * @see #Key$authNonceCheck
     */
    private String authNonceCheck = "";
    public static final String Key$authNonceCheck = Key + ".auth-nonce-check";

    /**
     * 简单的authorization code授权类型
     *
     * @see #Key$oauthAuthorize
     */
    private String oauthAuthorize = "";
    public static final String Key$oauthAuthorize = Key + ".oauth-authorize";

    /**
     * 简单的获取access-token
     *
     * @see #Key$oauthAccessToken
     */
    private String oauthAccessToken = "";
    public static final String Key$oauthAccessToken = Key + ".oauth-access-token";

    /**
     * 吊销authorize或access-token
     *
     * @see #Key$oauthRevokeToken
     */
    private String oauthRevokeToken = "";
    public static final String Key$oauthRevokeToken = Key + ".oauth-revoke-token";

    /**
     * 获得登录用户的自身基本信息
     *
     * @see #Key$userAuthedUser
     */
    private String userAuthedUser = "";
    public static final String Key$userAuthedUser = Key + ".user-authed-user";

    /**
     * 检查登录用户的权限，不区分大小写比较
     *
     * @see #Key$userAuthedPerm
     */
    private String userAuthedPerm = "";
    public static final String Key$userAuthedPerm = Key + ".user-authed-perm";


    /**
     * 列出用户所有登录session
     *
     * @see #Key$userListSession
     */
    private String userListSession = "";
    public static final String Key$userListSession = Key + ".user-list-session";

    /**
     * 踢出用户登录session
     *
     * @see #Key$userDropSession
     */
    private String userDropSession = "";
    public static final String Key$userDropSession = Key + ".user-drop-session";

    /**
     * @see #Key$mockCaptcha
     */
    private String mockCaptcha = "";
    public static final String Key$mockCaptcha = Key + ".mock-captcha";

    /**
     * @see #Key$mockDoubler
     */
    private String mockDoubler = "";
    public static final String Key$mockDoubler = Key + ".mock-doubler";

    /**
     * @see #Key$mockRighter
     */
    private String mockRighter = "";
    public static final String Key$mockRighter = Key + ".mock-righter";

    /**
     * @see #Key$mockEcho0o0
     */
    private String mockEcho0o0 = "";
    public static final String Key$mockEcho0o0 = Key + ".mock-echo0o0";

    /**
     * @see #Key$testRunMode
     */
    private String testRunMode = "";
    public static final String Key$testRunMode = Key + ".test-run-mode";

    /**
     * @see #Key$testSystemMills
     */
    private String testSystemMills = "";
    public static final String Key$testSystemMills = Key + ".test-system-mills";

    /**
     * @see #Key$testThreadMills
     */
    private String testThreadMills = "";
    public static final String Key$testThreadMills = Key + ".test-thread-mills";

    /**
     * @see #Key$adminTweakLogger
     */
    private String adminTweakLogger = "";
    public static final String Key$adminTweakLogger = Key + ".admin-tweak-logger";

    /**
     * @see #Key$adminTweakStack
     */
    private String adminTweakStack = "";
    public static final String Key$adminTweakStack = Key + ".admin-tweak-stack";

    /**
     * @see #Key$adminTweakClock
     */
    private String adminTweakClock = "";
    public static final String Key$adminTweakClock = Key + ".admin-tweak-clock";
}
