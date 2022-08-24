package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.servlet.resolver.WingsRemoteResolver;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.slardar.spring.prop.SlardarRemoteProp;

import static pro.fessional.wings.silencer.spring.help.CommonPropHelper.validValue;

/**
 * @author trydofor
 * @since 2019-06-29
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$remote, havingValue = "true")
public class SlardarRemoteConfiguration {

    private final Log log = LogFactory.getLog(SlardarRemoteConfiguration.class);

    @Bean
    public WingsRemoteResolver wingsTerminalResolver(SlardarRemoteProp conf) {
        log.info("Wings conf WingsRemoteResolver");
        final WingsRemoteResolver resolver = new WingsRemoteResolver();
        resolver.addInnerIp(validValue(conf.getInnerIp().values()));
        resolver.addAgentHeader(validValue(conf.getAgentHeader().values()));
        resolver.addIpHeader(validValue(conf.getIpHeader().values()));
        return resolver;
    }
}
