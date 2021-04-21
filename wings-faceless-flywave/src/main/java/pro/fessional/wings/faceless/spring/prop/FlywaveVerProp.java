package pro.fessional.wings.faceless.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

/**
 * @author trydofor
 * @see #Key
 * @since 2019-05-30
 */
@Data
@ConfigurationProperties(FlywaveVerProp.Key)
public class FlywaveVerProp {

    public static final String Key = "wings.faceless.flywave.ver";

    /**
     * 版本管理表
     * @see #Key$schemaVersionTable
     */
    private String schemaVersionTable = "sys_schema_version";
    public static final String Key$schemaVersionTable = Key + ".schema-version-table";

    /**
     * 变更跟踪表
     * @see #Key$schemaJournalTable
     */
    private String schemaJournalTable = "sys_schema_journal";
    public static final String Key$schemaJournalTable = Key + ".schema-journal-table";

    /**
     * 是否开启标记（ask@）确认
     * @see #Key$askMark
     */
    private boolean askMark = true;
    public static final String Key$askMark = Key + ".ask-mark";

    /**
     * 是否开启降级确认
     * @see #Key$askUndo
     */
    private boolean askUndo = true;
    public static final String Key$askUndo = Key + ".ask-undo";

    /**
     * 是否开启Drop语句确认
     * @see #Key$askDrop
     */
    private boolean askDrop = true;
    public static final String Key$askDrop = Key + ".ask-drop";

    /**
     * Drop语句的正则
     * @see #Key$dropReg
     */
    private List<String> dropReg = Collections.emptyList();
    public static final String Key$dropReg = Key + ".drop-reg";

    /**
     * update journal table。支持一下变量
     * - PLAIN_NAME 目标表的`本表`名字
     * - TABLE_NAME 目标表名字，可能是本表，分表，跟踪表
     * - TABLE_BONE 目标表字段(至少包含名字，类型，注释)，不含索引和约束
     * - TABLE_PKEY 目标表的主键中字段名，用来创建原主键的普通索引。
     * @see #Key$journalUpdate
     */
    private String journalUpdate = "";
    public static final String Key$journalUpdate = Key + ".journal-update";

    /**
     * update journal trigger。
     * # before update trigger，独自跟踪表，不需要增加原主键索引
     * @see #Key$triggerUpdate
     */
    private String triggerUpdate = "";
    public static final String Key$triggerUpdate = Key + ".trigger-update";

    /**
     * delete journal table。支持一下变量
     * - PLAIN_NAME 目标表的`本表`名字
     * - TABLE_NAME 目标表名字，可能是本表，分表，跟踪表
     * - TABLE_BONE 目标表字段(至少包含名字，类型，注释)，不含索引和约束
     * - TABLE_PKEY 目标表的主键中字段名，用来创建原主键的普通索引。
     * @see #Key$journalDelete
     */
    private String journalDelete = "";
    public static final String Key$journalDelete = Key + ".journal-delete";

    /**
     * delete journal trigger。
     * # before update trigger，独自跟踪表，不需要增加原主键索引
     * @see #Key$triggerDelete
     */
    private String triggerDelete = "";
    public static final String Key$triggerDelete = Key + ".trigger-delete";
}
