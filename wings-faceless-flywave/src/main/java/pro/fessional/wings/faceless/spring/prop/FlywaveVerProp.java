package pro.fessional.wings.faceless.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

/**
 * @author trydofor
 * @since 2019-05-30
 */
@Data
@ConfigurationProperties("wings.faceless.flywave.ver")
public class FlywaveVerProp {
    /**
     * 是否开启标记（ask@）确认
     */
    private boolean askMark = true;
    /**
     * 是否开启降级确认
     */
    private boolean askUndo = true;
    /**
     * 是否开启Drop语句确认
     */
    private boolean askDrop = true;

    /**
     * Drop语句的正则
     */
    private List<String> dropReg = Collections.emptyList();

    /**
     * update journal table。
     * # #### 数据版本跟踪 ######
     * # `{{PLAIN_NAME}}` 目标表的`本表`名字
     * # `{{TABLE_NAME}}` 目标表名字，可能是本表，分表，跟踪表
     * # `{{TABLE_BONE}}` 目标表字段(至少包含名字，类型，注释)，不含索引和约束
     * # `{{TABLE_PKEY}}` 目标表的主键中字段名，用来创建原主键的普通索引。
     * # ######################
     */
    private String journalUpdate = "";

    /**
     * update journal trigger。
     * # before update trigger，独自跟踪表，不需要增加原主键索引
     */
    private String triggerUpdate = "";

    /**
     * delete journal table。
     * # #### 数据版本跟踪 ######
     * # `{{PLAIN_NAME}}` 目标表的`本表`名字
     * # `{{TABLE_NAME}}` 目标表名字，可能是本表，分表，跟踪表
     * # `{{TABLE_BONE}}` 目标表字段(至少包含名字，类型，注释)，不含索引和约束
     * # `{{TABLE_PKEY}}` 目标表的主键中字段名，用来创建原主键的普通索引。
     * # ######################
     */
    private String journalDelete = "";
    /**
     * delete journal trigger。
     * # before update trigger，独自跟踪表，不需要增加原主键索引
     */
    private String triggerDelete = "";
    /**
     * 版本管理表
     */
    private String schemaVersionTable = "sys_schema_version";
    /**
     * 变更跟踪表
     */
    private String schemaJournalTable = "sys_schema_journal";
}
