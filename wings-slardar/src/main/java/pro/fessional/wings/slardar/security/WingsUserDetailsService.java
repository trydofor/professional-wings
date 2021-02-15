package pro.fessional.wings.slardar.security;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 支持验证类型的，当load*出现UsernameNotFoundException时，尝试saveUserByDetail
 *
 * @author trydofor
 * @since 2021-02-05
 */
public interface WingsUserDetailsService extends UserDetailsService {

    /**
     * 默认调用 loadUserByUsername(null, username)
     *
     * @param username 同上
     * @return UserDetails
     * @throws UsernameNotFoundException UsernameNotFound
     */
    @Override
    @NotNull
    default UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadUserByUsername(username, null, null);
    }


    /**
     * 按登录类型加载 UserDetails
     *
     * @param username type下身份唯一辨识，用户名，手机号，邮箱，userId等
     * @param authType 验证类型，默认null
     * @param authDetail   Authentication.getDetails
     * @return UserDetails
     * @throws UsernameNotFoundException UsernameNotFound
     * @see Authentication#getDetails
     */
    @NotNull
    UserDetails loadUserByUsername(String username, @Nullable Enum<?> authType, @Nullable Object authDetail) throws UsernameNotFoundException;
}
