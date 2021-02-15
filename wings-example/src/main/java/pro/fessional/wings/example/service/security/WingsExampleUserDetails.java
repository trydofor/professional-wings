package pro.fessional.wings.example.service.security;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.security.core.GrantedAuthority;
import pro.fessional.wings.example.enums.auto.LoginType;
import pro.fessional.wings.example.enums.auto.UserType;
import pro.fessional.wings.faceless.enums.ConstantEnumUtil;
import pro.fessional.wings.slardar.security.WingsUserDetails;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

/**
 * @author trydofor
 * @since 2019-11-30
 */
@Setter
@Getter
public class WingsExampleUserDetails implements WingsUserDetails {

    private String username;
    private String password;
    private Collection<GrantedAuthority> authorities = Collections.emptyList();

    private long userId;
    private int userType;
    private int loginType; // LoginTypeEnum

    private String name;
    private int gender;
    private LocalDate birth;
    private String avatar;
    private String country;

    private Locale locale;
    private ZoneId zoneId;

    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

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
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        WingsExampleUserDetails that = (WingsExampleUserDetails) o;

        return new EqualsBuilder()
                .append(userId, that.userId)
                .append(userType, that.userType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(userId)
                .append(userType)
                .toHashCode();
    }

    public String toLoginInfo() {
        // 手动拼接，高于自动化json
        return "{\"id\":" + userId
                + ",\"name\":\"" + name +
                "\",\"type\":\"" + ConstantEnumUtil.intOrNull(userType, UserType.values()) +
                "\",\"loginName\":\"" + username +
                "\",\"loginType\":\"" + ConstantEnumUtil.intOrNull(loginType, LoginType.values()) +
                "\"}";
    }
}
