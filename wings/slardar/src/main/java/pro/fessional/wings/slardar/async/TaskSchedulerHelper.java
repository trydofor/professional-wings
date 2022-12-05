package pro.fessional.wings.slardar.async;

import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author trydofor
 * @since 2022-12-05
 */
public class TaskSchedulerHelper {

    protected static ThreadPoolTaskScheduler LightTasker;
    protected static ThreadPoolTaskScheduler HeavyTasker;

    @NotNull
    public static ThreadPoolTaskScheduler Light() {
        if (LightTasker == null) {
            throw new IllegalStateException("Must Init before using");
        }
        return LightTasker;
    }

    @NotNull
    public static ThreadPoolTaskScheduler Heavy() {
        if (HeavyTasker == null) {
            throw new IllegalStateException("Must Init before using");
        }

        return HeavyTasker;
    }
}
