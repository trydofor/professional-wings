package pro.fessional.wings.faceless.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author trydofor
 * @see #Key
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties(LightIdProviderProp.Key)
public class LightIdProviderProp {

    public static final String Key = "wings.faceless.lightid.provider";

    /**
     * <pre>
     * method to provide blockId
     * - `sql` - query database, return the id
     * - `fix` - fixed number, int
     * - `biz` - use a custom business bean
     * </pre>
     *
     * @see #Key$blockType
     */
    private String blockType = "sql";
    public static final String Key$blockType = Key + ".block-type";

    /**
     * parameters of the provide method, select for sql, and number for fix.
     *
     * @see #Key$blockPara
     */
    private String blockPara;
    public static final String Key$blockPara = Key + ".block-para";

    /**
     * <pre>
     * insert statement for JdbcTemplate.
     * See `LightSequenceModifyJdbc` for details, the parameters are,
     * - `String` seq_name - sequence name
     * - `int` block_id - data block id
     * - `long` next_val - next seq
     * - `int` step_val - step value
     * - `String` comments - description
     * </pre>
     *
     * @see #Key$sequenceInsert
     */
    private String sequenceInsert;
    public static final String Key$sequenceInsert = Key + ".sequence-insert";

    /**
     * <pre>
     * update statement for JdbcTemplate.
     * See `LightSequenceModifyJdbc` for details, the parameters are,
     * - `String` seq_name - sequence name
     * - `int` block_id - data block id
     * - `long` next_val - next seq
     * - `int` step_val - step value
     * - `String` comments - description
     * </pre>
     *
     * @see #Key$sequenceUpdate
     */
    private String sequenceUpdate;
    public static final String Key$sequenceUpdate = Key + ".sequence-update";

    /**
     * <pre>
     * fetch one sql for JdbcTemplate.
     * See `LightSequenceModifyJdbc` for details, the parameters are,
     * - `String` seq_name - sequence name
     * - `int` block_id - data block id
     * - `long` next_val - next seq
     * - `int` step_val - step value
     * - `String` comments - description
     * </pre>
     *
     * @see #Key$sequenceGetOne
     */
    private String sequenceGetOne;
    public static final String Key$sequenceGetOne = Key + ".sequence-get-one";

    /**
     * <pre>
     * fetch all sql for JdbcTemplate.
     * See `LightSequenceModifyJdbc` for details, the parameters are,
     * - `String` seq_name - sequence name
     * - `int` block_id - data block id
     * - `long` next_val - next seq
     * - `int` step_val - step value
     * - `String` comments - description
     * </pre>
     *
     * @see #Key$sequenceGetAll
     */
    private String sequenceGetAll;
    public static final String Key$sequenceGetAll = Key + ".sequence-get-all";

    /**
     * try to verify and adjust the id in the database to make it correct. Set to `∅` to ignore this feature.
     * Enter `table name` (as sequence name), return `table name` and `column name` in the database.
     *
     * @see #Key$sequenceAdjust
     */
    private String sequenceAdjust = "";
    public static final String Key$sequenceAdjust = Key + ".sequence-adjust";
}
