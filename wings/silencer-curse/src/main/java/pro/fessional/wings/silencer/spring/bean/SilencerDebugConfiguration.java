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
import pro.fessional.wings.silencer.debug.DebugLogger;
import pro.fessional.wings.silencer.spring.prop.SilencerDebugProp;

import java.time.Clock;
import java.time.Duration;

/**
 * @author trydofor
 * @since 2022-10-27
 */
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class SilencerDebugConfiguration {
    private static final Log log = LogFactory.getLog(SilencerDebugConfiguration.class);

    private final SilencerDebugProp silencerDebugProp;

    @Autowired
    public void initCodeExceptionDebug() throws ThreadLocalAttention {
        final boolean stack = silencerDebugProp.isCodeStack();
        log.info("SilencerCurse spring-auto initCodeExceptionDebug with TransmittableThreadLocal, stack=" + stack);
        CodeException.TweakStack.initThread(new TransmittableThreadLocal<>());
        CodeException.TweakStack.initGlobal(stack);
    }

    @Autowired
    public void initThreadClockDebug() throws ThreadLocalAttention {
        final long ms = silencerDebugProp.getClockOffset();
        log.info("SilencerCurse spring-auto initThreadClockDebug with TransmittableThreadLocal, offset=" + ms);
        ThreadNow.TweakClock.initThread(new TransmittableThreadLocal<>());
        final Duration dr = Duration.ofMillis(ms);
        if (!dr.isZero()) {
            final Clock clock = ThreadNow.TweakClock.current(true);
            ThreadNow.TweakClock.initGlobal(Clock.offset(clock, dr));
        }
    }

    @Bean
    @ConditionalOnClass(FilterReply.class)
    public CommandLineRunner runnerLogbackDebug(LoggingSystem system, LoggerGroups groups) {
        log.info("SilencerCurse spring-runs runnerLogbackDebug, init TtlMDC");
        TtlMDCAdapter.initMdc();// 尽早初始化
        return args -> {
            if (silencerDebugProp.isMdcThreshold()) {
                log.info("SilencerCurse spring-conf autoLogbackDebug WingsMdcThresholdFilter");
                LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
                lc.getTurboFilterList().add(0, DebugLogger.MdcThresholdFilter);
            }
            // 尽晚初始化
            log.info("SilencerCurse spring-conf autoLogbackDebug LoggerDebug");
            DebugLogger.initGlobal(system, groups);
        };
    }
}
