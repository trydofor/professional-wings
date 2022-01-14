package pro.fessional.wings.faceless.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * spring-wings-enabled-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties(FacelessShardEnabledProp.Key)
public class FacelessShardEnabledProp {

    public static final String Key = "spring.wings.faceless.shard.enabled";

    /**
     * 使用wings方式配置ShardingSphere
     *
     * @see #Key$module
     */
    private boolean module = true;
    public static final String Key$module = Key + ".module";

}
