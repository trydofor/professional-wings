package pro.fessional.wings.slardar.context;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import pro.fessional.mirana.cast.TypedCastUtil;
import pro.fessional.wings.slardar.security.WingsAuthDetails;
import pro.fessional.wings.slardar.security.WingsUserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * <pre>
 * Wrapper fo SecurityContextHolder.
 *
 * Try to use it in controller layer, if in async thread, the context will fail.
 * Because spring's threadlocal only supports manual inheritance.
 *
 * In wings, the Authentication is UsernamePasswordAuthenticationToken;
 * details is WingsAuthDetails; principal is WingsUserDetails
 * </pre>
 *
 * @author trydofor
 * @since 2019-07-09
 */
public class SecurityContextUtil {

    @NotNull
    @SuppressWarnings("unchecked")
    public static Collection<GrantedAuthority> getAuthorities() {
        Authentication atn = getAuthentication(false);
        if (atn == null) return Collections.emptyList();

        Collection<? extends GrantedAuthority> ats = atn.getAuthorities();
        if (ats == null) {
            return Collections.emptyList();
        }
        return (Collection<GrantedAuthority>) ats;
    }

    @NotNull
    public static <T extends GrantedAuthority> Collection<T> getAuthorities(Class<T> claz) {
        Authentication atn = getAuthentication(false);
        if (atn == null) return Collections.emptyList();
        return TypedCastUtil.castCollection(atn.getAuthorities(), claz);
    }

    @NotNull
    public static Authentication getAuthentication() throws SecurityContextException {
        return getAuthentication(true);
    }

    @Contract("true -> !null")
    public static Authentication getAuthentication(boolean notnull) throws SecurityContextException {
        final Authentication an = SecurityContextHolder.getContext().getAuthentication();
        if (an == null && notnull) {
            throw new SecurityContextException("failed to getAuthentication");
        }
        return an;
    }

    @NotNull
    public static WingsAuthDetails getAuthDetails() throws SecurityContextException {
        return getAuthDetails(true);
    }

    @Contract("true -> !null")
    public static WingsAuthDetails getAuthDetails(boolean notnull) throws SecurityContextException {
        final WingsAuthDetails an = getAuthDetails(WingsAuthDetails.class);
        if (an == null && notnull) {
            throw new SecurityContextException("failed to getAuthDetails");
        }
        return an;
    }

    @Nullable
    public static <T> T getAuthDetails(Class<T> claz) {
        Authentication atn = getAuthentication(false);
        return getAuthDetails(claz, atn);
    }

    @Nullable
    public static WingsAuthDetails getAuthDetails(Authentication atn) {
        return getAuthDetails(WingsAuthDetails.class, atn);
    }

    @Nullable
    public static <T> T getAuthDetails(Class<T> claz, Authentication atn) {
        if (atn == null) return null;
        return TypedCastUtil.castObject(atn.getDetails(), claz);
    }

    /**
     * In wings, it is the username before login and WingsUserDetails after successful login.
     */
    @NotNull
    public static <T> T getPrincipal() throws SecurityContextException {
        return getPrincipal(true);
    }

    @SuppressWarnings("unchecked")
    @Contract("true -> !null")
    public static <T> T getPrincipal(boolean notnull) throws SecurityContextException {
        Authentication atn = getAuthentication(notnull);
        final Object pt = atn.getPrincipal();
        if (pt == null && notnull) {
            throw new SecurityContextException("failed to getPrincipal");
        }
        return (T) pt;
    }

    /**
     * Generally, it is UserDetails provided by UserDetailsService
     *
     * @param <T> UserDetails type
     * @return UserDetails
     */
    @Nullable
    public static <T> T getPrincipal(Class<T> claz) {
        Authentication atn = getAuthentication(false);
        if (atn == null) return null;
        return TypedCastUtil.castObject(atn.getPrincipal(), claz);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T getCredentials() {
        Authentication atn = getAuthentication(false);
        if (atn == null) return null;
        return (T) atn.getCredentials();
    }

    @Nullable
    public static <T> T getCredentials(Class<T> claz) {
        Authentication atn = getAuthentication(false);
        if (atn == null) return null;
        return TypedCastUtil.castObject(atn.getCredentials(), claz);
    }

    @NotNull
    public static WingsUserDetails getUserDetails() throws SecurityContextException {
        return getUserDetails(true);
    }

    @Contract("true -> !null")
    public static WingsUserDetails getUserDetails(boolean notnull) throws SecurityContextException {
        final WingsUserDetails dt = getUserDetails(getAuthentication(notnull));
        if (dt == null && notnull) {
            throw new SecurityContextException("failed to getUserDetails");
        }
        return dt;
    }

    @Nullable
    public static WingsUserDetails getUserDetails(SecurityContext context) {
        if (context == null) return null;
        return getUserDetails(context.getAuthentication());
    }

    @Nullable
    public static WingsUserDetails getUserDetails(Authentication atn) {
        if (atn == null) return null;

        final Object pri = atn.getPrincipal();
        if (pri instanceof WingsUserDetails) {
            return (WingsUserDetails) pri;
        }

        final Object dtl = atn.getDetails();
        if (dtl instanceof WingsUserDetails) {
            return (WingsUserDetails) dtl;
        }
        else if (dtl instanceof WingsAuthDetails) {
            final Object rd = ((WingsAuthDetails) dtl).getRealData();
            if (rd instanceof WingsUserDetails) {
                return (WingsUserDetails) rd;
            }
        }

        return null;
    }

    public static long getUserId() {
        return getUserId(true);
    }

    @Contract("true -> !null")
    public static Long getUserId(boolean notnull) throws SecurityContextException {
        final WingsUserDetails dtl = getUserDetails(notnull);
        return dtl == null ? null : dtl.getUserId();
    }
}
