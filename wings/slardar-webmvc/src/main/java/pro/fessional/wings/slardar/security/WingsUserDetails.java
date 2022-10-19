package pro.fessional.wings.slardar.security;

import org.springframework.security.core.userdetails.UserDetails;

import java.time.ZoneId;
import java.util.Locale;

/**
 * username 用于验证，nickname用于显示
 *
 * @author trydofor
 * @since 2019-11-27
 */
public interface WingsUserDetails extends UserDetails {

    /**
     * 获得用户id
     *
     * @return id
     */
    long getUserId();

    /**
     * 获得用户区域
     *
     * @return 区域
     */
    Locale getLocale();

    /**
     * 获得用户时区
     *
     * @return 时区
     */
    ZoneId getZoneId();

    /**
     * 验证类型
     *
     * @return 类型
     */
    Enum<?> getAuthType();

    /**
     * 获得用户昵称
     *
     * @return 昵称，默认使用username
     */
    default String getNickname() {
        return getUsername();
    }

    /**
     * 获取密码加盐，构造 password+salt的密码
     *
     * @return 盐，默认空
     */
    default String getPasssalt() {
        return "";
    }

    default boolean isPreAuthed() {
        return false;
    }
}
