package pro.fessional.wings.slardar.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;

/**
 * @author trydofor
 * @since 2019-11-14
 */
public class TestUserDetailsService implements UserDetailsService {

    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private final List<SimpleGrantedAuthority> auths = Arrays.asList(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN"),
            new SimpleGrantedAuthority("ROLE_CLURK"),
            new SimpleGrantedAuthority("MENU_READ")
    );

    private final String password = "{bcrypt}" + new BCryptPasswordEncoder().encode("wings-slardar-pass");

    @Override
    public UserDetails loadUserByUsername(String username) {
        System.out.println("loadUserByUsername=" + username);
        Authentication aut = SecurityContextHolder.getContext().getAuthentication();
        TestI18nUserDetail detail = new TestI18nUserDetail();
        int idType = username.endsWith("2") ? 2 : 1;
        detail.setUserId(idType);
        detail.setUserType(idType);
        detail.setPassword(password);
        detail.setUsername(username);
        detail.setAuthorities(auths);

        detail.setEnabled(enabled);
        detail.setAccountNonExpired(accountNonExpired);
        detail.setAccountNonLocked(accountNonLocked);
        detail.setCredentialsNonExpired(credentialsNonExpired);

        return detail;
    }
}
