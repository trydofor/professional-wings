package pro.fessional.wings.slardar.session;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;

import java.util.Date;

import static org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME;
import static pro.fessional.wings.slardar.session.WingsSessionHelper.ExpiredKey;

/**
 * @author trydofor
 * @since 2022-04-21
 */
@Slf4j
@Getter
public class WingsSessionInformation<S extends Session> extends SessionInformation {

    private final SessionRepository<S> sessionRepository;
    private final S session;

    public WingsSessionInformation(S session, SessionRepository<S> repository) {
        super(session.getAttribute(PRINCIPAL_NAME_INDEX_NAME), session.getId(), Date.from(session.getLastAccessedTime()));
        this.session = session;
        this.sessionRepository = repository;
        final Boolean expired = session.getAttribute(ExpiredKey);
        if (Boolean.TRUE.equals(expired)) {
            super.expireNow();
        }
    }

    @Override
    public void expireNow() {
        if (log.isDebugEnabled()) {
            log.debug("Expiring session " + getSessionId() + " for user '" + getPrincipal()
                      + "', presumably because maximum allowed concurrent " + "sessions was exceeded");
        }
        super.expireNow();
        S session = sessionRepository.findById(getSessionId());
        if (session != null) {
            session.setAttribute(ExpiredKey, Boolean.TRUE);
            sessionRepository.save(session);
        }
        else {
            log.info("Could not find Session with id " + getSessionId() + " to mark as expired");
        }
    }
}
