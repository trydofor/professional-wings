package pro.fessional.wings.slardar.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pro.fessional.wings.slardar.spring.bean.SlardarAsyncConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarCacheConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarDateTimeConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarDingNoticeConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarDoubleKillConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarI18nConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarJacksonConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarMonitorConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarOkhttpConfiguration;
import pro.fessional.wings.slardar.spring.bean.SlardarTweakingConfiguration;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@AutoConfiguration(before = {TaskExecutionAutoConfiguration.class, TaskSchedulingAutoConfiguration.class})
@ConditionalOnProperty(name = SlardarEnabledProp.Key$autoconf, havingValue = "true")
@EnableConfigurationProperties(SlardarEnabledProp.class)
@ImportAutoConfiguration({
        SlardarAsyncConfiguration.class,
        SlardarCacheConfiguration.class,
        SlardarDateTimeConfiguration.class,
        SlardarDingNoticeConfiguration.class,
        SlardarDoubleKillConfiguration.class,
        SlardarI18nConfiguration.class,
        SlardarJacksonConfiguration.class,
        SlardarMonitorConfiguration.class,
        SlardarOkhttpConfiguration.class,
        SlardarTweakingConfiguration.class,
})
public class SlardarAutoConfiguration {
}
