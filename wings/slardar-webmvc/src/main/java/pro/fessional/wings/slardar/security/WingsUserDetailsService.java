package pro.fessional.wings.slardar.security;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pro.fessional.mirana.data.Null;

/**
 * UserDetailsService with AuthType support, And
 * try `saveUserByDetail` if `load*` throws UsernameNotFoundException
 *
 * @author trydofor
 * @since 2021-02-05
 */
public interface WingsUserDetailsService extends UserDetailsService {

    /**
     * invoke loadUserByUsername(null, username) by default
     *
     * @throws UsernameNotFoundException UsernameNotFound
     */
    @Override
    @NotNull
    default UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadUserByUsername(username, Null.Enm, null);
    }

    /**
     * load UserDetails by username and authType
     *
     * @param username   Unique Key under authType. eg. username, email, userId, etc.
     * @param authType   auth type, Null.Enm equals null
     * @param authDetail Authentication.getDetails
     * @return UserDetails
     * @throws UsernameNotFoundException UsernameNotFound
     * @see Authentication#getDetails
     */
    @NotNull
    UserDetails loadUserByUsername(String username, @NotNull Enum<?> authType, @Nullable WingsAuthDetails authDetail) throws UsernameNotFoundException;
}
