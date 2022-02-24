package org.springframework.session.security;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import pro.fessional.wings.slardar.context.SecurityContextUtil;
import pro.fessional.wings.slardar.security.WingsUserDetails;
import pro.fessional.wings.slardar.session.WingsSessionHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * dont want copy SpringSessionBackedSessionInformation
 *
 * @author trydofor
 * @see SpringSessionBackedSessionRegistry
 * @since 2022-02-24
 */
public class WingsSessionRegistry<S extends Session> extends SpringSessionBackedSessionRegistry<S> {

    private final FindByIndexNameSessionRepository<S> sessionRepository;

    public WingsSessionRegistry(FindByIndexNameSessionRepository<S> sessionRepository) {
        super(sessionRepository);
        this.sessionRepository = sessionRepository;
    }

    @Override
    public List<SessionInformation> getAllSessions(Object principal, boolean includeExpiredSessions) {
        if (!(principal instanceof WingsUserDetails)) {
            return super.getAllSessions(principal, includeExpiredSessions);
        }

        final WingsUserDetails dtl = (WingsUserDetails) principal;
        final String userName = dtl.getUsername();
        final long userId = dtl.getUserId();

        Collection<S> sessions = this.sessionRepository.findByPrincipalName(userName).values();
        List<SessionInformation> infos = new ArrayList<>();
        for (S session : sessions) {
            if (isSameUid(session, userId) && includeExpiredSessions
                || !Boolean.TRUE.equals(session.getAttribute(SpringSessionBackedSessionInformation.EXPIRED_ATTR))) {
                infos.add(new SpringSessionBackedSessionInformation<>(session, this.sessionRepository));
            }
        }
        return infos;
    }

    private boolean isSameUid(Session session, long uid) {
        final SecurityContext ctx = WingsSessionHelper.getSecurityContext(session);
        if (ctx == null) return false;
        final WingsUserDetails dtl = SecurityContextUtil.getUserDetails(ctx.getAuthentication());
        return dtl != null && dtl.getUserId() == uid;
    }
}
