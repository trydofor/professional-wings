package pro.fessional.wings.silencer.spring.bean;

import ch.qos.logback.core.spi.FilterReply;
import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import pro.fessional.wings.silencer.debug.LoggerDebug;
import pro.fessional.wings.silencer.logback.WingsThresholdFilter;
import pro.fessional.wings.silencer.spring.prop.SilencerDebugProp;

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

    @Bean
    @ConditionalOnClass(FilterReply.class)
    public WingsThresholdFilter wingsThresholdFilter() {
        // TODO
        return null;
    }

    @Autowired
    public void autoCodeExceptionDebug() throws ThreadLocalAttention {
        final boolean stack = silencerDebugProp.isCodeStack();
        log.info("SilencerCurse spring-auto autoCodeExceptionDebug with TransmittableThreadLocal, stack=" + stack);
        CodeException.initThreadStack(new TransmittableThreadLocal<>());
        CodeException.initGlobalStack(stack);
    }

    @Autowired
    public void autoThreadNowDebug() throws ThreadLocalAttention {
        final long ms = silencerDebugProp.getClockOffset();
        log.info("SilencerCurse spring-auto autoThreadNowDebug with TransmittableThreadLocal, offset=" + ms);
        ThreadNow.initThread(new TransmittableThreadLocal<>());
        ThreadNow.initGlobal(Duration.ofMillis(ms));
    }

    @Bean
    @ConditionalOnClass(FilterReply.class)
    public CommandLineRunner autoLogbackDebug(LoggingSystem system, LoggerGroups groups) {
        log.info("SilencerCurse spring-runs autoLogbackDebug");
        return args -> {
            LoggerDebug.initGlobal(system, groups);
            // TODO
        };
    }
}
