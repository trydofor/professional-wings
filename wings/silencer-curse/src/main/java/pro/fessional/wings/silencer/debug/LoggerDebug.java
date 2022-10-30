package pro.fessional.wings.silencer.debug;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.DynamicThresholdFilter;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggerConfiguration;
import org.springframework.boot.logging.LoggerGroup;
import org.springframework.boot.logging.LoggerGroups;
import org.springframework.boot.logging.LoggingSystem;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author trydofor
 * @since 2022-10-27
 */
public class LoggerDebug {

    private static LoggingSystem loggingSystem = null;
    private static LoggerGroups loggerGroups = null;

    public static void initGlobal(LoggingSystem system, LoggerGroups groups) {
        loggingSystem = system;
        loggerGroups = groups;
    }

    // global
    private static final ConcurrentHashMap<String, LogLevel> GlobalLevel = new ConcurrentHashMap<>();

    public static void debugGlobal(@Nullable LogLevel level) {
        debugGlobal(LoggingSystem.ROOT_LOGGER_NAME, level);
    }

    public static void debugGlobal(@NotNull String name, @Nullable LogLevel level) {
        if (loggingSystem == null) {
            throw new IllegalStateException("must initLogging first");
        }

        if (level == null) {
            // try reset
            level = GlobalLevel.get(name);
        }

        if (loggerGroups != null) {
            LoggerGroup group = loggerGroups.get(name);
            if (group != null) {
                GlobalLevel.putIfAbsent(name, group.getConfiguredLevel());
                group.configureLogLevel(level, loggingSystem::setLogLevel);
                return;
            }
        }

        LoggerConfiguration configuration = loggingSystem.getLoggerConfiguration(name);
        GlobalLevel.putIfAbsent(name, configuration.getEffectiveLevel());
        loggingSystem.setLogLevel(name, level);
    }

    public static void resetGlobal(@NotNull String name) {
        debugGlobal(name, null);
    }

    public static void resetGlobal() {
        final Set<String> keys = new HashSet<>();
        // copy
        final Enumeration<String> it = GlobalLevel.keys();
        while (it.hasMoreElements()) {
            keys.add(it.nextElement());
        }
        // deal
        for (String key : keys) {
            debugGlobal(key, null);
        }
    }

    // thread

    /**
     * value为Level的文字明，大写
     */
    public static final String LevelKey = "WINGS_DEBUG_LEVEL";
    /**
     * value为logger名，区分大小写。比较逻辑为互相contains即可。
     */
    public static final String LoggerKey = "WINGS_DEBUG_LOGGER";

    /**
     * 根据MDC中的logger name和 level过滤
     *
     * @see DynamicThresholdFilter
     */
    public static final TurboFilter MdcThresholdFilter = new TurboFilter() {
        @Override
        public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
            final Level lvl = Level.toLevel(MDC.get(LevelKey));
            if (level.isGreaterOrEqual(lvl)) return FilterReply.ACCEPT;

            final String lgn = MDC.get(LoggerKey);
            if (lgn != null && !lgn.isEmpty()) {
                final String nm = logger.getName();
                if (nm.contains(lgn) || lgn.contains(nm)) {
                    return FilterReply.ACCEPT;
                }
            }

            return FilterReply.NEUTRAL;
        }
    };

    public static void debugThread(@Nullable LogLevel level) {
        if (level == null) {
            MDC.remove(LevelKey);
        }
        else {
            final String lvl;
            if (level == LogLevel.FATAL) {
                lvl = "ERROR";
            }
            else {
                lvl = level.name();
            }
            MDC.put(LevelKey, lvl);
        }
    }

    public static void debugThread(@NotNull String name, @Nullable LogLevel level) {
        debugThread(level);
        MDC.put(LoggerKey, name);
    }

    public static void resetThread() {
        MDC.remove(LevelKey);
        MDC.remove(LoggerKey);
    }

}
