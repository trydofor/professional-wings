package pro.fessional.wings.tiny.task.service;

/**
 * @author trydofor
 * @since 2022-12-26
 */
public interface TinyTaskBeatService {

    /**
     * Clean the history result of task
     */
    int cleanResult();

    /**
     * Check task health, should notice if return non-empty
     */
    String checkHealth();
}
