package pro.fessional.wings.slardar.domainx;

import javax.servlet.http.HttpServletRequest;

/**
 * @author trydofor
 * @since 2021-02-15
 */
public interface DomainRequestMatcher {

    /**
     * 尝试wrap request，
     *
     * @param request 原始请求
     * @param domain  匹配的domain
     * @return 原始request或wrap后
     */
    HttpServletRequest match(HttpServletRequest request, String domain);
}
