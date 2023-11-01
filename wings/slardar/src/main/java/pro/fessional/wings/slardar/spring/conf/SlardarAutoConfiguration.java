package pro.fessional.wings.slardar.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import pro.fessional.wings.slardar.spring.bean.SlardarCacheConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarDateTimeConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarDingNoticeConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarDoubleKillConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarEventConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarI18nConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarJacksonConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarMonitorConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarOkhttpConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarTweakConfiguration;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration
@ConditionalOnProperty(name = SlardarEnabledProp.Key$autoconf, havingValue = "true")
@ImportAutoConfiguration({
        SlardarCacheConfiguration.class,
        SlardarDateTimeConfiguration.class,
        SlardarDingNoticeConfiguration.class,
        SlardarDoubleKillConfiguration.class,
        SlardarEventConfiguration.class,
        SlardarI18nConfiguration.class,
        SlardarJacksonConfiguration.class,
        SlardarMonitorConfiguration.class,
        SlardarOkhttpConfiguration.class,
        SlardarTweakConfiguration.class,
})
public class SlardarAutoConfiguration {
}
