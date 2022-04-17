package pro.fessional.wings.slardar.session;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.query.Predicates;
import org.jetbrains.annotations.NotNull;
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
 * @author trydofor
 * @see HttpSessionSecurityContextRepository#SPRING_SECURITY_CONTEXT_KEY
 * @since 2022-02-24
 */
public class HazelcastSessionHelper implements WingsSessionHelper {


    public static final String UserIdKey = "userId";

    private final SessionRepository<?> hazelcastRepository;
    private final IMap<String, MapSession> hazelcastSessionMap;

    public HazelcastSessionHelper(SessionRepository<?> repository, HazelcastInstance instance, String sessionMap) {
        this.hazelcastRepository = repository;
        this.hazelcastSessionMap = instance.getMap(sessionMap);
    }

    @Override
    @Nullable
    public SecurityContext getSecurityContext(Session session) {
        return session.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
    }

    @Override
    @NotNull
    public List<MapSession> findByUserId(Long userId) {
        if (userId == null || userId == Long.MIN_VALUE || hazelcastSessionMap == null) {
            return Collections.emptyList();
        }

        final Collection<MapSession> ses = hazelcastSessionMap.values(Predicates.equal(UserIdKey, userId));
        return new ArrayList<>(ses);
    }

    @Override
    public boolean dropSession(String sessionId) {
        if (hazelcastRepository == null) return false;

        hazelcastRepository.deleteById(sessionId);
        return true;
    }
}
