package pro.fessional.wings.slardar.async;

import com.alibaba.ttl.TtlRunnable;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.task.TaskDecorator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ttl decorate first. then others
 *
 * @author trydofor
 * @since 2024-05-14
 */
@Getter @Setter
public class TtlTaskDecorator implements TaskDecorator {

    protected final List<TaskDecorator> decorators = new ArrayList<>();

    protected volatile boolean releaseTtlValueReferenceAfterRun = false;
    /**
     * false for dev, true for product
     */
    protected volatile boolean idempotent = true;

    public TtlTaskDecorator(Collection<? extends TaskDecorator> decorators) {
        add(decorators);
    }

    public void add(Collection<? extends TaskDecorator> decorators) {
        if (decorators == null) return;
        for (TaskDecorator decorator : decorators) {
            add(decorator);
        }
    }

    public void add(TaskDecorator decorator) {
        if (decorator != null && !(decorator instanceof TtlTaskDecorator)) {
            decorators.add(decorator);
        }
    }

    @Override
    @NotNull
    public Runnable decorate(@NotNull Runnable runnable) {
        // ttl decorate first
        runnable = TtlRunnable.get(runnable, releaseTtlValueReferenceAfterRun, idempotent);
        // other decorate
        for (TaskDecorator taskDecorator : decorators) {
            runnable = taskDecorator.decorate(runnable);
        }
        return runnable;
    }
}
