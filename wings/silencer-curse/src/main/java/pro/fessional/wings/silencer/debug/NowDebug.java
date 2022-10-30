package pro.fessional.wings.silencer.debug;

import org.jetbrains.annotations.NotNull;

import java.time.Clock;
import java.time.Duration;

import static pro.fessional.mirana.time.ThreadNow.TweakClock;

/**
 * @author trydofor
 * @since 2022-10-28
 */
public class NowDebug {

    // global
    public static void debugGlobal(@NotNull Duration offset) {
        if (!offset.isZero()) {
            final Clock clock = TweakClock.current(true);
            TweakClock.tweakGlobal(Clock.offset(clock, offset));
        }
    }

    public static void debugGlobal(@NotNull Clock clock) {
        TweakClock.tweakGlobal(clock);
    }

    public static void resetGlobal() {
        TweakClock.resetGlobal();
    }

    // thread
    public static void debugThread(@NotNull Duration offset) {
        if (!offset.isZero()) {
            final Clock clock = TweakClock.current(true);
            TweakClock.tweakThread(Clock.offset(clock, offset));
        }

    }

    public static void debugThread(@NotNull Clock clock) {
        TweakClock.tweakThread(clock);
    }

    public static void resetThread() {
        TweakClock.resetThread();
    }
}
