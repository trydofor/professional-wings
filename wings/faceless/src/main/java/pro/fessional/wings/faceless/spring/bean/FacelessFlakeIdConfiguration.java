package pro.fessional.wings.faceless.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.spring.consts.OrderedFacelessConst;
import pro.fessional.wings.faceless.service.flakeid.FlakeIdService;
import pro.fessional.wings.faceless.service.flakeid.impl.FlakeIdLightIdImpl;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.faceless.spring.prop.FacelessEnabledProp;

/**
 * @author trydofor
 * @since 2019-06-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = FacelessEnabledProp.Key$lightid, havingValue = "true")
@AutoConfigureOrder(OrderedFacelessConst.FlakeIdConfiguration)
public class FacelessFlakeIdConfiguration {

    private static final Log log = LogFactory.getLog(FacelessFlakeIdConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(FlakeIdService.class)
    public FlakeIdService flakeIdService(LightIdService lightIdService) {
        log.info("Faceless spring-bean flakeIdService");
        return new FlakeIdLightIdImpl(lightIdService);
    }
}
