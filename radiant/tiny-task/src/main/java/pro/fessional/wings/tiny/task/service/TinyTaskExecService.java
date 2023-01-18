package pro.fessional.wings.tiny.task.service;

import java.util.Set;

/**
 * 执行和取消任务，全局管理，同一个id只能启动一次
 *
 * @author trydofor
 * @since 2022-12-16
 */
public interface TinyTaskExecService {

    /**
     * 调度一个任务
     */
    boolean launch(long id);

    /**
     * 强制执行一个任务，不记入调度
     */
    boolean force(long id);

    /**
     * 取消一个任务，若任务不存在视为成功。
     * 应用重启或再次launch时，任务恢复
     */
    boolean cancel(long id);

    /**
     * 获取经执行中任务id
     */
    Set<Long> running();
}
