package pro.fessional.wings.warlock.service.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.pain.CodeException;
import pro.fessional.wings.faceless.convention.EmptyValue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户登录验证
 *
 * @author trydofor
 * @since 2021-03-25
 */
public interface WarlockUserAuthnService {

    enum Jane {
        Create,
        Modify,
        Renew
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    class Authn extends Renew {
        private String username;
        private String extraPara;
        private String extraUser;
    }

    @Data
    class Renew {
        private String password;
        private LocalDateTime expiredDt;
        private Integer failedCnt;
        private Integer failedMax;
    }

    /**
     * 创建
     *
     * @param userId   所属用户
     * @param authType 验证类型
     * @param authn    数据
     * @return id
     */
    long create(long userId, @NotNull Enum<?> authType, @NotNull Authn authn);

    /**
     * 修改
     *
     * @param userId   所属用户
     * @param authType 验证类型
     * @param authn    数据
     * @throws CodeException 数据不存在
     */
    void modify(long userId, @NotNull Enum<?> authType, @NotNull Authn authn);

    /**
     * 重置密码，有效期，错误计数，连错上限
     *
     * @param userId   user id
     * @param authType 验证类型
     * @param renew    修改项
     */
    void renew(long userId, @NotNull Enum<?> authType, @NotNull Renew renew);

    /**
     * 关闭该验证
     *
     * @param userId   user id
     * @param authType 验证类型
     */
    default void disable(long userId, @NotNull Enum<?> authType) {
        Renew renew = new Renew();
        renew.setExpiredDt(EmptyValue.DATE_TIME);
        renew(userId, authType, renew);
    }

    /**
     * 启用该验证
     *
     * @param userId   user id
     * @param authType 验证类型
     * @param expireIn 有效期
     */
    default void enable(long userId, @NotNull Enum<?> authType, Duration expireIn) {
        Renew renew = new Renew();
        renew.setExpiredDt(LocalDateTime.now().plusSeconds(expireIn.getSeconds()));
        renew(userId, authType, renew);
    }

    @Data
    class Item {
        private String username;
        private String authType;
        private LocalDateTime expiredDt;
        private int failedCnt;
    }

    /**
     * 列出用户所有登录信息
     *
     * @param userId 用户
     * @return 登录信息
     */
    @NotNull
    List<Item> list(long userId);
}
