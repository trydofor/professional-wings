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
     * Find Pass Info by ClientId, null if not exist
     */
    @Nullable
    Pass findPass(@NotNull String clientId);

    /**
     * Get the next serial number under the user by type
     *
     * @see Term#TypeAuthorizeCode
     * @see Term#TypeAccessToken
     */
    int nextSeq(long uid, int type);

    /**
     * revoke the code and token under the user
     */
    void revokeAll(long uid);


    /**
     * Calculate the expired timestamp
     *
     * @param ttl time to live
     */
    default long calcDue(@NotNull Duration ttl) {
        return Now.millis() / 1000 + ttl.toSeconds();
    }

    /**
     * Whether the serial number is valid.
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
         * the hostname of http status code=302, should not use ipv6
         */
        protected Set<String> hosts = Collections.emptySet();
    }

    interface Term {

        int TypeEmpty = 0;
        int TypeAuthorizeCode = 1;
        int TypeAccessToken = 2;

        /**
         * Whether it can decode and match exactly.
         */
        default boolean decode(String str) {
            return decode(str, true);
        }

        /**
         * Whether the string can decode into Term and its value
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


        /**
         * encode term to string
         */
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
         * Number of field to encode and decode.
         *
         * @see #encode(Term)
         * @see #decode(String)
         */
        default int getSize() {
            return 5; // type,userId,scope,clientId,sessionId
        }

        /**
         * @see #TypeAccessToken
         * @see #TypeAuthorizeCode
         */
        int getType();

        /**
         * @see #TypeAccessToken
         * @see #TypeAuthorizeCode
         */
        void setType(int type);

        /**
         * the userid of the resource visitor
         */
        long getUserId();

        /**
         * the userid of the resource visitor
         */
        void setUserId(long userId);

        /**
         * the scope of resource, space seperated (corresponds to the permissions)
         */
        String getScopes();

        /**
         * the scope of resource, space seperated (corresponds to the permissions)
         */
        void setScopes(String scopes);

        /**
         * the clientId of the resource visitor, Support for one-to-many scenarios
         */
        String getClientId();

        /**
         * the clientId of the resource visitor, Support for one-to-many scenarios
         */
        void setClientId(String clientId);

        /**
         * the sessionId of the resource owner, no session in api
         */
        String getSessionId();

        /**
         * the sessionId of the resource owner, no session in api
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
    }
}
