package pro.fessional.wings.slardar.security.impl;

import org.springframework.security.core.GrantedAuthority;
import pro.fessional.wings.slardar.security.WingsUserDetails;

import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author trydofor
 * @since 2021-02-18
 */
public class DefaultWingsUserDetails implements WingsUserDetails {

    private long userId;
    private int userType;
    private Locale locale = Locale.getDefault();
    private ZoneId zoneId = ZoneId.systemDefault();
    private Collection<? extends GrantedAuthority> authorities = Collections.emptySet();
    private String password;
    private String username;

    private boolean userEnabled;
    private boolean userNonExpired;
    private boolean userNonLocked;
    private boolean passNonExpired;

    private final Map<Enum<?>, Object> paras = new HashMap<>();

    public void putPara(Enum<?> key, Object value) {
        paras.put(key, value);
    }

    public void putParaAll(Map<? extends Enum<?>, ?> map) {
        paras.putAll(map);
    }

    @SuppressWarnings("unchecked")
    public <T> T getPara(Enum<?> key) {
        return (T) paras.get(key);
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserEnabled(boolean userEnabled) {
        this.userEnabled = userEnabled;
    }

    public void setUserExpired(boolean userExpired) {
        this.userNonExpired = !userExpired;
    }

    public void setUserLocked(boolean userLocked) {
        this.userNonLocked = !userLocked;
    }

    public void setPassExpired(boolean passExpired) {
        this.passNonExpired = !passExpired;
    }

    @Override
    public long getUserId() {
        return userId;
    }

    @Override
    public int getUserType() {
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
        return userNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return userNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return passNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return userEnabled;
    }
}
