package pro.fessional.wings.slardar.session;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.MapSession;
import org.springframework.session.Session;

import java.util.List;

/**
 * @author trydofor
 * @see HttpSessionSecurityContextRepository#SPRING_SECURITY_CONTEXT_KEY
 * @since 2022-02-24
 */
public interface WingsSessionHelper {

    @Nullable
    SecurityContext getSecurityContext(Session session);

    @NotNull
    List<MapSession> findByUserId(Long userId);

    boolean dropSession(String sessionId);
}
