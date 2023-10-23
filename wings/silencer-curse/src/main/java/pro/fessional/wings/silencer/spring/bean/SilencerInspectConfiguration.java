package pro.fessional.wings.silencer.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.runner.ApplicationInspectRunner;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.silencer.spring.help.ApplicationContextHelper;
import pro.fessional.wings.silencer.spring.prop.SilencerInspectProp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author trydofor
 * @since 2022-10-27
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SilencerInspectProp.Key$properties, havingValue = "true")
@EnableConfigurationProperties(SilencerInspectProp.class)
public class SilencerInspectConfiguration {
    private static final Log log = LogFactory.getLog(SilencerInspectConfiguration.class);

    @Bean
    public ApplicationInspectRunner inspectApplicationRunner() {
        log.info("SilencerCurse spring-bean inspectApplicationRunner");
        return new ApplicationInspectRunner(WingsOrdered.Lv5Supervisor, ignored -> {
            final Map<String, List<String>> map = ApplicationContextHelper.listPropertySource();
            final Map<String, List<String>> key = new LinkedHashMap<>();

            for (Map.Entry<String, List<String>> en : map.entrySet()) {
                for (String k : en.getValue()) {
                    key.computeIfAbsent(k, ignoreK -> new ArrayList<>()).add(en.getKey());
                }
            }

            for (Map.Entry<String, List<String>> en : key.entrySet()) {
                final List<String> vs = en.getValue();
                final String k = en.getKey();
                log.info(k + "=" + ApplicationContextHelper.getProperties(k));

                int c = 0;
                for (String v : vs) {
                    if (c++ == 0) {
                        log.info("+ " + v);
                    }
                    else {
                        log.info("- " + v);
                    }
                }
            }
        });
    }
}
