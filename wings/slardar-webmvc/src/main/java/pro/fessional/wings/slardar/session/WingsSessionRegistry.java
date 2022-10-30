package pro.fessional.wings.slardar.session;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.events.SessionCreatedEvent;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import pro.fessional.wings.slardar.context.SecurityContextUtil;
import pro.fessional.wings.slardar.security.DefaultUserId;
import pro.fessional.wings.slardar.security.WingsUserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author trydofor
 * @see SpringSessionBackedSessionRegistry
 * @since 2022-02-24
 */
@Slf4j
public class WingsSessionRegistry<S extends Session> implements SessionRegistry, ApplicationListener<SessionCreatedEvent> {

    private final WingsSessionHelper wingsSessionHelper;
    private final FindByIndexNameSessionRepository<S> sessionRepository;

    public WingsSessionRegistry(FindByIndexNameSessionRepository<S> repository, WingsSessionHelper helper) {
        this.sessionRepository = repository;
        this.wingsSessionHelper = helper;
    }

    @Override
    public List<SessionInformation> getAllSessions(Object principal, boolean includeExpiredSessions) {
        final String name;
        long userId = DefaultUserId.Guest;
        if (principal instanceof WingsUserDetails) {
            final WingsUserDetails dt = (WingsUserDetails) principal;
            name = dt.getUsername();
            userId = dt.getUserId();
        }
        else if (principal instanceof AbstractAuthenticationToken) {
            name = ((AbstractAuthenticationToken) principal).getName();
        }
        else {
            name = principal == null ? "" : principal.toString();
        }

        final Collection<S> sessions = sessionRepository.findByPrincipalName(name).values();
        List<SessionInformation> infos = new ArrayList<>();

        for (S ss : sessions) {
            if (includeExpiredSessions || !wingsSessionHelper.isExpired(ss)) {
                // 默认情况principal和userId一对一，实际也支持principal和userId一对多
                if (userId == DefaultUserId.Guest || userId == wingsSessionHelper.getUserId(ss)) {
                    infos.add(new WingsSessionInformation<>(ss, sessionRepository));
                }
            }
        }

        return infos;
    }

    @Override
    public List<Object> getAllPrincipals() {
        throw new UnsupportedOperationException("no need");
    }

    @Override
    public SessionInformation getSessionInformation(String sessionId) {
        S ss = sessionRepository.findById(sessionId);
        return ss == null ? null : new WingsSessionInformation<>(ss, sessionRepository);
    }

    @Override
    public void refreshLastRequest(String sessionId) {
    }

    @Override
    public void registerNewSession(String sessionId, Object principal) {
    }

    @Override
    public void removeSessionInformation(String sessionId) {
    }

    //
    @Override
    public void onApplicationEvent(@NotNull SessionCreatedEvent event) {
        final Session session = event.getSession();
        final SecurityContext ctx = wingsSessionHelper.getSecurityContext(session);
        if (ctx == null) return;
        final WingsUserDetails dtl = SecurityContextUtil.getUserDetails(ctx.getAuthentication());
        if (dtl == null) return;

        S backend = sessionRepository.findById(session.getId());
        if (backend == null) {
            log.warn("Could not find Session with id " + session.getId() + " to set UserId");
        }
        else {
            backend.setAttribute(WingsSessionHelper.UserIdKey, dtl.getUserId());
            sessionRepository.save(backend);
        }
    }
}
