package pro.fessional.wings.slardar.spring.bean;

import com.hazelcast.core.HazelcastInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.faceless.service.flakeid.FlakeIdService;
import pro.fessional.wings.slardar.service.flakeid.FlakeIdHazelcastImpl;
import pro.fessional.wings.spring.consts.OrderedSlardarConst;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(FlakeIdService.class)
@AutoConfigureOrder(OrderedSlardarConst.HazelcastFlakeIdConfiguration)
public class HazelcastFlakeIdConfiguration {

    private static final Log log = LogFactory.getLog(HazelcastFlakeIdConfiguration.class);

    @Bean
    public FlakeIdService hazelcastFlakeId(HazelcastInstance instance) {
        log.info("SlardarHazelCaching spring-bean hazelcastFlakeId Overriding");
        return new FlakeIdHazelcastImpl(instance);
    }
}
