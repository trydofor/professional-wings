package pro.fessional.wings.tiny.task.service;

/**
 * @author trydofor
 * @since 2022-12-26
 */
public interface TinyTaskBeatService {

    /**
     * 清理task历史result
     */
    int cleanResult();

    /**
     * 检查任务健康状态，非空时发生通知
     */
    String checkHealth();
}
