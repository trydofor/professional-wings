package pro.fessional.wings.slardar.service;

import org.springframework.security.core.GrantedAuthority;
import pro.fessional.wings.slardar.security.TypeIdI18nUserDetail;

import java.time.ZoneId;
import java.util.Collection;
import java.util.Locale;

/**
 * @author trydofor
 * @since 2019-11-20
 */
public class TestI18nUserDetail implements TypeIdI18nUserDetail {

    private long userType;
    private long userId;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private ZoneId zoneId = ZoneId.systemDefault();
    private Locale locale = Locale.CHINA;


    public void setUserType(long userType) {
        this.userType = userType;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public long getUserId() {
        return userId;
    }

    @Override
    public long getUserType() {
        return userType;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public ZoneId getZoneId() {
        return zoneId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}