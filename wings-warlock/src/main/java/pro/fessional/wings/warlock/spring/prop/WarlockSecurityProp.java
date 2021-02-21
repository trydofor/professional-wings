package pro.fessional.wings.warlock.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;
import static pro.fessional.mirana.cast.EnumConvertor.str2Enum;

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
     * 未登录时跳转的页面，需要有controller处理
     *
     * @see #Key$loginPage
     */
    private String loginPage = "/auth/login-page.json";
    public static final String Key$loginPage = Key + ".login-page";

    /**
     * 处理登录的URL，由filter处理，不需要controller
     *
     * @see #Key$loginUrl
     */
    private String loginUrl = "/auth/*/login.json";
    public static final String Key$loginUrl = Key + ".login-url";

    /**
     * 登出地址，由filter处理，不需要controller
     *
     * @see #Key$logoutUrl
     */
    private String logoutUrl = "/auth/logout.json";
    public static final String Key$logoutUrl = Key + ".logout-url";

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
     * @see #Key$authority
     */
    private Map<String, Set<String>> authority = Collections.emptyMap();
    public static final String Key$authority = Key + ".authority";

    /**
     * 需要权限访问的路径，antMatcher，逗号分隔，斜杠换行
     *
     * @see #Key$authenticated
     */
    private Set<String> authenticated = Collections.emptySet();
    public static final String Key$authenticated = Key + ".authenticated";

    /**
     * 无权限访问的路径，antMatcher，逗号分隔，斜杠换行
     *
     * @see #Key$permitAll
     */
    private Set<String> permitAll = Collections.emptySet();
    public static final String Key$permitAll = Key + ".permit-all";

    /**
     * 支持的验证类型， enum全路径
     *
     * @see #Key$authType
     */
    private Map<String, String> authType = new HashMap<>();
    public static final String Key$authType = Key + ".auth-type";

    public Map<String, Enum<?>> mapAuthTypeEnum() {
        return authType.entrySet()
                       .stream()
                       .collect(toMap(Map.Entry::getKey, en -> str2Enum(en.getValue())));
    }
}
