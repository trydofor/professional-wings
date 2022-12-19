package pro.fessional.wings.tiny.task.schedule.conf;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 关联了database及配置文件
 *
 * @author trydofor
 * @since 2022-12-17
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TaskerConf extends TaskerProp {
    private long id;
    private String propkey;

    public TaskerConf(TaskerProp prop) {
        super.enabled = prop.enabled;
        super.autorun = prop.autorun;
        super.version = prop.version;

        super.taskerBean = prop.taskerBean;
        super.taskerPara = prop.taskerPara;
        super.taskerName = prop.taskerName;
        super.taskerFast = prop.taskerFast;
        super.taskerApps = prop.taskerApps;
        super.taskerRuns = prop.taskerRuns;

        super.noticeBean = prop.noticeBean;
        super.noticeWhen = prop.noticeWhen;
        super.noticeConf = prop.noticeConf;

        super.timingZone = prop.timingZone;
        super.timingType = prop.timingType;
        super.timingCron = prop.timingCron;
        super.timingIdle = prop.timingIdle;
        super.timingRate = prop.timingRate;
        super.timingMiss = prop.timingMiss;

        super.duringFrom = prop.duringFrom;
        super.duringStop = prop.duringStop;
        super.duringExec = prop.duringExec;
        super.duringFail = prop.duringFail;
        super.duringDone = prop.duringDone;

        super.resultKeep = prop.resultKeep;
    }
}
