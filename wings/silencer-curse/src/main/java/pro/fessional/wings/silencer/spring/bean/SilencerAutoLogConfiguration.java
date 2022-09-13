package pro.fessional.wings.silencer.spring.bean;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.logging.LogLevel;
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
    public ApplicationRunner autoAddFilterLogbackConsole(SilencerMiranaProp prop) {
        log.info("Wings conf autoAddFilterLogbackConsole");
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

            final LogLevel ll = autoLog.getLevel() == null ? LogLevel.OFF : autoLog.getLevel();
            final Level level;
            // TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF
            switch (ll) {
                case TRACE:
                    level = Level.TRACE;
                    break;
                case DEBUG:
                    level = Level.DEBUG;
                    break;
                case INFO:
                    level = Level.INFO;
                    break;
                case WARN:
                    level = Level.WARN;
                    break;
                case ERROR:
                case FATAL:
                    level = Level.ERROR;
                    break;
                default:
                    level = Level.OFF;
            }

            log.info("Wings conf LogbackFilter to ConsoleAppender");
            log.info("================= Silencer =================");
            log.info("Auto Switch the following Appender Level to " + level);
            for (Appender<ILoggingEvent> appender : appenders) {
                log.info("- " + appender.getName() + " : " + appender.getClass().getName());
            }
            log.info("================= Silencer =================");
            final LogbackFilter filter = new LogbackFilter(level);
            for (Appender<ILoggingEvent> appender : appenders) {
                appender.addFilter(filter);
            }
        };
    }

    @RequiredArgsConstructor
    public static class LogbackFilter extends Filter<ILoggingEvent> {
        private final Level level;

        @Override
        public FilterReply decide(ILoggingEvent event) {
            if (event.getLevel().isGreaterOrEqual(level)) {
                return FilterReply.NEUTRAL;
            }
            else {
                return FilterReply.DENY;
            }
        }
    }
}
