package pro.fessional.wings.warlock.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
import static pro.fessional.wings.silencer.spring.help.CommonPropHelper.validValue;
import static pro.fessional.wings.warlock.enums.autogen.UserStatus.ACTIVE;

/**
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
     * @see #Key$webDebug
     */
    private boolean webDebug = false;
    public static final String Key$webDebug = Key + ".web-debug";

    /**
     * 权限是否使用Role
     *
     * @see #Key$authorityRole
     */
    private boolean authorityRole = true;
    public static final String Key$authorityRole = Key + ".authority-role";

    /**
     * 权限是否使用Perm
     *
     * @see #Key$authorityPerm
     */
    private boolean authorityPerm = false;
    public static final String Key$authorityPerm = Key + ".authority-perm";

    /**
     * true以servlet的forward进行，否则redirect(302)跳转
     *
     * @see #Key$loginForward
     */
    private boolean loginForward = true;
    public static final String Key$loginForward = Key + ".login-forward";

    /**
     * 未登录时跳转的页面，需要有controller处理
     *
     * @see #Key$loginPage
     */
    private String loginPage = "/auth/login-page.json";
    public static final String Key$loginPage = Key + ".login-page";

    /**
     * 处理登录的Ant格式URL，由filter处理，不需要controller
     * 支持变量`authType`和`authZone`，可以通过param或path获得（PathPattern）
     *
     * @see #Key$loginProcUrl
     */
    private String loginProcUrl = "/auth/{authType}/login.json";
    public static final String Key$loginProcUrl = Key + ".login-proc-url";

    /**
     * @see #Key$loginProcMethod
     */
    private Set<String> loginProcMethod = Collections.emptySet();
    public static final String Key$loginProcMethod = Key + ".login-proc-method";

    /**
     * 登出地址，由filter处理，不需要controller
     *
     * @see #Key$logoutUrl
     */
    private String logoutUrl = "/auth/logout.json";
    public static final String Key$logoutUrl = Key + ".logout-url";

    /**
     * 登录成功后是否重定向
     *
     * @see #Key$loginSuccessRedirect
     */
    private boolean loginSuccessRedirect = false;
    public static final String Key$loginSuccessRedirect = Key + ".login-success-redirect";

    /**
     * 登录成功的重定向参数
     *
     * @see #Key$loginSuccessRedirectParam
     */
    private String loginSuccessRedirectParam = "";
    public static final String Key$loginSuccessRedirectParam = Key + ".login-success-redirect-param";

    /**
     * 登录成功的重定向默认地址
     *
     * @see #Key$loginSuccessRedirectDefault
     */
    private String loginSuccessRedirectDefault = "";
    public static final String Key$loginSuccessRedirectDefault = Key + ".login-success-redirect-default";

    /**
     * 登录成功返回的body
     *
     * @see #Key$loginSuccessBody
     */
    private String loginSuccessBody = "";
    public static final String Key$loginSuccessBody = Key + ".login-success-body";

    /**
     * 登录失败返回的body
     *
     * @see #Key$loginFailureBody
     */
    private String loginFailureBody = "";
    public static final String Key$loginFailureBody = Key + ".login-failure-body";

    /**
     * 登出成功返回的body
     * <p>
     * logout-success-body
     *
     * @see #Key$logoutSuccessBody
     */
    private String logoutSuccessBody = "";
    public static final String Key$logoutSuccessBody = Key + ".logout-success-body";

    /**
     * 同时登陆的session数
     *
     * @see #Key$sessionMaximum
     */
    private int sessionMaximum = 1;
    public static final String Key$sessionMaximum = Key + ".session-maximum";

    /**
     * 过期时返回的内容
     *
     * @see #Key$sessionExpiredBody
     */
    private String sessionExpiredBody = "";
    public static final String Key$sessionExpiredBody = Key + ".session-expired-body";

    /**
     * usernameParameter 名字
     *
     * @see #Key$usernamePara
     */
    private String usernamePara = "username";
    public static final String Key$usernamePara = Key + ".username-para";

    /**
     * passwordParameter 名字
     *
     * @see #Key$passwordPara
     */
    private String passwordPara = "password";
    public static final String Key$passwordPara = Key + ".password-para";

    /**
     * @see #Key$rolePrefix
     */
    private String rolePrefix = "ROLE_";
    public static final String Key$rolePrefix = Key + ".role-prefix";

    /**
     * @see #Key$authority
     */
    private Map<String, Set<String>> authority = Collections.emptyMap();
    public static final String Key$authority = Key + ".authority";

    /**
     * 需要权限访问的路径，antMatcher，逗号分隔，斜杠换行
     *
     * @see #Key$authenticated
     */
    private Map<String, String> authenticated = Collections.emptyMap();
    public static final String Key$authenticated = Key + ".authenticated";

    /**
     * 都允许访问的路径，antMatcher，逗号分隔，斜杠换行
     *
     * @see #Key$permitAll
     */
    private Map<String, String> permitAll = Collections.emptyMap();
    public static final String Key$permitAll = Key + ".permit-all";

    /**
     * 忽略项，无SecurityFilter流程及功能，如静态资源。
     *
     * @see #Key$webIgnore
     */
    private Map<String, String> webIgnore = Collections.emptyMap();
    public static final String Key$webIgnore = Key + ".web-ignore";

    /**
     * 空为忽略，支持【permitAll|authenticated|anonymous|fullyAuthenticated】
     * 任意非空，非以上字符串，任务是Authority，逗号或空白分割。
     *
     * @see #Key$anyRequest
     */
    private String anyRequest = "";
    public static final String Key$anyRequest = Key + ".any-request";

    /**
     * 支持的验证类型， enum全路径，一对一，否则反向解析有问题
     *
     * @see #Key$authType
     */
    private Map<String, String> authType = new HashMap<>();
    public static final String Key$authType = Key + ".auth-type";

    /**
     * 默认auth-type
     *
     * @see #Key$authTypeDefault
     */
    private String authTypeDefault = "";
    public static final String Key$authTypeDefault = Key + ".auth-type-default";

    /**
     * 设置authZone对应的权限，若非全部满足，则不可登录，以用户名密码错误返回
     *
     * @see #Key$zonePerm
     */
    private Map<String, Set<String>> zonePerm = new HashMap<>();
    public static final String Key$zonePerm = Key + ".zone-perm";

    /**
     * 设置spring.application.name对应的权限，若非全部满足，则不可登录，以用户名密码错误返回
     * 支持AntPath，如`wings-*`，合并所有匹配的权限设置项，wings默认程序为`wings-default`
     *
     * @see #Key$appPerm
     */
    private Map<String, Set<String>> appPerm = new HashMap<>();
    public static final String Key$appPerm = Key + ".app-perm";

    /**
     * 支持Nonce的验证类型
     *
     * @see #Key$nonceAuthType
     */
    private Set<String> nonceAuthType = Collections.emptySet();
    public static final String Key$nonceAuthType = Key + ".nonce-auth-type";

    /**
     * 默认的cache-manager bean name，同wings.slardar.cache.primary
     *
     * @see #Key$nonceCacheManager
     * @see pro.fessional.wings.slardar.cache.WingsCache.Manager
     */
    private String nonceCacheManager = "MemoryCacheManager";
    public static final String Key$nonceCacheManager = Key + ".nonce-cache-manager";

    /**
     * 默认使用的缓存leve
     *
     * @see #Key$nonceCacheLevel
     */
    private String nonceCacheLevel = "pro/fessional/wings/slardar/service";
    public static final String Key$nonceCacheLevel = Key + ".nonce-cache-level";


    /**
     * 支持自动注册用户的验证类型
     *
     * @see #Key$autoregAuthType
     */
    private Set<String> autoregAuthType = Collections.emptySet();
    public static final String Key$autoregAuthType = Key + ".autoreg-auth-type";

    /**
     * 最大连续失败次数，到达后锁账户
     *
     * @see #Key$autoregMaxFailed
     */
    private int autoregMaxFailed = 5;
    public static final String Key$autoregMaxFailed = Key + ".autoreg-max-failed";

    /**
     * 创建用户时，默认凭证过期时间
     *
     * @see #Key$autoregExpired
     */
    private Duration autoregExpired = Duration.ofDays(1000);
    public static final String Key$autoregExpired = Key + ".autoreg-expired";


    /**
     * 内存用户，key用户说明，重复时覆盖，建议为`username`+[`/`+`auth-type`]
     * auth-type=""或null时，为匹配全部auth-type，而"null"为Null类型
     * 其他设置，参考WarlockAuthnService.Details 的类型及默认值
     *
     * @see #Key$memUser
     */
    private Map<String, Mu> memUser = Collections.emptyMap();
    public static final String Key$memUser = Key + ".mem-user";

    /**
     * 内存用户权限，key授权说明，重复时覆盖，建议以类型和用途
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
                       .filter(it -> validValue(it.getValue()))
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
