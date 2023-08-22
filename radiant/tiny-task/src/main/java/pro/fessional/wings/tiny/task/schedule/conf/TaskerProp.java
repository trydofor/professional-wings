package pro.fessional.wings.tiny.task.schedule.conf;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TinyTask Config, A taskerBean can have only one
 *
 * @author trydofor
 * @since 2022-12-09
 */
@Data
@NoArgsConstructor
public class TaskerProp {
    /**
     * whether to register and execute, not use Default config.
     */
    protected boolean enabled = true;
    /**
     * whether to auto register and start, not use Default config.
     */
    protected boolean autorun = true;
    /**
     * version number, higher version config overrides lower one, not use Default config.
     */
    protected int version = 0;

    /**
     * Beans annotated by TinyTasker, formatted as Class#method,
     * automatically recognized by default, not use Default config.
     */
    protected String taskerBean = null;

    /**
     * Parameters of the task, object array in json format,
     * default null or no parameters, not use Default config.
     */
    protected String taskerPara = null;

    /**
     * Task name, used for notice and log, better readability,
     * default is `[shortClassName#method]`, not use Default config.
     */
    protected String taskerName = null;

    /**
     * Whether it is a light task, fast execution, completed in seconds, not use Default config.
     */
    protected boolean taskerFast = true;

    /**
     * The app it belongs to, comma separated,
     * use Default config if null or empty.
     */
    protected String taskerApps = null;

    public boolean notTaskerApps() {
        return taskerApps == null || taskerApps.isEmpty();
    }

    /**
     * RunMode(product|test|develop|local), Comma separated, ignore case, default all,
     * use Default config if null or empty.
     *
     * @see pro.fessional.wings.silencer.modulate.RunMode
     */
    protected String taskerRuns = "";

    public boolean notTaskerRuns() {
        return taskerRuns == null || taskerRuns.isEmpty();
    }

    /**
     * Notice bean, SmallNotice type, fullpath of Class, no notice by default.
     * use Default config if null or empty.
     */
    protected String noticeBean = "";

    public boolean notNoticeBean() {
        return noticeBean == null || noticeBean.isEmpty();
    }

    /**
     * <pre>
     * Timing of notice, exec|fail|done|feed, comma separated ignoring case, default fail.
     * use Default config if null or empty.
     *
     * * timing is roughly expressed: exec;try{run...;done}catch{fail}
     * * exec - init task; done - success; fail - failed; feed - non-empty return.
     * </pre>
     */
    protected String noticeWhen = "";

    public boolean notNoticeWhen() {
        return noticeWhen == null || noticeWhen.isEmpty();
    }

    /**
     * The config name of the notice bean, automatic by default. use Default config if empty.
     */
    protected String noticeConf = "";

    public boolean notNoticeConf() {
        return noticeConf == null || noticeConf.isEmpty();
    }

    /**
     * timezone of scheduling , default system timezone, use Default config if null or empty.
     */
    protected String timingZone = null;

    public boolean notTimingZone() {
        return timingZone == null || timingZone.isEmpty();
    }

    /**
     * scheduling expression type, affects how timingCron is parsed,
     * defaults to spring cron format, use Default config if null or empty.
     */
    protected String timingType = "";

    public boolean notTimingType() {
        return timingType == null || timingType.isEmpty();
    }

    /**
     * Scheduling expression content, highest priority, affected by timingType,
     * default spring cron format (second minute hour day month week), not use Default config.
     *
     * @see org.springframework.scheduling.annotation.Scheduled
     * @see org.springframework.scheduling.support.CronTrigger
     */
    protected String timingCron = "";

    public boolean hasTimingCron() {
        return timingCron != null && !timingCron.isEmpty();
    }

    /**
     * Fixed idle interval (seconds), lower priority than timingCron,
     * equal to fixedDelay, end to start, 0 means disable, not use Default config.
     *
     * @see org.springframework.scheduling.annotation.Scheduled
     * @see org.springframework.scheduling.support.PeriodicTrigger
     */
    protected int timingIdle = 0;

    public boolean hasTimingIdle() {
        return timingIdle > 0;
    }

    /**
     * Fixed frequency interval (seconds), lower priority than timingIdle,
     * equal to fixedRate, start to start, 0 means disable, not use Default config.
     *
     * @see org.springframework.scheduling.annotation.Scheduled
     * @see org.springframework.scheduling.support.PeriodicTrigger
     */
    protected int timingRate = 0;

    public boolean hasTimingRate() {
        return timingRate > 0;
    }

    /**
     * Whether no hasTimingCron, hasTimingIdle or hasTimingRate
     */
    public boolean notTimingPlan() {
        return !hasTimingCron() && !hasTimingIdle() && !hasTimingRate();
    }

    /**
     * Within how many seconds of a misfire, execution is required,
     * 0 means no execution. not use Default config.
     */
    protected int timingMiss = 0;

    /**
     * the interval seconds of heartbeat, if the task's last_exec is more
     * than 2 heartbeats away from now, it is considered as an exception. default auto to
     * take rate or idle maximum, cron needs to specify it by itself, not use Default config.
     */
    protected int timingBeat = 0;
    /**
     * schedule start datetime at timingZone, in yyyy-MM-dd HH:mm:ss format,
     * 0 means disable, not use Default config.
     */
    protected String duringFrom = "";
    /**
     * schedule stop datetime at timingZone, in yyyy-MM-dd HH:mm:ss format,
     * 0 means disable, not use Default config.
     */
    protected String duringStop = "";
    /**
     * stop schedule after how many total executions, not use Default config.
     */
    protected int duringExec = 0;
    /**
     * stop schedule after how many consecutive failures, not use Default config.
     */
    protected int duringFail = 0;
    /**
     * stop schedule after how many successful executions, not use Default config.
     */
    protected int duringDone = 0;
    /**
     * recount each time the app is started, and stop schedule after how many
     * successful executions, disable by default, not use Default config.
     */
    protected int duringBoot = 0;

    /**
     * how many days to save the execution results, default 60 days,
     * 0 means not save, use Default configuration if null.
     */
    protected int resultKeep = 0;

    public boolean notResultKeep() {
        return resultKeep == 0;
    }

}
