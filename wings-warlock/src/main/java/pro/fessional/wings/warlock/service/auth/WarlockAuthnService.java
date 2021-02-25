package pro.fessional.wings.warlock.service.auth;

import lombok.Builder;
import lombok.Data;
import org.springframework.core.Ordered;
import pro.fessional.wings.slardar.security.impl.DefaultWingsUserDetails;
import pro.fessional.wings.warlock.enums.autogen.UserStatus;

import java.time.Duration;
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

    enum Jane {
        AutoSave,
        Success,
        Failure,
        Renew
    }

    Details load(Enum<?> authType, String username);

    Details save(Enum<?> authType, String username, Object details);

    void auth(DefaultWingsUserDetails userDetails, Details details);

    void onSuccess(Enum<?> authType, long userId, String details);

    void onFailure(Enum<?> authType, String username);

    @Data
    @Builder
    class Authn {
        private Integer maxFailed;
        private String password;
        private Duration expiredIn;
        private boolean zeroFail;
    }

    /**
     * 设置密码，有限期，错误计数，连错上限
     */
    void renew(Enum<?> authType, String username, Authn authn);

    /**
     * 设置密码，有限期，错误计数，连错上限
     */
    void renew(Enum<?> authType, long userId, Authn authn);

    // /////
    interface Saver extends Ordered {
        /**
         * 不需要事务,在外层事务内调用
         */
        Details save(Enum<?> authType, String username, Object details);

        boolean accept(Enum<?> authType, String username, Object details);
    }
}
