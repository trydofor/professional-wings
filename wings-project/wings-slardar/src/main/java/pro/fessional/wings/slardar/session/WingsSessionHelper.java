package pro.fessional.wings.slardar.session;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.query.Predicates;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.MapSession;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

/**
 * @see HttpSessionSecurityContextRepository#SPRING_SECURITY_CONTEXT_KEY
 * @author trydofor
 * @since 2022-02-24
 */
public class WingsSessionHelper {

    @Nullable
    public static SecurityContext getSecurityContext(Session session) {
        return session.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
    }

    //
    public static final String UserIdKey = "userId";

    protected static String hazelcastMapName = null;
    protected static HazelcastInstance hazelcastInstance = null;
    protected static SessionRepository<?> hazelcastRepository = null;

    private static IMap<String, MapSession> hazelcastSessionMap = null;

    public static List<MapSession> findByUserId(Long userId) {
        if (userId == null || userId == Long.MIN_VALUE
            || hazelcastInstance == null
            || hazelcastMapName == null) {
            return Collections.emptyList();
        }

        if (hazelcastSessionMap == null) {
            hazelcastSessionMap = hazelcastInstance.getMap(hazelcastMapName);
        }

        final Collection<MapSession> ses = hazelcastSessionMap.values(Predicates.equal(UserIdKey, userId));
        return new ArrayList<>(ses);
    }

    public static boolean dropSession(String sessionId) {
        if (hazelcastRepository == null) return false;
        hazelcastRepository.deleteById(sessionId);
        return true;
    }
}
