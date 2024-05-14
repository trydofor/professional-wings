package pro.fessional.wings.slardar.async;

import com.alibaba.ttl.threadpool.TtlExecutors;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * use Ttl ThreadPool
 *
 * @author trydofor
 * @since 2022-12-03
 */
public class TtlThreadPoolTaskScheduler extends ThreadPoolTaskScheduler {

    @Override
    protected ScheduledExecutorService createExecutor(int poolSize, ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        ScheduledExecutorService executor = super.createExecutor(poolSize, threadFactory, rejectedExecutionHandler);
        return TtlExecutors.getTtlScheduledExecutorService(executor);
    }
}
