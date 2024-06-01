package pro.fessional.wings.testing.slardar.service;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pro.fessional.mirana.bits.Md5;
import pro.fessional.wings.slardar.security.WingsAuthDetails;
import pro.fessional.wings.slardar.security.WingsUserDetailsService;
import pro.fessional.wings.slardar.security.impl.DefaultWingsUserDetails;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author trydofor
 * @since 2019-11-14
 */
@Slf4j
public class TestingWingsUserDetailsService implements WingsUserDetailsService {

    public final String origPassword = "WingsBoot is Good";
    //5DDEEB6C6F9812EE9C7CCF6FC82A50DD
    public final String sendPassword = Md5.sum(origPassword + ":" + origPassword);
    private final String hashPassword = "{bcrypt}" + new BCryptPasswordEncoder().encode(sendPassword);

    private final List<GrantedAuthority> auths = Arrays.asList(
        new SimpleGrantedAuthority("ROLE_USER"),
        new SimpleGrantedAuthority("ROLE_ADMIN"),
        new SimpleGrantedAuthority("ROLE_CLURK"),
        new SimpleGrantedAuthority("MENU_READ")
    );

    @Override
    public @NotNull UserDetails loadUserByUsername(String username, @Nullable Enum<?> type, @Nullable WingsAuthDetails authDetail) throws UsernameNotFoundException {
        log.info("TestWingsUserDetailsService login type={}, username={}", type, username);

        DefaultWingsUserDetails ud = new DefaultWingsUserDetails();
        int idType = username.endsWith("2") ? 2 : 1;
        ud.setUserId(idType);
        ud.setNickname("nick" + idType);
        ud.setPassword(hashPassword);
        ud.setUsername(username);
        ud.setAuthorities(auths);
        ud.setLocale(Locale.CANADA);
        ud.setZoneId(ZoneId.of("Canada/Central"));

        return ud;
    }

    public static void main(String[] args) {
        final String origPassword = "WingsBoot is Good";
        //
        final String sendPassword = Md5.sum(origPassword + ":" + origPassword);
        log.info("password={}", sendPassword);
    }
}
