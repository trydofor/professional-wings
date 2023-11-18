package pro.fessional.wings.tiny.task.service.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.async.TaskSchedulerHelper;
import pro.fessional.wings.tiny.task.service.TinyTaskConfService;
import pro.fessional.wings.tiny.task.service.TinyTaskConfService.Conf;
import pro.fessional.wings.tiny.task.service.TinyTaskExecService;
import pro.fessional.wings.tiny.task.service.TinyTaskService;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author trydofor
 * @since 2022-12-13
 */
@Service
@ConditionalWingsEnabled
@Slf4j
public class TinyTaskServiceImpl implements TinyTaskService {

    @Setter(onMethod_ = {@Autowired})
    protected TinyTaskConfService tinyTaskConfService;

    @Setter(onMethod_ = {@Autowired})
    protected TinyTaskExecService tinyTaskExecService;

    @Override
    @NotNull
    public ThreadPoolTaskScheduler referScheduler(boolean fast) {
        return TaskSchedulerHelper.referScheduler(fast);
    }

    @Override
    @NotNull
    public Set<Task> schedule(@NotNull Object taskerBean) {
        final Set<Conf> conf = tinyTaskConfService.config(taskerBean);
        final Set<Task> rst = new HashSet<>();
        for (Conf cnf : conf) {
            if (cnf.isEnabled() && cnf.isAutorun()) {
                final boolean cd = tinyTaskExecService.launch(cnf.getId());
                log.info("schedule task {}, scheduled={}", cnf, cd);
                rst.add(new Task(cnf.getId(), cd));
            }
            else {
                log.info("skip task {}", cnf);
            }
        }
        return rst;
    }

    @Override
    public Task schedule(@NotNull Object taskerBean, @NotNull Method taskerCall, @Nullable Object taskerPara) {
        final Conf cnf = tinyTaskConfService.config(taskerBean, taskerCall, taskerPara);
        final boolean cd;
        if (cnf.isEnabled()) {
            cd = tinyTaskExecService.launch(cnf.getId());
            log.info("schedule task {}, scheduled={}", cnf, cd);
        }
        else {
            cd = false;
            log.info("skip task {}", cnf);
        }

        return new Task(cnf.getId(), cd);
    }
}
