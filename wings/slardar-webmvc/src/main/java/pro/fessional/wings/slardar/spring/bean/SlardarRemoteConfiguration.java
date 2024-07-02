package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.silencer.support.PropHelper;
import pro.fessional.wings.slardar.servlet.resolver.WingsRemoteResolver;
import pro.fessional.wings.slardar.spring.prop.SlardarRemoteProp;

/**
 * @author trydofor
 * @since 2019-06-29
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class SlardarRemoteConfiguration {

    private final Log log = LogFactory.getLog(SlardarRemoteConfiguration.class);

    @Bean
    @ConditionalWingsEnabled
    public WingsRemoteResolver wingsRemoteResolver(SlardarRemoteProp conf) {
        log.info("SlardarWebmvc spring-bean wingsRemoteResolver");
        final WingsRemoteResolver resolver = new WingsRemoteResolver();
        resolver.addInnerIp(PropHelper.onlyValid(conf.getInnerIp().values()));
        resolver.addAgentHeader(PropHelper.onlyValid(conf.getAgentHeader().values()));
        resolver.addIpHeader(PropHelper.onlyValid(conf.getIpHeader().values()));
        return resolver;
    }
}
