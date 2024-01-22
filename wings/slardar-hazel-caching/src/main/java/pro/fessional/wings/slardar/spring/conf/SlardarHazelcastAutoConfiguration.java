package pro.fessional.wings.slardar.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.spring.bean.HazelcastConfigConfiguration;
import pro.fessional.wings.slardar.spring.bean.HazelcastFacelessConfiguration;
import pro.fessional.wings.slardar.spring.bean.HazelcastServiceConfiguration;

/**
 * Distributed
 *
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration(before = SlardarCacheAutoConfiguration.class)
@ConditionalWingsEnabled
@Import({
        HazelcastConfigConfiguration.class,
        HazelcastFacelessConfiguration.class,
        HazelcastServiceConfiguration.class,
})
public class SlardarHazelcastAutoConfiguration {
}
