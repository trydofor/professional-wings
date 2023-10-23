package pro.fessional.wings.slardar.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import pro.fessional.wings.slardar.spring.bean.HazelcastCacheConfiguration;
import pro.fessional.wings.slardar.spring.bean.HazelcastFlakeIdConfiguration;
import pro.fessional.wings.slardar.spring.bean.HazelcastLightIdConfiguration;
import pro.fessional.wings.slardar.spring.bean.HazelcastMockConfiguration;
import pro.fessional.wings.slardar.spring.bean.HazelcastPublisherConfiguration;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration(before = {SlardarAutoConfiguration.class, HazelcastAutoConfiguration.class})
@ConditionalOnProperty(name = SlardarEnabledProp.Key$autoconf, havingValue = "true")
@ImportAutoConfiguration({
        HazelcastCacheConfiguration.class,
        HazelcastFlakeIdConfiguration.class,
        HazelcastLightIdConfiguration.class,
        HazelcastMockConfiguration.class,
        HazelcastPublisherConfiguration.class,
})
public class SlardarHazelCachingAutoConfiguration {
}
