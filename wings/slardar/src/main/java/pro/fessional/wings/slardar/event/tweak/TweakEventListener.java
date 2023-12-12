package pro.fessional.wings.slardar.event.tweak;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.event.EventListener;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.silencer.tweak.TweakClock;
import pro.fessional.wings.silencer.tweak.TweakLogger;
import pro.fessional.wings.silencer.tweak.TweakStack;
import pro.fessional.wings.slardar.context.TerminalContext;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author trydofor
 * @since 2022-10-31
 */
public class TweakEventListener implements TerminalContext.Listener {

    public static final long FixDate = Duration.ofDays(3650).toMillis();
    public static final long GlobalUid = Long.MAX_VALUE;

    private final ConcurrentHashMap<Long, Conf> debugs = new ConcurrentHashMap<>();
    private final Conf global = new Conf();

    @EventListener
    public void tweakLogger(TweakLoggerEvent event) {
        final long uid = event.getUserId();
        final Conf conf = uid == GlobalUid ? global : debugs.computeIfAbsent(uid, k -> new Conf());
        final LogLevel lvl = event.getLevel();
        if (lvl == LogLevel.OFF) {
            TweakLogger.resetThread();
            conf.logger = null;
        }
        else {
            conf.logger = lvl;
        }
    }

    @EventListener
    public void tweakStack(TweakStackEvent event) {
        final long uid = event.getUserId();
        final Conf conf = uid == GlobalUid ? global : debugs.computeIfAbsent(uid, k -> new Conf());
        final Boolean stack = event.getStack();
        if (stack == null) {
            TweakStack.resetThread();
        }
        conf.stack = stack;
    }

    @EventListener
    public void tweakClock(TweakClockEvent event) {
        final long uid = event.getUserId();
        final Conf conf = uid == GlobalUid ? global : debugs.computeIfAbsent(uid, k -> new Conf());
        final long offset = event.getMills();
        if (offset == 0) {
            TweakClock.resetThread();
            conf.clock = null;
        }
        else {
            if (offset < FixDate) {
                conf.clock = Clock.offset(ThreadNow.TweakClock.defaultValue(true), Duration.ofMillis(offset));
            }
            else {
                conf.clock = Clock.fixed(Instant.ofEpochMilli(offset), ThreadNow.sysZoneId());
            }
        }
    }

    @Override
    public void onChange(boolean del, TerminalContext.@NotNull Context ctx) {
        final Conf cur = debugs.getOrDefault(ctx.getUserId(), Null); // current user

        final Clock cc = cur.clock;
        final LogLevel cl = cur.logger;
        final Boolean cs = cur.stack;

        if (del) {
            if (cc != null || global.clock != null) {
                TweakClock.resetThread();
            }
            if (cl != null || global.logger != null) {
                TweakLogger.resetThread();
            }
            if (cs != null || global.stack != null) {
                TweakStack.resetThread();
            }
            //
            if (cur != Null && cc == null && cl == null && cs == null) {
                debugs.remove(ctx.getUserId());
            }
        }
        else {
            final Clock dc = cc != null ? cc : global.clock;
            if (dc != null) {
                TweakClock.tweakThread(dc);
            }
            final LogLevel dl = cl != null ? cl : global.logger;
            if (dl != null) {
                TweakLogger.tweakThread(dl);
            }
            final Boolean ds = cs != null ? cs : global.stack;
            if (ds != null) {
                TweakStack.tweakThread(ds);
            }
        }
    }

    private static final Conf Null = new Conf();

    private static class Conf {
        private transient Clock clock;
        private transient LogLevel logger;
        private transient Boolean stack;
    }
}
