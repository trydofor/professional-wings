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
     * 启动一个任务
     */
    boolean launch(long id);

    /**
     * 取消一个任务，若任务不存在视为成功。
     */
    boolean cancel(long id);

    /**
     * 获取经执行中任务id
     */
    Set<Long> running();
}
