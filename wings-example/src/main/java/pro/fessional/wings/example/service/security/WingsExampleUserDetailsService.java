package pro.fessional.wings.example.service.security;

import lombok.Setter;
import org.jooq.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pro.fessional.mirana.i18n.LocaleResolver;
import pro.fessional.wings.example.database.autogen.tables.WinUserLoginTable;
import pro.fessional.wings.example.database.autogen.tables.daos.WinUserDao;
import pro.fessional.wings.example.database.autogen.tables.daos.WinUserLoginDao;
import pro.fessional.wings.example.database.autogen.tables.pojos.WinUser;
import pro.fessional.wings.example.database.autogen.tables.pojos.WinUserLogin;
import pro.fessional.wings.example.enums.auto.LoginType;
import pro.fessional.wings.example.enums.auto.UserStatus;
import pro.fessional.wings.example.enums.auto.UserType;
import pro.fessional.wings.example.service.authrole.AuthRoleService;
import pro.fessional.wings.faceless.enums.ConstantEnumUtil;
import pro.fessional.wings.faceless.enums.TimezoneEnumUtil;
import pro.fessional.wings.faceless.enums.auto.StandardTimezone;

import java.util.List;
import java.util.Set;

/**
 * @author trydofor
 * @since 2019-11-30
 */
@Service
@Setter(onMethod = @__({@Autowired}))
public class WingsExampleUserDetailsService implements UserDetailsService {

    private WinUserLoginDao winUserLoginDao;
    private WinUserDao winUserDao;
    private AuthRoleService authRoleService;

    // 支持的登录类型
    private static final LoginType[] loginTypes = {
            LoginType.EMAIL_PASS,
            LoginType.NAME_PASS
    };

    private static final UserStatus[] enables = {
            UserStatus.UNINIT,
            UserStatus.ACTIVE
    };


    @Override
    public WingsExampleUserDetails loadUserByUsername(String username) {
        LoginType loginType = LoginType.NAME_PASS;
//        String password = passwordEncoder.encode("moilioncircle");

        WinUserLoginTable t = winUserLoginDao.getTable();
        Condition cond = t.LoginName.eq(username).and(t.LoginType.eq(loginType.getId()));
        List<WinUserLogin> logins = winUserLoginDao.fetch(cond);

        if (logins.size() != 1) {
            throw new UsernameNotFoundException("find " + logins.size() + " users. username=" + username);
        }

        WinUserLogin login = logins.get(0);
        WinUser user = winUserDao.fetchOneById(login.getUserId());
        if (user == null) {
            throw new UsernameNotFoundException("can not find users. id=" + login.getUserId());
        }

        Set<GrantedAuthority> auths = authRoleService.loadRoleAuth(user.getRoleSet(), user.getAuthSet());

        WingsExampleUserDetails detail = new WingsExampleUserDetails();
        detail.setUsername(username);
        detail.setPassword(login.getLoginPass());
        detail.setAuthorities(null);
        detail.setUserId(user.getId());
        detail.setUserType(UserType.GUEST.getId());
        detail.setLoginType(loginType.getId());
        detail.setName(user.getName());
        detail.setGender(user.getGender());
        detail.setBirth(user.getBirth());
        detail.setAvatar(user.getAvatar());
        detail.setCountry(user.getCountry());
        detail.setLocale(LocaleResolver.locale(user.getLanguage()));
        detail.setZoneId(TimezoneEnumUtil.idOrThrow(user.getTimezone(), StandardTimezone.values()).toZoneId());

        detail.setAccountNonExpired(true);
        detail.setAccountNonLocked(true);
        detail.setCredentialsNonExpired(true);
        detail.setEnabled(ConstantEnumUtil.idIn(user.getStatus(), enables));
        detail.setAuthorities(auths);

        return detail;
    }
}
