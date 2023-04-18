package pro.fessional.wings.silencer.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Inspect and audit the Application
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SilencerInspectProp.Key)
public class SilencerInspectProp {

    public static final String Key = "wings.silencer.inspect";

    /**
     * Whether to audit the file and cascading relationship of properties key/value
     *
     * @see #Key$properties
     */
    private boolean properties = false;
    public static final String Key$properties = Key + ".properties";

}
