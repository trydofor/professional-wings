package pro.fessional.wings.warlock.service.auth;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.text.BarString;
import pro.fessional.wings.slardar.context.Now;
import pro.fessional.wings.slardar.security.DefaultUserId;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author trydofor
 * @since 2022-11-05
 */
public interface WarlockTicketService {

    /**
     * 获取ClientId对应的信息，null为不存在
     */
    @Nullable
    Pass findPass(@NotNull String clientId);

    /**
     * 获取用户下的发行序号，分为accessToken和code
     */
    int nextSeq(long uid, int type);

    /**
     * 吊销用户下所有Code或Token
     */
    void revokeAll(long uid);

    /**
     * 获取凭证的过期时间戳
     *
     * @param ttl 存活时间
     */
    default long calcDue(@NotNull Duration ttl) {
        return Now.millis() / 1000 + ttl.toSeconds();
    }

    /**
     * 当前凭证的序号是否有效，默认不检查，返回true
     */
    default boolean checkSeq(long uid, int type, int seq) {
        return seq >= 0;
    }

    /**
     * 检查 scope是否合法
     */
    default boolean checkScope(long uid, String scope) {
        return true;
    }

    @Data
    class Pass {
        protected long userId = DefaultUserId.Null;
        protected String client = Null.Str;
        protected String secret = Null.Str;
    }

    @Data
    class Term {

        public static final int TypeEmpty = 0;
        public static final int TypeAuthorizeCode = 1;
        public static final int TypeAccessToken = 2;

        /**
         * 类别，AuthCode或AccessToken，非enum以备扩展
         */
        protected int type = TypeEmpty;
        /**
         * 关联的user id
         */
        protected long userId = DefaultUserId.Null;
        /**
         * oauth scope，空格分割，对应于权限
         */
        protected String scopes = Null.Str;
        /**
         * 绑定的session，api不需要session
         */
        protected String sessionId = Null.Str;

        public static String encode(Term term) {
            BarString buff = new BarString();
            buff.append(term.type);
            buff.append(term.userId);
            buff.append(term.scopes);
            buff.append(term.sessionId);
            return buff.toString();
        }

        public static Term decode(String str) {
            final ArrayList<String> parts = BarString.split(str, 4, true);
            if (parts.isEmpty()) return null;

            Term term = new Term();
            final Iterator<String> it = parts.iterator();
            term.type = Integer.parseInt(it.next());
            term.userId = Long.parseLong(it.next());
            term.scopes = it.next();
            term.sessionId = it.next();
            return term;
        }
    }
}
