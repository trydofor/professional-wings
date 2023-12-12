package pro.fessional.wings.silencer.spring.bean;

import ch.qos.logback.classic.LoggerContext;
import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.TtlMDCAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggerGroups;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.annotation.Configuration;
import pro.fessional.mirana.evil.ThreadLocalAttention;
import pro.fessional.mirana.pain.CodeException;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.silencer.spring.prop.SilencerEnabledProp;
import pro.fessional.wings.silencer.spring.prop.SilencerTweakProp;
import pro.fessional.wings.silencer.tweak.TweakLogger;

import java.time.Clock;
import java.time.Duration;

/**
 * @author trydofor
 * @since 2022-10-27
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class SilencerTweakConfiguration {
    private static final Log log = LogFactory.getLog(SilencerTweakConfiguration.class);

    @Configuration(proxyBeanMethods = false)
    @ConditionalWingsEnabled(abs = SilencerEnabledProp.Key$tweakClock)
    public static class ClockWired {
        @Autowired
        public void auto(SilencerTweakProp prop) throws ThreadLocalAttention {
            final long ms = prop.getClockOffset();
            log.info("SilencerCurse spring-auto initThreadClockTweak with TransmittableThreadLocal, offset=" + ms);
            ThreadNow.TweakClock.initThread(new TransmittableThreadLocal<>(), false);
            final Duration dr = Duration.ofMillis(ms);
            if (!dr.isZero()) {
                final Clock clock = ThreadNow.TweakClock.current(true);
                ThreadNow.TweakClock.initDefault(Clock.offset(clock, dr));
            }
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalWingsEnabled(abs = SilencerEnabledProp.Key$tweakLogback)
    public static class LogbackWired {
        @Autowired
        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
        public void auto(SilencerTweakProp prop,
                         LoggingSystem loggingSystem,
                         LoggerGroups loggerGroups,
                         @Value("${debug:false}") boolean debug,
                         @Value("${trace:false}") boolean trace
        ) {
            log.info("SilencerCurse spring-auto autowireLogbackTweak, init TtlMDC");
            TtlMDCAdapter.initMdc();// init as early as possible

            if (prop.isMdcThreshold()) {
                log.info("SilencerCurse spring-conf autowireLogbackTweak WingsMdcThresholdFilter");
                LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
                lc.getTurboFilterList().add(0, TweakLogger.MdcThresholdFilter);
            }
            // init as late as possible
            final LogLevel core;
            if (debug) {
                core = LogLevel.DEBUG;
            }
            else if (trace) {
                core = LogLevel.TRACE;
            }
            else {
                core = null;
            }
            log.info("SilencerCurse spring-conf autowireLogbackTweak TweakLogger, coreLevel=" + core);
            TweakLogger.initGlobal(loggingSystem, loggerGroups, core);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalWingsEnabled(abs = SilencerEnabledProp.Key$tweakStack)
    public static class StackWired {
        @Autowired
        public void auto(SilencerTweakProp prop) throws ThreadLocalAttention {
            log.info("SilencerCurse spring-auto initCodeExceptionTweak with TransmittableThreadLocal, stack=" + prop.isCodeStack());
            CodeException.TweakStack.initThread(new TransmittableThreadLocal<>(), false);
            CodeException.TweakStack.initDefault(prop::isCodeStack);
        }
    }
}
