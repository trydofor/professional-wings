package pro.fessional.wings.slardar.security;

import org.springframework.security.core.userdetails.UserDetails;

import java.time.ZoneId;
import java.util.Locale;

/**
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
     * 获得用户类型
     *
     * @return 类型
     */
    int getUserType();


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
}
