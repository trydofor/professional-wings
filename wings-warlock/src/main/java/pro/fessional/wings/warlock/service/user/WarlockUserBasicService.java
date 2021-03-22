package pro.fessional.wings.warlock.service.user;

import lombok.Data;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.warlock.enums.autogen.UserGender;
import pro.fessional.wings.warlock.enums.autogen.UserStatus;

import java.time.ZoneId;
import java.util.Locale;

/**
 * @author trydofor
 * @since 2021-02-23
 */
public interface WarlockUserBasicService {

    enum Jane {
        CreateUser,
    }

    @Data
    class User {
        private String nickname;
        private UserGender gender = UserGender.UNKNOWN;
        private String avatar = Null.Str;
        private Locale locale = Locale.getDefault();
        private ZoneId zoneid = ZoneId.systemDefault();
        private String remark = Null.Str;
        private UserStatus status = UserStatus.UNINIT;
    }

    long createUser(User user);

}
