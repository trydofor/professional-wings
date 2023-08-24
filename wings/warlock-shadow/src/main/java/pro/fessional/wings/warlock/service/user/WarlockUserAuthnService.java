package pro.fessional.wings.warlock.service.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.pain.CodeException;
import pro.fessional.wings.faceless.convention.EmptyValue;
import pro.fessional.wings.slardar.context.Now;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * user login authn
 *
 * @author trydofor
 * @since 2021-03-25
 */
public interface WarlockUserAuthnService {

    enum Jane {
        Create,
        Modify,
        Renew,
        Danger
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
        private Enum<?> authType;
        private String password;
        private LocalDateTime expiredDt;
        private Integer failedCnt;
        private Integer failedMax;
    }

    /**
     * create an auth for user, and return the authn id
     *
     * @param userId user id
     * @param authn  authn
     * @return id
     */
    long create(long userId, @NotNull Authn authn);

    /**
     * modify items(if not null) of authn
     *
     * @param userId user id
     * @param authn  items
     * @throws CodeException data not exised
     */
    void modify(long userId, @NotNull Authn authn);

    /**
     * renew password, expired, failedCount, etc. use default if null.
     *
     * @param userId user id
     * @param renew  item to renew
     */
    void renew(long userId, @NotNull Renew renew);


    /**
     * set user status to danger or not
     *
     * @param userId user
     * @param danger danger or not
     * @param authType auth type to reset
     */
    void dander(long userId, boolean danger, @NotNull Enum<?>... authType);

    /**
     * disable auth by type
     *
     * @param userId   user id
     * @param authType auth type
     */
    default void disable(long userId, @NotNull Enum<?> authType) {
        Renew renew = new Renew();
        renew.setAuthType(authType);
        renew.setExpiredDt(EmptyValue.DATE_TIME);
        renew(userId, renew);
    }

    /**
     * enable auth by type
     *
     * @param userId   user id
     * @param authType auth type
     * @param expireIn expired duration from now
     */
    default void enable(long userId, @NotNull Enum<?> authType, Duration expireIn) {
        Renew renew = new Renew();
        renew.setAuthType(authType);
        renew.setExpiredDt(Now.localDateTime().plusSeconds(expireIn.getSeconds()));
        renew(userId, renew);
    }

    @Data
    class Item {
        private String username;
        private String authType;
        private LocalDateTime expiredDt;
        private int failedCnt;
    }

    /**
     * list auth info of user
     *
     * @param userId user
     * @return auth info
     */
    @NotNull
    List<Item> list(long userId);
}
