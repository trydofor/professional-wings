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
     * blockId提供方法，默认sql。
     * sql - 查询数据库，唯一返回值为id。
     * fix - 固定数字，int。
     * biz - 使用自定义的业务Bean
     *
     * @see #Key$blockType
     */
    private String blockType = "sql";
    public static final String Key$blockType = Key + ".block-type";

    /**
     * 提供方式的参数，sql时为select
     *
     * @see #Key$blockPara
     */
    private String blockPara;
    public static final String Key$blockPara = Key + ".block-para";

    /**
     * 插入语句。jdbc template sql 类型SysLightSequence
     * String seq_name, int block_id, long next_val, int step_val, String comments
     *
     * @see #Key$sequenceInsert
     */
    private String sequenceInsert;
    public static final String Key$sequenceInsert = Key + ".sequence-insert";

    /**
     * 更新语句。jdbc template sql 类型SysLightSequence
     * String seq_name, int block_id, long next_val, int step_val, String comments
     *
     * @see #Key$sequenceUpdate
     */
    private String sequenceUpdate;
    public static final String Key$sequenceUpdate = Key + ".sequence-update";

    /**
     * 获取单个。jdbc template sql 类型SysLightSequence
     * String seq_name, int block_id, long next_val, int step_val, String comments
     *
     * @see #Key$sequenceGetOne
     */
    private String sequenceGetOne;
    public static final String Key$sequenceGetOne = Key + ".sequence-get-one";

    /**
     * 获取全部。jdbc template sql 类型SysLightSequence
     * String seq_name, int block_id, long next_val, int step_val, String comments
     *
     * @see #Key$sequenceGetAll
     */
    private String sequenceGetAll;
    public static final String Key$sequenceGetAll = Key + ".sequence-get-all";

    /**
     * 从数据库获取可以校验和调整的表名和自动，第一列表名，第二列主键名
     *
     * @see #Key$sequenceAdjust
     */
    private String sequenceAdjust = "";
    public static final String Key$sequenceAdjust = Key + ".sequence-adjust";
}
