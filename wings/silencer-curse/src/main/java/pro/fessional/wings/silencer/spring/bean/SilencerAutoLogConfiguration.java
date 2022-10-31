package pro.fessional.wings.silencer.spring.bean;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.spring.prop.SilencerEnabledProp;
import pro.fessional.wings.silencer.spring.prop.SilencerMiranaProp;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author trydofor
 * @since 2019-06-26
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SilencerEnabledProp.Key$autoLog, havingValue = "true")
public class SilencerAutoLogConfiguration {

    private static final Log log = LogFactory.getLog(SilencerAutoLogConfiguration.class);

    @Bean
    @ConditionalOnClass(ConsoleAppender.class)
    public CommandLineRunner runnerSilenceLogbackConsole(SilencerMiranaProp prop) {
        log.info("SilencerCurse spring-runs runnerSilenceLogbackConsole");
        return args -> {
            final SilencerMiranaProp.AutoLog autoLog = prop.getAutoLog();
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
            log.info("Wings conf LogbackFilter to ConsoleAppender");
            log.info("================= Silencer =================");
            log.info("Auto Switch the following Appender Level to " + level);
            for (Appender<ILoggingEvent> appender : appenders) {
                log.info("- " + appender.getName() + " : " + appender.getClass().getName());
            }
            log.info("================= Silencer =================");
            final ThresholdFilter tft = new ThresholdFilter();
            tft.setLevel(level);
            for (Appender<ILoggingEvent> appender : appenders) {
                appender.addFilter(tft);
            }
        };
    }
}
