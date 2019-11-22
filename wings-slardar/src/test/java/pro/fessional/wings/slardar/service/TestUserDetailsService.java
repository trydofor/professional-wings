package pro.fessional.wings.slardar.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import pro.fessional.wings.slardar.security.SecurityContextUtil;
import pro.fessional.wings.slardar.security.WingsOAuth2xContext;

/**
 * @author trydofor
 * @since 2019-11-14
 */
public class TestUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) {
        System.out.println(username);
        WingsOAuth2xContext.Context ocx = SecurityContextUtil.getOauth2xContext();
        Authentication aut = SecurityContextHolder.getContext().getAuthentication();
        return new TestI18nUserDetail();
    }
}
