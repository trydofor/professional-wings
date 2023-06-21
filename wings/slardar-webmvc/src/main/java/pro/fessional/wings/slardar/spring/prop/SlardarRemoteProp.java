package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

import static java.util.Collections.emptyMap;

/**
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SlardarRemoteProp.Key)
public class SlardarRemoteProp {

    public static final String Key = "wings.slardar.remote";

    /**
     * intranet segments not considered as remote ip
     *
     * @see #Key$innerIp
     */
    private Map<String, String> innerIp = emptyMap();
    public static final String Key$innerIp = Key + ".inner-ip";

    /**
     * which header to get the real ip when behind proxy.
     *
     * @see #Key$ipHeader
     */
    private Map<String, String> ipHeader = emptyMap();
    public static final String Key$ipHeader = Key + ".ip-header";

    /**
     * which headers (use all) to get device info.
     *
     * @see #Key$agentHeader
     */
    private Map<String, String> agentHeader = emptyMap();
    public static final String Key$agentHeader = Key + ".agent-header";
}
