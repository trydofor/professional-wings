package pro.fessional.wings.warlock.service.user;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.pain.CodeException;
import pro.fessional.wings.warlock.enums.autogen.UserGender;
import pro.fessional.wings.warlock.enums.autogen.UserStatus;

import java.time.ZoneId;
import java.util.Locale;

/**
 * 创建和修改基本用户
 *
 * @author trydofor
 * @since 2021-02-23
 */
public interface WarlockUserBasicService {

    enum Jane {
        Create,
        Modify,
    }

    @Data
    class User {
        private String nickname;
        private UserGender gender;
        private String avatar;
        private Locale locale;
        private ZoneId zoneid;
        private String remark;
        private UserStatus status;
    }

    /**
     * 插入用户，并返回Uid。
     * 对应自动为null时，系统设置默认值
     *
     * @param user user
     * @return userId
     */
    long create(@NotNull User user);

    /**
     * 修改用户，只修改不为null的字段。
     * userId不存在未修改失败
     *
     * @param userId 对应的userId
     * @param user   需要调整的值
     * @throws CodeException 数据不存在
     */
    void modify(long userId, @NotNull User user);

    default void changeStatus(long userId, UserStatus status) {
        User user = new User();
        user.setStatus(status);
        modify(userId, user);
    }
}
