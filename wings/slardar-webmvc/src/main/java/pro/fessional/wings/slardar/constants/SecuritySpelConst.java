package pro.fessional.wings.slardar.constants;

import org.springframework.security.access.expression.SecurityExpressionOperations;

/**
 * @author trydofor
 * @see SecurityExpressionOperations
 * @since 2023-09-2023/9/22
 */
public interface SecuritySpelConst {
    String Bgn = "('";
    String Comma = "','";
    String End = "')";
    String hasAuthority = "hasAuthority" + Bgn;
    String hasAnyAuthority = "hasAnyAuthority" + Bgn;
    String hasRole = "hasRole" + Bgn;
    String hasAnyRole = "hasAnyRole" + Bgn;
    String permitAll = "permitAll()";
    String denyAll = "denyAll()";
    String isAnonymous = "isAnonymous()";
    String isAuthenticated = "isAuthenticated()";
    String isRememberMe = "isRememberMe()";
    String isFullyAuthenticated = "isFullyAuthenticated()";
}
