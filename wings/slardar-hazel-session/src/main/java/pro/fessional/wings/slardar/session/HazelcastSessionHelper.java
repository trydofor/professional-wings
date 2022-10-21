package pro.fessional.wings.slardar.session;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.query.Predicates;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.MapSession;
import org.springframework.session.SessionRepository;
import pro.fessional.wings.slardar.security.DefaultUserId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author trydofor
 * @see HttpSessionSecurityContextRepository#SPRING_SECURITY_CONTEXT_KEY
 * @since 2022-02-24
 */
public class HazelcastSessionHelper implements WingsSessionHelper {

    private final SessionRepository<?> hazelcastRepository;
    private final IMap<String, MapSession> hazelcastSessionMap;

    public HazelcastSessionHelper(SessionRepository<?> repository, HazelcastInstance instance, String sessionMap) {
        this.hazelcastRepository = repository;
        this.hazelcastSessionMap = instance.getMap(sessionMap);
    }

    @Override
    @NotNull
    public List<MapSession> findByUserId(Long userId) {
        if (DefaultUserId.asGuest(userId) || hazelcastSessionMap == null) {
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
