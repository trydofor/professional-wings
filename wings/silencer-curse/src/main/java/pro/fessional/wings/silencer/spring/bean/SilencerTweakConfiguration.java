package pro.fessional.wings.silencer.spring.bean;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.spi.FilterReply;
import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.TtlMDCAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.logging.LoggerGroups;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.mirana.evil.ThreadLocalAttention;
import pro.fessional.mirana.pain.CodeException;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.silencer.spring.prop.SilencerTweakProp;
import pro.fessional.wings.silencer.tweak.TweakLogger;

import java.time.Clock;
import java.time.Duration;

/**
 * @author trydofor
 * @since 2022-10-27
 */
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class SilencerTweakConfiguration {
    private static final Log log = LogFactory.getLog(SilencerTweakConfiguration.class);

    private final SilencerTweakProp silencerTweakProp;

    @Autowired
    public void initCodeExceptionTweak() throws ThreadLocalAttention {
        final boolean stack = silencerTweakProp.isCodeStack();
        log.info("SilencerCurse spring-auto initCodeExceptionTweak with TransmittableThreadLocal, stack=" + stack);
        CodeException.TweakStack.initThread(new TransmittableThreadLocal<>());
        CodeException.TweakStack.initGlobal(stack);
    }

    @Autowired
    public void initThreadClockTweak() throws ThreadLocalAttention {
        final long ms = silencerTweakProp.getClockOffset();
        log.info("SilencerCurse spring-auto initThreadClockTweak with TransmittableThreadLocal, offset=" + ms);
        ThreadNow.TweakClock.initThread(new TransmittableThreadLocal<>());
        final Duration dr = Duration.ofMillis(ms);
        if (!dr.isZero()) {
            final Clock clock = ThreadNow.TweakClock.current(true);
            ThreadNow.TweakClock.initGlobal(Clock.offset(clock, dr));
        }
    }

    @Bean
    @ConditionalOnClass(FilterReply.class)
    public CommandLineRunner runnerLogbackTweak(LoggingSystem system, LoggerGroups groups) {
        log.info("SilencerCurse spring-runs runnerLogbackTweak, init TtlMDC");
        TtlMDCAdapter.initMdc();// 尽早初始化
        return args -> {
            if (silencerTweakProp.isMdcThreshold()) {
                log.info("SilencerCurse spring-conf runnerLogbackTweak WingsMdcThresholdFilter");
                LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
                lc.getTurboFilterList().add(0, TweakLogger.MdcThresholdFilter);
            }
            // 尽晚初始化
            log.info("SilencerCurse spring-conf runnerLogbackTweak TweakLogger");
            TweakLogger.initGlobal(system, groups);
        };
    }
}
