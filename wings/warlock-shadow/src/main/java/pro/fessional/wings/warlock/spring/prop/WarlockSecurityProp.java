package pro.fessional.wings.warlock.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.silencer.spring.help.CommonPropHelper;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.warlock.enums.autogen.UserStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static pro.fessional.mirana.cast.EnumConvertor.str2Enum;
import static pro.fessional.wings.warlock.enums.autogen.UserStatus.ACTIVE;

/**
 * The order of config is from loose to strict.
 * `webIgnore` > `PermitAll` > `Authenticated` > `Authority` > `AnyRequest` at the end.
 * if value is `-` or `empty`, means ignore this key.
 * <p>
 * Spring Security setting.
 * wings-warlock-security-77.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties(WarlockSecurityProp.Key)
public class WarlockSecurityProp {

    public static final String Key = "wings.warlock.security";

    /**
     * whether to enable WebSecurity.debug
     *
     * @see #Key$webDebug
     */
    private boolean webDebug = false;
    public static final String Key$webDebug = Key + ".web-debug";

    /**
     * whether to use Role in AuthX.
     *
     * @see #Key$authorityRole
     */
    private boolean authorityRole = true;
    public static final String Key$authorityRole = Key + ".authority-role";

    /**
     * whether to use Perm in AuthX.
     *
     * @see #Key$authorityPerm
     */
    private boolean authorityPerm = false;
    public static final String Key$authorityPerm = Key + ".authority-perm";

    /**
     * true to forward in servlet, otherwise redirect(302)
     *
     * @see #Key$loginForward
     */
    private boolean loginForward = true;
    public static final String Key$loginForward = Key + ".login-forward";

    /**
     * the redirect page when not login, need to have controller to handle.
     *
     * @see #Key$loginPage
     */
    private String loginPage = "/auth/login-page.json";
    public static final String Key$loginPage = Key + ".login-page";

    /**
     * loginProcessingUrl, the Ant style URL for processing login, handled by filter, no controller required.
     * Support `authType` and `authZone` variables, which can be obtained via param or path (PathPattern)
     *
     * @see #Key$loginProcUrl
     */
    private String loginProcUrl = "/auth/{authType}/login.json";
    public static final String Key$loginProcUrl = Key + ".login-proc-url";

    /**
     * Spring is POST only to better follow RESTful, but Oauth has Get.
     *
     * @see #Key$loginProcMethod
     */
    private Set<String> loginProcMethod = Collections.emptySet();
    public static final String Key$loginProcMethod = Key + ".login-proc-method";

    /**
     * logout url, handled by filter, no controller required.
     *
     * @see #Key$logoutUrl
     */
    private String logoutUrl = "/auth/logout.json";
    public static final String Key$logoutUrl = Key + ".logout-url";

    /**
     * whether to redirect after successful login.
     *
     * @see #Key$loginSuccessRedirect
     */
    private boolean loginSuccessRedirect = false;
    public static final String Key$loginSuccessRedirect = Key + ".login-success-redirect";

    /**
     * redirect parameters if redirect after successful login.
     *
     * @see #Key$loginSuccessRedirectParam
     */
    private String loginSuccessRedirectParam = "";
    public static final String Key$loginSuccessRedirectParam = Key + ".login-success-redirect-param";

    /**
     * default address if redirect after successful login.
     *
     * @see #Key$loginSuccessRedirectDefault
     */
    private String loginSuccessRedirectDefault = "";
    public static final String Key$loginSuccessRedirectDefault = Key + ".login-success-redirect-default";

    /**
     * the response body if no redirect after successful login.
     *
     * @see #Key$loginSuccessBody
     */
    private String loginSuccessBody = "";
    public static final String Key$loginSuccessBody = Key + ".login-success-body";

    /**
     * the response body if login fail.
     *
     * @see #Key$loginFailureBody
     */
    private String loginFailureBody = "";
    public static final String Key$loginFailureBody = Key + ".login-failure-body";

    /**
     * the response body after successful logout, no handler is injected when empty.
     *
     * @see #Key$logoutSuccessBody
     */
    private String logoutSuccessBody = "";
    public static final String Key$logoutSuccessBody = Key + ".logout-success-body";

    /**
     * the response body after successful logout, no handler is injected when empty.
     *
     * @see #Key$sessionMaximum
     */
    private int sessionMaximum = 1;
    public static final String Key$sessionMaximum = Key + ".session-maximum";

    /**
     * the response body when session expired.
     *
     * @see #Key$sessionExpiredBody
     */
    private String sessionExpiredBody = "";
    public static final String Key$sessionExpiredBody = Key + ".session-expired-body";

    /**
     * username Parameter
     *
     * @see #Key$usernamePara
     */
    private String usernamePara = "username";
    public static final String Key$usernamePara = Key + ".username-para";

    /**
     * password Parameter
     *
     * @see #Key$passwordPara
     */
    private String passwordPara = "password";
    public static final String Key$passwordPara = Key + ".password-para";

    /**
     * GrantedAuthorityDefaults, suggest keeping the same with spring, do not edit.
     *
     * @see #Key$rolePrefix
     */
    private String rolePrefix = "ROLE_";
    public static final String Key$rolePrefix = Key + ".role-prefix";

    /**
     * (1) ignored items, antMatcher, no need of SecurityFilter, such as static resources.
     *
     * @see #Key$webIgnore
     */
    private Map<String, String> webIgnore = Collections.emptyMap();
    public static final String Key$webIgnore = Key + ".web-ignore";

    /**
     * (2) allow all, `Map<String, String>`, antMatcher.
     *
     * @see #Key$permitAll
     */
    private Map<String, String> permitAll = Collections.emptyMap();
    public static final String Key$permitAll = Key + ".permit-all";

    /**
     * (3) authed only, antMatcher.
     *
     * @see #Key$authenticated
     */
    private Map<String, String> authenticated = Collections.emptyMap();
    public static final String Key$authenticated = Key + ".authenticated";

    /**
     * (4) has authority, antMatcher.
     * merge authority by URL grouping, and finally set the URL in reverse ASCII order,
     * i.e., the English number comes before the `*`, and the loose rule comes after.
     *
     * @see #Key$authority
     */
    private Map<String, Set<String>> authority = Collections.emptyMap();
    public static final String Key$authority = Key + ".authority";

    /**
     * <pre>
     * (5) defaults, `String`, support the followings.
     * - `permitAll`|`authenticated`|`anonymous`|`fullyAuthenticated`
     * - any non-empty, non-above string, considered as `Authority`, use `comma` or `blank` to separate multiple ones.
     * </pre>
     *
     * @see #Key$anyRequest
     */
    private String anyRequest = "";
    public static final String Key$anyRequest = Key + ".any-request";

    /**
     * Supported validation types, enum full path, one-to-one, otherwise reverse parsing problem;
     * no `-`, `default` is a special value used when there is no match.
     *
     * @see #Key$authTypeDefault
     */
    private String authTypeDefault = "";
    public static final String Key$authTypeDefault = Key + ".auth-type-default";

    /**
     * login auth-type and enum mapping, must be one-to-one.
     *
     * @see #Key$authType
     */
    private Map<String, String> authType = new HashMap<>();
    public static final String Key$authType = Key + ".auth-type";

    /**
     * Map permissions to authZone, if you have one of them, you can login,
     * otherwise, it will fail with wrong username and password.
     *
     * @see #Key$zonePerm
     */
    private Map<String, Set<String>> zonePerm = new HashMap<>();
    public static final String Key$zonePerm = Key + ".zone-perm";

    /**
     * Map permissions to spring.application.name, if you have one of them, you can login,
     * otherwise, it will fail with wrong username and password. Support AntPath, eg. `wings-*`,
     * merge all matching permissions, wings default app is `wings-default`.
     *
     * @see #Key$appPerm
     */
    private Map<String, Set<String>> appPerm = new HashMap<>();
    public static final String Key$appPerm = Key + ".app-perm";

    /**
     * which auth-type support Nonce auth.
     *
     * @see #Key$nonceAuthType
     */
    private Set<String> nonceAuthType = Collections.emptySet();
    public static final String Key$nonceAuthType = Key + ".nonce-auth-type";

    /**
     * bean name of cache-manager, same as `wings.slardar.cache.primary`.
     *
     * @see #Key$nonceCacheManager
     * @see pro.fessional.wings.slardar.cache.WingsCache.Manager
     */
    private String nonceCacheManager = "MemoryCacheManager";
    public static final String Key$nonceCacheManager = Key + ".nonce-cache-manager";

    /**
     * cache level, see `wings.slardar.cache.level.`
     *
     * @see #Key$nonceCacheLevel
     */
    private String nonceCacheLevel = "pro/fessional/wings/slardar/service";
    public static final String Key$nonceCacheLevel = Key + ".nonce-cache-level";


    /**
     * which auth-type support to auto register new user. eg. `github,weibo`
     *
     * @see #Key$autoregAuthType
     */
    private Set<String> autoregAuthType = Collections.emptySet();
    public static final String Key$autoregAuthType = Key + ".autoreg-auth-type";

    /**
     * max mumber of consecutive failures for auto-registering users, and locking the account when reached.
     *
     * @see #Key$autoregMaxFailed
     */
    private int autoregMaxFailed = 5;
    public static final String Key$autoregMaxFailed = Key + ".autoreg-max-failed";

    /**
     * credential expiration time for auto-registering users, default 3652 days (10 years)
     *
     * @see #Key$autoregExpired
     */
    private Duration autoregExpired = Duration.ofDays(1000);
    public static final String Key$autoregExpired = Key + ".autoreg-expired";


    /**
     * <pre>
     * Configure memory user, usually used for special user login.
     * - key is the description, override if duplicate, suggest `username`+(`/`+`auth-type`)?
     * - `auth-type=`, to match all auth-type.
     * - For other settings, see WarlockAuthnService.Details and its defaults.
     * </pre>
     *
     * @see #Key$memUser
     */
    private Map<String, Mu> memUser = Collections.emptyMap();
    public static final String Key$memUser = Key + ".mem-user";

    /**
     * Memory user permissions, key is the description,
     * override if duplicate, suggest naming by type and usage.
     *
     * @see #Key$memAuth
     */
    private Map<String, Ma> memAuth = Collections.emptyMap();
    public static final String Key$memAuth = Key + ".mem-auth";

    @Data
    public static class Mu {
        private long userId;
        private Set<String> authType = new HashSet<>();
        private String username;
        private String password;
        private UserStatus status = ACTIVE;
        private String nickname;
        private String passsalt = "";
        private Locale locale = TerminalContext.defaultLocale();
        private ZoneId zoneId = TerminalContext.defaultZoneId();
        private LocalDateTime expired = LocalDateTime.MAX;
    }

    @Data
    public static class Ma {
        private long userId = -1;
        private String authType;
        private String username;
        private Set<String> authRole = Collections.emptySet();
        private Set<String> authPerm = Collections.emptySet();
    }

    // ////
    public Enum<?> mapAuthTypeDefault() {
        return str2Enum(authTypeDefault);
    }

    public Map<String, Enum<?>> mapAuthTypeEnum() {
        return authType.entrySet()
                       .stream()
                       .filter(it -> CommonPropHelper.hasValue(it.getValue()))
                       .collect(toMap(Map.Entry::getKey, en -> str2Enum(en.getValue())));
    }

    public Set<Enum<?>> mapAutoregAuthEnum() {
        return autoregAuthType.stream().map(it -> {
            final String s = authType.get(it);
            if (s == null) throw new IllegalArgumentException("not found auth-type=" + it);
            return str2Enum(s);
        }).collect(Collectors.toSet());
    }

    public Set<Enum<?>> mapNonceAuthEnum() {
        return nonceAuthType.stream().map(it -> {
            final String s = authType.get(it);
            if (s == null) throw new IllegalArgumentException("not found auth-type=" + it);
            return str2Enum(s);
        }).collect(Collectors.toSet());
    }
}
