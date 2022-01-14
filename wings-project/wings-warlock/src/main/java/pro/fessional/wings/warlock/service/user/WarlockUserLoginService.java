package pro.fessional.wings.warlock.service.user;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.page.PageQuery;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户登录验证
 *
 * @author trydofor
 * @since 2021-03-25
 */
public interface WarlockUserLoginService {

    @Data
    class Item {
        private String authType;
        private String loginIp;
        private LocalDateTime loginDt;
        private String terminal;
        private boolean success;
    }

    /**
     * 列出用户所有登录信息
     *
     * @param userId 用户
     * @param query  分页
     * @return 登录信息
     */
    @NotNull
    List<Item> list(long userId, PageQuery query);


    @Data
    class Auth {
        private Enum<?> authType;
        private long userId;
        private String details;
        private boolean failed;
    }

    /**
     * 记录登录验证情况
     *
     * @param auth 验证
     */
    void auth(Auth auth);
}
