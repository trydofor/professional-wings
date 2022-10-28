package pro.fessional.wings.silencer.debug;

import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.time.ThreadNow;

import java.time.Clock;
import java.time.Duration;

/**
 * @author trydofor
 * @since 2022-10-28
 */
public class NowDebug {

    // global
    public static void debugGlobal(@NotNull Duration offset) {
        ThreadNow.adaptSystem(offset);
    }

    public static void debugGlobal(@NotNull Clock clock) {
        ThreadNow.adaptSystem(clock);
    }

    public static void resetGlobal() {
        ThreadNow.resetSystem();
    }

    // thread
    public static void debugThread(@NotNull Duration offset) {
        ThreadNow.adaptThread(offset);
    }

    public static void debugThread(@NotNull Clock clock) {
        ThreadNow.adaptThread(clock);
    }

    public static void resetThread() {
        ThreadNow.resetThread();
    }
}
