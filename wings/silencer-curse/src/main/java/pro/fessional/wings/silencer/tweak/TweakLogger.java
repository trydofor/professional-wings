package pro.fessional.wings.silencer.tweak;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.DynamicThresholdFilter;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggerConfiguration;
import org.springframework.boot.logging.LoggerGroup;
import org.springframework.boot.logging.LoggerGroups;
import org.springframework.boot.logging.LoggingSystem;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.boot.logging.LoggingSystem.ROOT_LOGGER_NAME;

/**
 * <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.logging">Logging</a>
 *
 * @author trydofor
 * @see LoggingApplicationListener
 * @since 2022-10-27
 */
public class TweakLogger {

    private static LoggingSystem loggingSystem = null;
    private static LoggerGroups loggerGroups = null;
    private static LogLevel CoreLevel = null;

    private static Set<String> LazyCoreLevel = null;

    public static void initGlobal(LoggingSystem system, LoggerGroups groups, LogLevel core) {
        loggingSystem = system;
        loggerGroups = groups;

        if (core != null) {
            CoreLevel = core;
            if (LazyCoreLevel != null) {
                for (String name : LazyCoreLevel) {
                    tweakGlobal(name, core, false);
                }
            }
        }

        if (LazyCoreLevel != null) {
            LazyCoreLevel.clear();
            LazyCoreLevel = null;
        }
    }

    /**
     * trace=true
     * debug=true
     * When the debug mode is enabled,
     * a selection of core loggers (embedded container, Hibernate, and Spring Boot)
     * are configured to output more information.
     * Enabling the debug mode does not configure your application to
     * log all messages with DEBUG level.
     */
    public static LogLevel getCoreLevel() {
        return CoreLevel;
    }

    public static void asCoreLevel(@NotNull String name) {
        if (loggingSystem != null) {
            if (CoreLevel != null) {
                tweakGlobal(name, CoreLevel, false);
            }
        }
        else {
            if (LazyCoreLevel == null) {
                LazyCoreLevel = new HashSet<>();
            }
            LazyCoreLevel.add(name);
        }
    }

    // global
    private static final ConcurrentHashMap<String, LogLevel> GlobalLevel = new ConcurrentHashMap<>();

    public static void tweakGlobal(@Nullable LogLevel level) {
        tweakGlobal(ROOT_LOGGER_NAME, level);
    }

    public static void tweakGlobal(@NotNull String name, @Nullable LogLevel level) {
        tweakGlobal(name, level, true);
    }

    public static void tweakGlobal(@NotNull String name, @Nullable LogLevel level, boolean cache) {
        if (loggingSystem == null) {
            throw new IllegalStateException("must initLogging first");
        }

        if (level == null) {
            level = GlobalLevel.get(name);
        }

        if (loggerGroups != null) {
            LoggerGroup group = loggerGroups.get(name);
            if (group != null) {
                GlobalLevel.put(name, group.getConfiguredLevel());
                group.configureLogLevel(level, loggingSystem::setLogLevel);
                return;
            }
        }

        if (cache) {
            LoggerConfiguration configuration = loggingSystem.getLoggerConfiguration(name);
            GlobalLevel.put(name, configuration.getEffectiveLevel());
        }
        //
        loggingSystem.setLogLevel(name, level);
    }

    public static void resetGlobal(@NotNull String name) {
        tweakGlobal(name, null);
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
            tweakGlobal(key, null);
        }
    }

    @NotNull
    public static LogLevel globalLevel(@NotNull String name) {
        if (loggingSystem == null) {
            throw new IllegalStateException("must initLogging first");
        }
        return GlobalLevel.computeIfAbsent(name, k -> {
            if (loggerGroups != null) {
                LoggerGroup group = loggerGroups.get(name);
                if (group != null) return group.getConfiguredLevel();
            }

            LoggerConfiguration configuration = loggingSystem.getLoggerConfiguration(name);
            return configuration.getEffectiveLevel();
        });
    }

    // thread

    /**
     * The string value of Level, capitalized
     */
    public static final String LevelKey = "WINGS_DEBUG_LEVEL";
    /**
     * the string value of logger name, case-insensitive.
     * Comparison logic is A.contains(B) or B.contains(A)
     */
    public static final String LoggerKey = "WINGS_DEBUG_LOGGER";

    /**
     * Filter by logger name and level in the MDC
     *
     * @see DynamicThresholdFilter
     */
    public static final TurboFilter MdcThresholdFilter = new TurboFilter() {
        @Override
        public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
            final Level lvl = Level.toLevel(MDC.get(LevelKey), null);
            if (lvl != null) {
                final FilterReply rpl = level.isGreaterOrEqual(lvl) ? FilterReply.ACCEPT : FilterReply.DENY;
                final String lgn = MDC.get(LoggerKey);
                if (lgn == null || lgn.isEmpty() || ROOT_LOGGER_NAME.equalsIgnoreCase(lgn)) {
                    return rpl;
                }

                final String nm = logger.getName();
                if (nm.contains(lgn) || lgn.contains(nm)) {
                    return rpl;
                }
            }
            return FilterReply.NEUTRAL;
        }
    };

    /**
     * Set the new log level for root, but if level is null or OFF, reset to the original level.
     */
    public static void tweakThread(@Nullable LogLevel level) {
        tweakThread(ROOT_LOGGER_NAME, level);
    }

    /**
     * Set the new log level for logger, but if level is null or OFF, reset to the original level.
     * tweak root level if the name is ROOT or empty.
     */
    public static void tweakThread(@NotNull String name, @Nullable LogLevel level) {
        if (level == null || level == LogLevel.OFF) {
            resetThread();
            return;
        }

        final String lvl;
        if (level == LogLevel.FATAL) {
            lvl = "ERROR";
        }
        else {
            lvl = level.name();
        }
        MDC.put(LevelKey, lvl);

        if (!name.isEmpty()) {
            MDC.put(LoggerKey, name);
        }
    }

    public static void resetThread() {
        MDC.remove(LevelKey);
        MDC.remove(LoggerKey);
    }


    @Nullable
    public static LogLevel threadLevel() {
        final String lvl = MDC.get(LevelKey);
        if (lvl == null || lvl.isEmpty()) return null;
        for (LogLevel v : LogLevel.values()) {
            if (v.name().equalsIgnoreCase(lvl)) return v;
        }
        return null;
    }

    @NotNull
    public static LogLevel currentLevel(@NotNull String name) {
        final LogLevel tvl = threadLevel();
        if (tvl != null) return tvl;

        final LogLevel gvl = globalLevel(name);
        if (CoreLevel != null && gvl.ordinal() > CoreLevel.ordinal()) {
            return CoreLevel;
        }
        else {
            return gvl;
        }
    }
}
