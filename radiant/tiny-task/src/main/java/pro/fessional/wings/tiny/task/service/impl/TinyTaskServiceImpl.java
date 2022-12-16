package pro.fessional.wings.tiny.task.service.impl;

import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import pro.fessional.wings.slardar.async.TaskSchedulerHelper;
import pro.fessional.wings.tiny.task.service.TinyTaskConfService;
import pro.fessional.wings.tiny.task.service.TinyTaskService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author trydofor
 * @since 2022-12-13
 */
@Service
public class TinyTaskServiceImpl implements TinyTaskService {

    @Setter(onMethod_ = {@Autowired})
    protected TinyTaskConfService tinyTaskConfService;

    @Override
    @NotNull
    public ThreadPoolTaskScheduler referScheduler(boolean fast) {
        return TaskSchedulerHelper.referScheduler(fast);
    }

    @Override
    @NotNull
    public List<Long> schedule(@NotNull Object taskerBean) {
        tinyTaskConfService.config(taskerBean);
        return new ArrayList<>();
    }

    @Override
    public long schedule(@NotNull Object taskerBean, @NotNull Method taskerCall, @Nullable Object taskerPara) {
        tinyTaskConfService.config(taskerBean, taskerCall, taskerPara);
        return 0;
    }
}
