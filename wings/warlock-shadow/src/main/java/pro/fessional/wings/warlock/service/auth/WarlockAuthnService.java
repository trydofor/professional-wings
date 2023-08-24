package pro.fessional.wings.warlock.service.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.wings.faceless.convention.EmptySugar;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.slardar.security.WingsAuthDetails;
import pro.fessional.wings.slardar.security.impl.DefaultWingsUserDetails;
import pro.fessional.wings.warlock.enums.autogen.UserStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

/**
 * Authentication (AuthN)
 *
 * @author trydofor
 * @since 2021-02-23
 */
public interface WarlockAuthnService {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true, builderMethodName = "")
    class Details {
        // user
        private long userId;
        private String nickname;
        private Locale locale = TerminalContext.defaultLocale();
        private ZoneId zoneId = TerminalContext.defaultZoneId();
        private UserStatus status;

        // auth
        private Enum<?> authType;
        private String username;
        private String password;
        private String passsalt;
        private LocalDateTime expiredDt;

        public boolean isUninit() {
            return EmptySugar.asEmptyValue(expiredDt);
        }
    }

    enum Jane {
        AutoSave,
        Success,
        Failure,
    }

    @Nullable
    Details load(@NotNull Enum<?> authType, String username);

    @Nullable
    Details load(@NotNull Enum<?> authType, long userId);

    /**
     * create the user automatically
     *
     * @param authType authn type
     * @param username username to login
     * @param details  user and auth info
     * @return user details if success
     */
    @Nullable
    Details register(@NotNull Enum<?> authType, String username, WingsAuthDetails details);

    void auth(DefaultWingsUserDetails userDetails, Details details);

    void onSuccess(@NotNull Enum<?> authType, long userId, String details);

    void onFailure(@NotNull Enum<?> authType, String username, String details);
}
