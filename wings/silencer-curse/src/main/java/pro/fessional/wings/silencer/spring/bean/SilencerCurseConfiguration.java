package pro.fessional.wings.silencer.spring.bean;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.modulate.RuntimeMode;
import pro.fessional.wings.silencer.runner.ApplicationInspectRunner;
import pro.fessional.wings.silencer.runner.ApplicationReadyEventRunner;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.silencer.spring.help.ApplicationContextHelper;
import pro.fessional.wings.silencer.spring.prop.SilencerAutoLogProp;
import pro.fessional.wings.silencer.spring.prop.SilencerEnabledProp;
import pro.fessional.wings.silencer.spring.prop.SilencerRuntimeProp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author trydofor
 * @since 2019-06-26
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class SilencerCurseConfiguration {

    private static final Log log = LogFactory.getLog(SilencerCurseConfiguration.class);

    /**
     * audit the file and cascading relationship of properties key/value
     */
    @Bean
    @ConditionalWingsEnabled(abs = SilencerEnabledProp.Key$auditProp, value = false)
    public ApplicationInspectRunner auditPropRunner() {
        log.info("SilencerCurse spring-bean auditPropRunner");
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

    /**
     * Configuration is complete and the log is switched before the service starts
     */
    @Bean
    @ConditionalWingsEnabled(abs = SilencerEnabledProp.Key$muteConsole)
    @ConditionalOnClass(ConsoleAppender.class)
    public ApplicationReadyEventRunner muteConsoleRunner(SilencerAutoLogProp autoLog) {
        log.info("SilencerCurse spring-runs muteConsoleRunner");
        return new ApplicationReadyEventRunner(WingsOrdered.Lv1Config, ignored -> {
            final Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
            final Set<String> targets = autoLog.getTarget();
            final Set<String> exists = autoLog.getExists();

            boolean skip = true;
            final Set<Appender<ILoggingEvent>> appenders = new HashSet<>();
            for (Iterator<Appender<ILoggingEvent>> iter = root.iteratorForAppenders(); iter.hasNext(); ) {
                Appender<ILoggingEvent> next = iter.next();
                final String name = next.getName();
                if (targets.contains(name)) {
                    appenders.add(next);
                    log.info("find target appender name=" + name);
                }
                else if (exists.contains(name)) {
                    log.info("find condition appender name=" + name);
                    skip = false;
                }
                else {
                    log.info("find others appender name=" + name);
                }
            }

            if (skip || appenders.isEmpty()) {
                log.info("skip auto-log appender");
                return;
            }

            final String level = autoLog.getLevel();
            log.info("================= Silencer =================");
            log.info("Auto Switch the following Appender Level to " + level);
            for (Appender<ILoggingEvent> appender : appenders) {
                log.info("- " + appender.getName() + " : " + appender.getClass().getName());
            }
            final ThresholdFilter tft = new ThresholdFilter();
            tft.setLevel(level);
            for (Appender<ILoggingEvent> appender : appenders) {
                appender.addFilter(tft);
            }
            log.info("================= Silencer =================");
            tft.start();
        });
    }

    @Bean
    @ConditionalWingsEnabled
    public RuntimeMode runtimeMode(SilencerRuntimeProp prop) {

        log.info("Silencer spring-auto runtimeMode");
        return new RuntimeMode(prop.getRunMode(), prop.getApiMode()) {};
    }
}
