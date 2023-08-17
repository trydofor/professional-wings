package pro.fessional.wings.warlock.service.user;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.page.PageQuery;
import pro.fessional.mirana.page.PageResult;

import java.time.LocalDateTime;

/**
 * User login and record auth
 *
 * @author trydofor
 * @since 2021-03-25
 */
public interface WarlockUserLoginService {

    @Data
    class Item {
        private String authType;
        private String username;
        private String loginIp;
        private LocalDateTime loginDt;
        private String terminal;
        private boolean success;
    }

    /**
     * List all login info of user
     *
     * @param userId user id
     * @param query  page query
     * @return login info
     */
    @NotNull
    PageResult<Item> list(long userId, PageQuery query);


    @Data
    class Auth {
        private Enum<?> authType;
        private String username;
        private long userId;
        private String details;
        private boolean failed;
    }

    /**
     * Record the auth info
     *
     * @param auth auth record
     */
    void auth(Auth auth);
}
