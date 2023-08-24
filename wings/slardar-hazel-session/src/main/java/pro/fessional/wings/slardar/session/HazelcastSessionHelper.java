package pro.fessional.wings.slardar.session;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.query.Predicates;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.MapSession;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.events.SessionCreatedEvent;
import pro.fessional.wings.slardar.context.SecurityContextUtil;
import pro.fessional.wings.slardar.security.DefaultUserId;
import pro.fessional.wings.slardar.security.WingsUserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author trydofor
 * @see HttpSessionSecurityContextRepository#SPRING_SECURITY_CONTEXT_KEY
 * @since 2022-02-24
 */
@Slf4j
public class HazelcastSessionHelper implements WingsSessionHelper, ApplicationListener<SessionCreatedEvent> {

    private final SessionRepository<Session> hazelcastRepository;
    private final IMap<String, MapSession> hazelcastSessionMap;

    public HazelcastSessionHelper(SessionRepository<Session> repository, HazelcastInstance instance, String sessionMap) {
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

    @Override
    public void onApplicationEvent(@NotNull SessionCreatedEvent event) {
        final Session session = event.getSession();
        final SecurityContext ctx = getSecurityContext(session);
        if (ctx == null) return;
        final WingsUserDetails dtl = SecurityContextUtil.getUserDetails(ctx.getAuthentication());
        if (dtl == null) return;

        Session backend = hazelcastRepository.findById(session.getId());
        if (backend == null) {
            log.warn("Could not find Session with id={} to set UserId", session.getId());
        }
        else {
            final long userId = dtl.getUserId();
            log.debug("set Attribute UserIdKey to session, userId={}", userId);
            backend.setAttribute(WingsSessionHelper.UserIdKey, userId);
            hazelcastRepository.save(backend);
        }
    }
}
