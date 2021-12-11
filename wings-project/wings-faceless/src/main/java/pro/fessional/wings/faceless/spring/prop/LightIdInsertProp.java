package pro.fessional.wings.faceless.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author trydofor
 * @see #Key
 * @since 2019-05-30
 */
@Data
@ConfigurationProperties(LightIdInsertProp.Key)
public class LightIdInsertProp {

    public static final String Key = "wings.faceless.lightid.insert";

    /**
     * 不存在当前name和block的id时，是插入还是抛异常
     *
     * @see #Key$auto
     */
    private boolean auto = true;
    public static final String Key$auto = Key + ".auto";

    /**
     * 起始ID，自动生成1000起，以下为手动生成。
     *
     * @see #Key$next
     */
    private long next = 1000;
    public static final String Key$next = Key + ".next";

    /**
     * 默认步长
     *
     * @see #Key$step
     */
    private int step = 100;
    public static final String Key$step = Key + ".step";

}
