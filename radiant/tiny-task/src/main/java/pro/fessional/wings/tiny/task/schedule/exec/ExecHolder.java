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
    public static NoticeExec<?> getNotice(@NotNull String bean, @NotNull Function<String, NoticeExec<?>> exec) {
        return Notice.computeIfAbsent(bean, exec);
    }

    @Contract("_,true->!null")
    public static NoticeExec<?> getNotice(String bean, boolean nonnull) {
        final NoticeExec<?> exec = StringUtils.isEmpty(bean) ? null : Notice.get(bean);
        if (nonnull && exec == null) {
            throw new IllegalStateException("notice not found, bean=" + bean);
        }
        return exec;
    }

    @NotNull
    public static TaskerExec getTasker(@NotNull String prop, @NotNull Function<String, TaskerExec> exec) {
        return Tasker.computeIfAbsent(prop, exec);
    }

    @Contract("_,true->!null")
    public static TaskerExec getTasker(String prop, boolean nonnull) {
        final TaskerExec exec = StringUtils.isEmpty(prop) ? null : Tasker.get(prop);
        if (nonnull && exec == null) {
            throw new IllegalStateException("tasker not found, prop=" + prop);
        }
        return exec;
    }
}
