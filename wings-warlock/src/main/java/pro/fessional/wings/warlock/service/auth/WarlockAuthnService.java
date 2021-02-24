package pro.fessional.wings.warlock.service.auth;

import lombok.Data;
import me.zhyd.oauth.model.AuthUser;
import pro.fessional.wings.slardar.security.impl.DefaultWingsUserDetails;
import pro.fessional.wings.warlock.enums.autogen.UserStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

/**
 * @author trydofor
 * @since 2021-02-23
 */
public interface WarlockAuthnService {

    @Data
    class Details {
        // user
        private long userId;
        private String nickname;
        private Locale locale = Locale.getDefault();
        private ZoneId zoneId = ZoneId.systemDefault();
        private UserStatus status;

        // auth
        private Enum<?> authType;
        private String username;
        private String password;
        private String passsalt;
        private LocalDateTime expiredDt;
    }

    Details load(Enum<?> authType, String username);

    Details save(Enum<?> authType, String username, AuthUser authUser);

    void auth(DefaultWingsUserDetails userDetails, Details details);

    void onSuccess(Enum<?> authType, long userId, String details);

    void onFailure(Enum<?> authType, String username);
}
