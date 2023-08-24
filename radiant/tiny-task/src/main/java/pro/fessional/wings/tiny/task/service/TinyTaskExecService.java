package pro.fessional.wings.tiny.task.service;

import java.util.Set;

/**
 * Launch and cancel tasks, global management, same id can only be started once
 *
 * @author trydofor
 * @since 2022-12-16
 */
public interface TinyTaskExecService {

    /**
     * launch a task
     */
    boolean launch(long id);

    /**
     * Force launch a task without scheduling
     */
    boolean force(long id);

    /**
     * Cancel a task. If the task does not exist, consider it as successful.
     * When the application restarts or relaunches, the task should be restored.
     */
    boolean cancel(long id);

    /**
     * Get all running tasks
     */
    Set<Long> running();
}
