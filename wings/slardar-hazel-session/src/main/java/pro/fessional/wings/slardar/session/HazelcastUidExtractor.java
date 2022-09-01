package pro.fessional.wings.slardar.session;

import com.hazelcast.query.extractor.ValueCollector;
import com.hazelcast.query.extractor.ValueExtractor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.session.MapSession;
import pro.fessional.wings.slardar.context.SecurityContextUtil;
import pro.fessional.wings.slardar.security.WingsUserDetails;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

/**
 * @author trydofor
 * @since 2022-02-23
 */
public class HazelcastUidExtractor implements ValueExtractor<MapSession, String> {

    @Override
    @SuppressWarnings("unchecked")
    public void extract(MapSession target, String argument, ValueCollector collector) {
        SecurityContext ctx = target.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        final WingsUserDetails ud = SecurityContextUtil.getUserDetails(ctx);
        if (ud != null) {
            collector.addObject(ud.getUserId());
        }
    }
}
