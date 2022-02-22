package pro.fessional.wings.slardar.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import pro.fessional.mirana.cast.TypedCastUtil;
import pro.fessional.wings.slardar.security.WingsAuthDetails;
import pro.fessional.wings.slardar.security.WingsUserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Wrapper fo SecurityContextHolder.
 * <p>
 * 尽量在controller层使用，当异步时，context会失效。
 * 因为spring的threadlocal仅支持手动inherit。
 *
 * @author trydofor
 * @since 2019-07-09
 */
public class SecurityContextUtil {

    @NotNull
    @SuppressWarnings("unchecked")
    public static Collection<GrantedAuthority> getAuthorities() {
        Authentication atn = getAuthentication();
        if (atn == null) return Collections.emptyList();

        Collection<? extends GrantedAuthority> ats = atn.getAuthorities();
        if (ats == null) {
            return Collections.emptyList();
        }
        return (Collection<GrantedAuthority>) ats;
    }

    @NotNull
    public static <T extends GrantedAuthority> Collection<T> getAuthorities(Class<T> claz) {
        Authentication atn = getAuthentication();
        if (atn == null) return Collections.emptyList();
        return TypedCastUtil.castCollection(atn.getAuthorities(), claz);

    }

    @Nullable
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Nullable
    public static WingsAuthDetails getAuthDetails() {
        return getAuthDetails(WingsAuthDetails.class);
    }

    @Nullable
    public static <T> T getAuthDetails(Class<T> claz) {
        Authentication atn = getAuthentication();
        if (atn == null) return null;

        return TypedCastUtil.castObject(atn.getDetails(), claz);
    }

    /**
     * 一般为登录的用户名，String
     *
     * @param <T> Principal 类型
     * @return Principal
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T getPrincipal() {
        Authentication atn = getAuthentication();
        if (atn == null) return null;
        return (T) atn.getPrincipal();
    }

    /**
     * 一般为 UserDetailsService 放入的 UserDetails
     *
     * @param <T> UserDetails 类型
     * @return UserDetails
     */
    @Nullable
    public static <T> T getPrincipal(Class<T> claz) {
        Authentication atn = getAuthentication();
        if (atn == null) return null;
        return TypedCastUtil.castObject(atn.getPrincipal(), claz);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T getCredentials() {
        Authentication atn = getAuthentication();
        if (atn == null) return null;
        return (T) atn.getCredentials();
    }

    @Nullable
    public static <T> T getCredentials(Class<T> claz) {
        Authentication atn = getAuthentication();
        if (atn == null) return null;
        return TypedCastUtil.castObject(atn.getCredentials(), claz);
    }

    @Nullable
    public static WingsUserDetails getUserDetails() {
        Authentication atn = getAuthentication();
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


    /**
     * 获得登录的userId，如果未登录或非wings类型，返回 Long.MIN_VALUE。
     * 一般下userId有区间，小的正数-内置用户；大一点正数-业务用户；负数-特殊用户。
     *
     * @return 登录uid，或Long.MIN_VALUE
     */
    public static long getUserId() {
        final WingsUserDetails dtl = getUserDetails();
        return dtl == null ? Long.MIN_VALUE : dtl.getUserId();
    }
}
