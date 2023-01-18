package pro.fessional.wings.tiny.task.schedule.exec;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author trydofor
 * @since 2022-12-16
 */
public class ExecHolder {

    private static final ConcurrentHashMap<String, NoticeExec<?>> Notice = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, TaskerExec> Tasker = new ConcurrentHashMap<>();

    @NotNull
    public static NoticeExec<?> getNotice(@NotNull String token, @NotNull Function<String, NoticeExec<?>> exec) {
        return Notice.computeIfAbsent(token, exec);
    }

    @NotNull
    public static TaskerExec getTasker(@NotNull String token, @NotNull Function<String, TaskerExec> exec) {
        return Tasker.computeIfAbsent(token, exec);
    }

    @Contract("_,true->!null")
    public static NoticeExec<?> getNotice(String token, boolean nonnull) {
        final NoticeExec<?> exec = StringUtils.isEmpty(token) ? null : Notice.get(token);
        if (nonnull && exec == null) {
            throw new IllegalStateException("notice not found, token=" + token);
        }
        return exec;
    }

    @Contract("_,true->!null")
    public static TaskerExec getTasker(String token, boolean nonnull) {
        final TaskerExec exec = StringUtils.isEmpty(token) ? null : Tasker.get(token);
        if (nonnull && exec == null) {
            throw new IllegalStateException("tasker not found, token=" + token);
        }
        return exec;
    }
}
