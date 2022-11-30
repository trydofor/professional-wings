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
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * @author trydofor
 * @since 2022-11-05
 */
public interface WarlockTicketService {

    @Nullable
    Term decode(String token);

    @NotNull
    String encode(@NotNull Term term, @NotNull Duration ttl);

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

    @Data
    class Pass {
        protected long userId = DefaultUserId.Null;
        protected String client = Null.Str;
        protected String secret = Null.Str;
        protected Set<String> scopes = Collections.emptySet();
        /**
         * 302的主机名，不要使用ipv6
         */
        protected Set<String> hosts = Collections.emptySet();
    }

    interface Term {

        int TypeEmpty = 0;
        int TypeAuthorizeCode = 1;
        int TypeAccessToken = 2;

        /**
         * 能否精确解析，完全匹配
         */
        default boolean decode(String str) {
            return decode(str, true);
        }

        /**
         * 能否成功的以term解析字符串，并赋值
         */
        default boolean decode(String str, boolean exactly) {
            final int size = getSize();
            final ArrayList<String> parts = BarString.split(str, size, exactly);
            if (parts.size() < size) return false;

            final Iterator<String> it = parts.iterator();
            setType(Integer.parseInt(it.next()));
            setUserId(Long.parseLong(it.next()));
            setScopes(it.next());
            setClientId(it.next());
            setSessionId(it.next());
            return true;
        }


        static String encode(Term term) {
            BarString buff = new BarString();
            buff.append(term.getType());
            buff.append(term.getUserId());
            buff.append(term.getScopes());
            buff.append(term.getClientId());
            buff.append(term.getSessionId());
            return buff.toString();
        }

        /**
         * 包含的字段数
         */
        int getSize();

        /**
         * 类别，AuthCode或AccessToken，非enum以备扩展
         */
        int getType();

        /**
         * 类别，AuthCode或AccessToken，非enum以备扩展
         */
        void setType(int type);

        /**
         * 资源访问者对应的user
         */
        long getUserId();

        /**
         * 资源访问者对应的user
         */
        void setUserId(long userId);

        /**
         * 资源对应的scope，空格分割，对应于权限
         */
        String getScopes();

        /**
         * 资源对应的scope，空格分割，对应于权限
         */
        void setScopes(String scopes);

        /**
         * 资源访问者的client id，支持一对多的场景
         */
        String getClientId();

        /**
         * 资源访问者的client id，支持一对多的场景
         */
        void setClientId(String clientId);

        /**
         * 资源拥有者的session，api不需要session
         */
        String getSessionId();

        /**
         * 资源拥有者的session，api不需要session
         */
        void setSessionId(String sessionId);
    }

    @Data
    class SimpleTerm implements Term {
        protected int type = TypeEmpty;
        protected long userId = DefaultUserId.Null;
        protected String scopes = Null.Str;
        protected String clientId = Null.Str;
        protected String sessionId = Null.Str;

        @Override
        public int getSize() {
            return 5;
        }
    }
}
