package pro.fessional.wings.slardar.session;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.MapSession;
import org.springframework.session.Session;
import pro.fessional.wings.slardar.context.SecurityContextUtil;
import pro.fessional.wings.slardar.security.DefaultUserId;
import pro.fessional.wings.slardar.security.WingsUserDetails;

import java.util.List;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

/**
 * @author trydofor
 * @see HttpSessionSecurityContextRepository#SPRING_SECURITY_CONTEXT_KEY
 * @since 2022-02-24
 */
public interface WingsSessionHelper {

    String UserIdKey = "userId";
    String ExpiredKey = "WingsSession.EXPIRED";

    /**
     * Get the login UserId or DefaultUserId.Unknown
     */
    default long getUserId(@NotNull Session session) {
        final Long uid = session.getAttribute(UserIdKey);
        if (uid != null) return uid;

        final SecurityContext ctx = getSecurityContext(session);
        if (ctx != null) {
            final WingsUserDetails dtl = SecurityContextUtil.getUserDetails(ctx.getAuthentication());
            if (dtl != null) {
                return dtl.getUserId();
            }
        }

        return DefaultUserId.Guest;
    }

    /**
     * Determine if ExpiredKey exists `true` value in `bool` or `String` type by default.
     */
    default boolean isExpired(@NotNull Session session) {
        final Object obj = session.getAttribute(ExpiredKey);
        if (obj == null) {
            return false;
        }
        else if (Boolean.TRUE.equals(obj)) {
            return true;
        }
        else if (obj instanceof String str) {
            return "true".equalsIgnoreCase(str);
        }

        return false;
    }

    /**
     * Get the Spring SecurityContext within SPRING_SECURITY_CONTEXT_KEY by default.
     */
    @Nullable
    default SecurityContext getSecurityContext(@NotNull Session session) {
        return session.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
    }

    /**
     * Get all session of userId
     */
    @NotNull
    List<MapSession> findByUserId(Long userId);

    /**
     * Drop the session by sessionId
     */
    boolean dropSession(String sessionId);
}
