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
     * user id
     */
    long getUserId();

    /**
     * locale for i18n
     */
    Locale getLocale();

    /**
     * zoneid for time
     */
    ZoneId getZoneId();

    /**
     * auth type for auth
     */
    Enum<?> getAuthType();

    /**
     * nickname to show, unlike username for auth
     *
     * @return use username by default
     */
    default String getNickname() {
        return getUsername();
    }

    /**
     * password salting
     *
     * @return empty by default.
     */
    default String getPasssalt() {
        return "";
    }

    default boolean isPreAuthed() {
        return false;
    }
}
