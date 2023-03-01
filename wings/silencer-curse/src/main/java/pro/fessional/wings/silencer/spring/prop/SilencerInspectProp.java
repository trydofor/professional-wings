package pro.fessional.wings.silencer.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SilencerInspectProp.Key)
public class SilencerInspectProp {

    public static final String Key = "wings.silencer.inspect";

    /**
     * 是否审视properties的key,value,所在文件及层叠关系
     *
     * @see #Key$properties
     */
    private boolean properties = false;
    public static final String Key$properties = Key + ".properties";

}
