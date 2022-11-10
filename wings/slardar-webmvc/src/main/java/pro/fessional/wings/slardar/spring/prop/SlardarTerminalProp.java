package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Map;

/**
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SlardarTerminalProp.Key)
public class SlardarTerminalProp {

    public static final String Key = "wings.slardar.terminal";

    /**
     * @see #Key$requestIgnore
     */
    private Map<String, String> requestIgnore = Collections.emptyMap();
    public static final String Key$requestIgnore = Key + ".request-ignore";

}
