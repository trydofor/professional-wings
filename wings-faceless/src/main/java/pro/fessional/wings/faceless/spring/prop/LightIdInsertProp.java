package pro.fessional.wings.faceless.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author trydofor
 * @since 2019-05-30
 */
@Data
@ConfigurationProperties("wings.faceless.lightid.insert")
public class LightIdInsertProp {

    /**
     * 不存在当前name和block的id时，是插入还是抛异常
     */
    private boolean auto = true;
    /**
     * 起始ID，自动生成1000起，以下为手动生成。
     */
    private long next = 1000;
    /**
     * 默认步长
     */
    private int step = 100;
}
