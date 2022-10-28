package pro.fessional.wings.silencer.debug;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

    public static void cleanGlobal(@NotNull String name) {
        debugGlobal(name, null);
    }

    public static void cleanGlobal() {
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
    public LogLevel debugThread(@NotNull String name, @Nullable LogLevel level) {
        // TODO
        return null;
    }

    private static final String MdcLevelKey = "levelAssociatedWithMDCValue";
}
