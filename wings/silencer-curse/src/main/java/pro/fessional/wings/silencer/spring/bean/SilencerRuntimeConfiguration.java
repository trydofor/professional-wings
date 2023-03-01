package pro.fessional.wings.silencer.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.modulate.RuntimeMode;
import pro.fessional.wings.silencer.spring.prop.SilencerRuntimeProp;
import pro.fessional.wings.spring.consts.OrderedSilencerConst;

/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(OrderedSilencerConst.RuntimeConfiguration)
public class SilencerRuntimeConfiguration {

    private final static Log log = LogFactory.getLog(SilencerRuntimeConfiguration.class);

    @Autowired
    public void autowireRuntimeMode(SilencerRuntimeProp prop) {
        log.info("Silencer spring-auto runnerRuntimeMode");
        new RuntimeMode() {{
            runMode = prop.getRunMode();
            apiMode = prop.getApiMode();
        }};
    }
}
