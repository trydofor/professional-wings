package pro.fessional.wings.tiny.task.schedule.conf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TinyTask的配置项，一个taskerBean只能存在一条
 *
 * @author trydofor
 * @since 2022-12-09
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TaskerProp {
    /**
     * 是否可以注册及执行，不会使用Default配置
     */
    private boolean enabled = true;
    /**
     * 是否可以自动注册并启动，不会使用Default配置
     */
    private boolean autorun = true;
    /**
     * 版本号，版本高的配置覆盖版本低的，不会使用Default配置
     */
    private long version = 0;

    /**
     * 由TinyTasker注解的Bean，格式为Class#method，默认自动识别，不会使用Default配置
     */
    private String taskerBean = null;

    /**
     * 任务的参数，对象数组的json格式，默认null或空无参数，不会使用Default配置
     */
    private String taskerPara = null;

    /**
     * 任务名字，用于通知和日志，可读性好一些，默认为'[全类名#方法名]'，不会使用Default配置
     */
    private String taskerName = null;

    /**
     * 是否为轻任务，执行快，秒级完成，不会使用Default配置
     */
    private boolean taskerFast = true;

    /**
     * 所属程序，逗号分隔，推荐一个，使用spring.application.name，null及空时使用Default配置
     */
    private String taskerApps = null;

    public boolean notTaskerApps() {
        return taskerApps == null || taskerApps.isEmpty();
    }

    /**
     * 执行模式，RunMode(product|test|develop|local)，逗号分隔忽略大小写，空表示所有。null时使用Default配置
     *
     * @see pro.fessional.wings.silencer.modulate.RunMode
     */
    private String taskerRuns = null;

    public boolean notTaskerRuns() {
        return taskerRuns == null || taskerRuns.isEmpty();
    }

    /**
     * 通知Bean，SmallNotice类型，格式为Class，默认无通知。null及空时使用Default配置
     */
    private String noticeBean = null;

    public boolean notNoticeBean() {
        return noticeBean == null || noticeBean.isEmpty();
    }

    /**
     * <pre>
     * 通知的时机，exec|fail|done，逗号分隔忽略大小写，默认fail。null及空时使用Default配置
     * 时机大概表述为：exec;try{run...;done}catch{fail}
     * exec - 初始任务
     * done - 执行成功
     * fail - 执行失败
     * </pre>
     */
    private String noticeWhen = "fail";

    public boolean notNoticeWhen() {
        return noticeWhen == null || noticeWhen.isEmpty();
    }

    /**
     * 对noticeBean的默认配置的覆盖，默认为json格式，不做补充，null及空时使用Default配置
     */
    private String noticeConf = null;

    public boolean notNoticeConf() {
        return noticeConf == null || noticeConf.isEmpty();
    }

    /**
     * 调度时区的ZoneId格式，默认系统时区，null及空时使用Default配置
     */
    private String timingZone = null;

    public boolean notTimingZone() {
        return timingZone == null || timingZone.isEmpty();
    }

    /**
     * 调度表达式类型，影响timingCron的解析方式，默认为spring的cron格式，null及空时使用Default配置
     */
    private String timingType = null;

    public boolean notTimingType() {
        return timingType == null || timingType.isEmpty();
    }

    /**
     * 调度表达式内容，最高优先级，受timingType影响，默认spring cron格式（秒分时日月周），不会使用Default配置
     *
     * @see org.springframework.scheduling.annotation.Scheduled
     * @see org.springframework.scheduling.support.CronTrigger
     */
    private String timingCron = null;

    public boolean hasTimingCron() {
        return timingCron != null && !timingCron.isEmpty();
    }

    /**
     * 固定空闲相连（秒），优先级次于timingCron，相当于fixedDelay，结束到开始，0为无效，不会使用Default配置
     *
     * @see org.springframework.scheduling.annotation.Scheduled
     * @see org.springframework.scheduling.support.PeriodicTrigger
     */
    private Integer timingIdle = null;

    public boolean hasTimingIdle() {
        return timingIdle != null && timingIdle > 0;
    }

    /**
     * 固定频率开始（秒），优先级次于timingIdle，相当于fixedRate，开始到开始，0为无效，不会使用Default配置
     *
     * @see org.springframework.scheduling.annotation.Scheduled
     * @see org.springframework.scheduling.support.PeriodicTrigger
     */
    private Integer timingRate = null;

    public boolean hasTimingRate() {
        return timingRate != null && timingRate > 0;
    }

    /**
     * 错过调度（misfire）多少秒内，需要补救执行，0表示不补救，不会使用Default配置
     */
    private Integer timingMiss = null;

    /**
     * 调度开始的日期时间，timingZone时区，yyyy-MM-dd HH:mm:ss，0表示无效，不会使用Default配置
     */
    private String duringFrom = "";
    /**
     * 调度结束的日期时间，timingZone时区，yyyy-MM-dd HH:mm:ss，0表示无效，不会使用Default配置
     */
    private String duringStop = "";
    /**
     * 总计初始执行多少次后，结束调度，不会使用Default配置
     */
    private Integer duringExec = null;
    /**
     * 连续失败多少次后，结束调度，不会使用Default配置
     */
    private Integer duringFail = null;
    /**
     * 总计成功执行多少次后，结束调度，不会使用Default配置
     */
    private Integer duringDone = null;

    /**
     * 执行结果保存的天数，0为不保存，null时使用Default配置
     */
    private Integer resultKeep = null;

    public boolean notResultKeep() {
        return resultKeep == null;
    }

}
