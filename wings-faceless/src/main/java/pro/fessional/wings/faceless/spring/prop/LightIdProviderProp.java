package pro.fessional.wings.faceless.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author trydofor
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties("wings.faceless.lightid.provider")
public class LightIdProviderProp {
    /**
     * blockId提供方法，默认sql。
     * sql - 查询数据库，唯一返回值为id。
     * fix - 固定数字，int。
     */
    private String blockType = "sql";

    /**
     * 提供方式的参数，sql时为select
     */
    private String blockPara;

    /**
     * 插入语句。jdbc template sql 类型SysLightSequence
     * String seq_name, int block_id, long next_val, int step_val, String comments
     */
    private String sequenceInsert;
    /**
     * 更新语句。jdbc template sql 类型SysLightSequence
     * String seq_name, int block_id, long next_val, int step_val, String comments
     */
    private String sequenceUpdate;
    /**
     * 获取单个。jdbc template sql 类型SysLightSequence
     * String seq_name, int block_id, long next_val, int step_val, String comments
     */
    private String sequenceGetOne;
    /**
     * 获取全部。jdbc template sql 类型SysLightSequence
     * String seq_name, int block_id, long next_val, int step_val, String comments
     */
    private String sequenceGetAll;
}
