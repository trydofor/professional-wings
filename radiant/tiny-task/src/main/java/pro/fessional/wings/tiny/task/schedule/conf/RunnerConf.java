package pro.fessional.wings.tiny.task.schedule.conf;

import lombok.Data;

/**
 * 以UTC Epoch记录的执行信息，可构造 TriggerContext
 *
 * @author trydofor
 * @see org.springframework.scheduling.TriggerContext
 * @since 2022-12-05
 */
@Data
public class RunnerConf {

    /**
     * 上次执行开始时间（epoch毫秒）
     */
    private long lastExec = 0;
    /**
     * 上次执行失败时间（epoch毫秒）
     */
    private long lastFail = 0;
    /**
     * 上次执行成功时间（epoch毫秒）
     */
    private long lastDone = 0;

    /**
     * 下次执行开始时间（epoch毫秒）默认为停止
     */
    private long nextExec = 0;

    /**
     * exec执行的乐观锁
     */
    private long nextLock = 0;

    /**
     * 连续失败次数
     */
    private int coreFail = 0;

    /**
     * 合计开始次数
     */
    private int sumsExec = 0;
    /**
     * 合计失败次数
     */
    private int sumsFail = 0;
    /**
     * 合计成功次数
     */
    private int sumsDone = 0;

}
