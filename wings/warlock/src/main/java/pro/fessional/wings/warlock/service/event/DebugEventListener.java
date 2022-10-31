package pro.fessional.wings.warlock.service.event;

import org.springframework.boot.logging.LogLevel;
import org.springframework.context.event.EventListener;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.silencer.debug.DebugClock;
import pro.fessional.wings.silencer.debug.DebugLogger;
import pro.fessional.wings.silencer.debug.DebugStack;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.warlock.event.debug.DebugClockEvent;
import pro.fessional.wings.warlock.event.debug.DebugLoggerEvent;
import pro.fessional.wings.warlock.event.debug.DebugStackEvent;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author trydofor
 * @since 2022-10-31
 */
public class DebugEventListener implements TerminalContext.Listener {

    public static final long FixDate = Duration.ofDays(3650).toMillis();
    public static final long GlobalUid = Long.MAX_VALUE;

    private final ConcurrentHashMap<Long, Conf> debugs = new ConcurrentHashMap<>();
    private final Conf global = new Conf();

    @EventListener
    public void debugLogger(DebugLoggerEvent event) {
        final long uid = event.getUserId();
        final Conf conf = uid == GlobalUid ? global : debugs.computeIfAbsent(uid, k -> new Conf());
        final LogLevel lvl = event.getLevel();
        if (lvl == LogLevel.OFF) {
            DebugLogger.resetThread();
            conf.logger = null;
        }
        else {
            conf.logger = lvl;
        }
    }

    @EventListener
    public void debugStack(DebugStackEvent event) {
        final long uid = event.getUserId();
        final Conf conf = uid == GlobalUid ? global : debugs.computeIfAbsent(uid, k -> new Conf());
        final Boolean stack = event.getStack();
        if (stack == null) {
            DebugStack.resetThread();
        }
        conf.stack = stack;
    }

    @EventListener
    public void debugClock(DebugClockEvent event) {
        final long uid = event.getUserId();
        final Conf conf = uid == GlobalUid ? global : debugs.computeIfAbsent(uid, k -> new Conf());
        final long offset = event.getMills();
        if (offset == 0) {
            DebugClock.resetThread();
            conf.clock = null;
        }
        else {
            if (offset < FixDate) {
                conf.clock = Clock.offset(ThreadNow.TweakClock.defaultValue(true), Duration.ofMillis(offset));
            }
            else {
                conf.clock = Clock.fixed(Instant.ofEpochMilli(offset), ZoneId.systemDefault());
            }
        }
    }

    @Override
    public void onChange(boolean del, TerminalContext.Context ctx) {
        final Conf cur = debugs.getOrDefault(ctx.getUserId(), Null); // 当前用户

        final Clock cc = cur.clock;
        final LogLevel cl = cur.logger;
        final Boolean cs = cur.stack;

        if (del) {
            if (cc != null || global.clock != null) {
                DebugClock.resetThread();
            }
            if (cl != null || global.logger != null) {
                DebugLogger.resetThread();
            }
            if (cs != null || global.stack != null) {
                DebugStack.resetThread();
            }
            //
            if (cur != Null && cc == null && cl == null && cs == null) {
                debugs.remove(ctx.getUserId());
            }
        }
        else {
            final Clock dc = cc != null ? cc : global.clock;
            if (dc != null) {
                DebugClock.debugThread(dc);
            }
            final LogLevel dl = cl != null ? cl : global.logger;
            if (dl != null) {
                DebugLogger.debugThread(dl);
            }
            final Boolean ds = cs != null ? cs : global.stack;
            if (ds != null) {
                DebugStack.debugThread(ds);
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
