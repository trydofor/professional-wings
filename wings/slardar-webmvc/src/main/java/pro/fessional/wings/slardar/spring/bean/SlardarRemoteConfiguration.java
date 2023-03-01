package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.spring.help.CommonPropHelper;
import pro.fessional.wings.spring.consts.OrderedSlardarConst;
import pro.fessional.wings.slardar.servlet.resolver.WingsRemoteResolver;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.slardar.spring.prop.SlardarRemoteProp;

/**
 * @author trydofor
 * @since 2019-06-29
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$remote, havingValue = "true")
@AutoConfigureOrder(OrderedSlardarConst.RemoteConfiguration)
public class SlardarRemoteConfiguration {

    private final Log log = LogFactory.getLog(SlardarRemoteConfiguration.class);

    @Bean
    public WingsRemoteResolver wingsTerminalResolver(SlardarRemoteProp conf) {
        log.info("SlardarWebmvc spring-bean WingsRemoteResolver");
        final WingsRemoteResolver resolver = new WingsRemoteResolver();
        resolver.addInnerIp(CommonPropHelper.onlyValue(conf.getInnerIp().values()));
        resolver.addAgentHeader(CommonPropHelper.onlyValue(conf.getAgentHeader().values()));
        resolver.addIpHeader(CommonPropHelper.onlyValue(conf.getIpHeader().values()));
        return resolver;
    }
}
