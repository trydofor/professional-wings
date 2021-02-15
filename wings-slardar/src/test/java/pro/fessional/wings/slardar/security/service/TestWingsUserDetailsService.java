package pro.fessional.wings.slardar.security.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pro.fessional.mirana.bits.Md5;
import pro.fessional.wings.slardar.security.WingsUserDetailsService;
import pro.fessional.wings.slardar.service.TestWingsUserDetails;

import java.util.Arrays;
import java.util.List;

/**
 * @author trydofor
 * @since 2019-11-14
 */
@Data
@Service
@Slf4j
public class TestWingsUserDetailsService implements WingsUserDetailsService {

    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;

    public final String origPassword = "大翅挺好吃";
    //F9EC9CF4EA9EEEE69FC01AA44638087F
    public final String sendPassword = Md5.sum(origPassword + ":" + origPassword);
    private final String hashPassword = "{bcrypt}" + new BCryptPasswordEncoder().encode(sendPassword);

    private final List<SimpleGrantedAuthority> auths = Arrays.asList(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN"),
            new SimpleGrantedAuthority("ROLE_CLURK"),
            new SimpleGrantedAuthority("MENU_READ")
    );

    @Override
    public @NotNull UserDetails loadUserByUsername(String username, @Nullable Enum<?> type, @Nullable Object authDetail) throws UsernameNotFoundException {
        log.info("login type={}, username={}", type, username);

        TestWingsUserDetails ud = new TestWingsUserDetails();
        int idType = username.endsWith("2") ? 2 : 1;
        ud.setUserId(idType);
        ud.setUserType(idType);
        ud.setPassword(hashPassword);
        ud.setUsername(username);
        ud.setAuthorities(auths);

        ud.setEnabled(enabled);
        ud.setAccountNonExpired(accountNonExpired);
        ud.setAccountNonLocked(accountNonLocked);
        ud.setCredentialsNonExpired(credentialsNonExpired);

        return ud;
    }

    public static void main(String[] args) {
        final String origPassword = "大翅挺好吃";
        //
        final String sendPassword = Md5.sum(origPassword + ":" + origPassword);
        System.out.println(sendPassword);
    }
}
