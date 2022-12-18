package pro.fessional.wings.tiny.task.schedule.conf;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 关联了database及配置文件
 *
 * @author trydofor
 * @since 2022-12-17
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TaskerConf extends TaskerProp {
    private long id;
    private String prop;
}
