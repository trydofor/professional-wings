package pro.fessional.wings.slardar.session;

import com.hazelcast.query.extractor.ValueCollector;
import com.hazelcast.query.extractor.ValueExtractor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.session.MapSession;
import pro.fessional.wings.slardar.context.SecurityContextUtil;
import pro.fessional.wings.slardar.security.WingsUserDetails;

/**
 * @author trydofor
 * @since 2022-02-23
 */
public class HazelcastUidExtractor implements ValueExtractor<MapSession, String> {

    public HazelcastUidExtractor() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public void extract(MapSession target, String argument, ValueCollector collector) {
        SecurityContext ctx = WingsSessionHelper.getSecurityContext(target);
        final WingsUserDetails ud = SecurityContextUtil.getUserDetails(ctx);
        if (ud != null) {
            collector.addObject(ud.getUserId());
        }
    }
}
