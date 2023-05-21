package pro.fessional.wings.faceless.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Map;

/**
 * <pre>
 * set version and journal table for Flywave.
 * - `{{PLAIN_NAME}}` The `plain` table name of the target table
 * - `{{TABLE_NAME}}` Target table name, can be plain, shard, trace table
 * - `{{TABLE_BONE}}` Target table field (at least name, type, comments), without indexes and constraints
 * - `{{TABLE_PKEY}}` The field name in PK of the target table, used to create a normal index copy from the original PK
 * </pre>
 *
 * @author trydofor
 * @see #Key
 * @since 2019-05-30
 */
@Data
@ConfigurationProperties(FlywaveVerProp.Key)
public class FlywaveVerProp {

    public static final String Key = "wings.faceless.flywave.ver";

    /**
     * table name of schema version.
     *
     * @see #Key$schemaVersionTable
     */
    private String schemaVersionTable = "sys_schema_version";
    public static final String Key$schemaVersionTable = Key + ".schema-version-table";

    /**
     * table name of journal.
     *
     * @see #Key$schemaJournalTable
     */
    private String schemaJournalTable = "sys_schema_journal";
    public static final String Key$schemaJournalTable = Key + ".schema-journal-table";

    /**
     * RegExp is treated as drop statements for dangerous confirm.
     *
     * @see #Key$dropReg
     */
    private Map<String, String> dropReg = Collections.emptyMap();
    public static final String Key$dropReg = Key + ".drop-reg";

    /**
     * Trace table for AfterInsert (create the original PK index)
     *
     * @see #Key$journalInser
     */
    private String journalInsert = "";
    public static final String Key$journalInser = Key + ".journal-insert";

    /**
     * AfterInsert Trigger
     *
     * @see #Key$triggerInsert
     */
    private String triggerInsert = "";
    public static final String Key$triggerInsert = Key + ".trigger-insert";

    /**
     * Trace table for AfterUpdate (create the original PK index)
     *
     * @see #Key$journalUpdate
     */
    private String journalUpdate = "";
    public static final String Key$journalUpdate = Key + ".journal-update";

    /**
     * AfterUpdate Trigger
     *
     * @see #Key$triggerUpdate
     */
    private String triggerUpdate = "";
    public static final String Key$triggerUpdate = Key + ".trigger-update";

    /**
     * Trace table for BeforeDelete (create the original PK index)`
     *
     * @see #Key$journalDelete
     */
    private String journalDelete = "";
    public static final String Key$journalDelete = Key + ".journal-delete";

    /**
     * BeforeDelete Trigger
     *
     * @see #Key$triggerDelete
     */
    private String triggerDelete = "";
    public static final String Key$triggerDelete = Key + ".trigger-delete";
}
