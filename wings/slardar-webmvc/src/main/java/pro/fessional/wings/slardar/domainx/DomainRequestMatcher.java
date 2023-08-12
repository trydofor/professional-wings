package pro.fessional.wings.slardar.domainx;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author trydofor
 * @since 2021-02-15
 */
public interface DomainRequestMatcher {

    /**
     * Try wrap request if matches
     *
     * @param request original request
     * @param domain  domain to match
     * @return original request or wrapped request if matches
     */
    HttpServletRequest match(HttpServletRequest request, String domain);
}
