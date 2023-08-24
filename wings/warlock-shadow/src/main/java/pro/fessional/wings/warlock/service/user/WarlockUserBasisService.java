package pro.fessional.wings.warlock.service.user;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.pain.CodeException;
import pro.fessional.wings.warlock.enums.autogen.UserGender;
import pro.fessional.wings.warlock.enums.autogen.UserStatus;

import java.time.ZoneId;
import java.util.Locale;

/**
 * Create or modify basis user
 *
 * @author trydofor
 * @since 2021-02-23
 */
public interface WarlockUserBasisService {

    enum Jane {
        Create,
        Modify,
    }

    @Data
    class Basis {
        private String nickname;
        private UserGender gender;
        private String avatar;
        private Locale locale;
        private ZoneId zoneId;
        private String remark;
        private UserStatus status;
    }

    /**
     * Create a user and return the userId.
     */
    long create(@NotNull Basis user);

    /**
     * Modify the user, only change non-null value.
     * fail if userId not found.
     *
     * @throws CodeException data not exist
     */
    void modify(long userId, @NotNull Basis user);

    default void changeStatus(long userId, UserStatus status) {
        Basis user = new Basis();
        user.setStatus(status);
        modify(userId, user);
    }
}
