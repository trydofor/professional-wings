package pro.fessional.wings.silencer.tweak;

import org.jetbrains.annotations.NotNull;

import java.time.Clock;
import java.time.Duration;

import static pro.fessional.mirana.time.ThreadNow.TweakClock;

/**
 * @author trydofor
 * @since 2022-10-28
 */
public class TweakClock {

    // global
    public static void tweakGlobal(@NotNull Duration offset) {
        if (!offset.isZero()) {
            final Clock clock = TweakClock.current(true);
            TweakClock.tweakGlobal(Clock.offset(clock, offset));
        }
    }

    public static void tweakGlobal(@NotNull Clock clock) {
        TweakClock.tweakGlobal(clock);
    }

    public static void resetGlobal() {
        TweakClock.resetGlobal();
    }

    // thread
    public static void tweakThread(@NotNull Duration offset) {
        if (!offset.isZero()) {
            final Clock clock = TweakClock.current(true);
            TweakClock.tweakThread(Clock.offset(clock, offset));
        }

    }

    public static void tweakThread(@NotNull Clock clock) {
        TweakClock.tweakThread(clock);
    }

    public static void resetThread() {
        TweakClock.resetThread();
    }
}
