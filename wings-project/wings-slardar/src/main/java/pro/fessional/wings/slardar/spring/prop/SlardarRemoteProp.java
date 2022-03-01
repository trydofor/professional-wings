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
     * 不作为remote ip考虑的内网网段
     *
     * @see #Key$innerIp
     */
    private Map<String, String> innerIp = emptyMap();
    public static final String Key$innerIp = Key + ".inner-ip";

    /**
     * 使用代理时，通过哪些header获得真实ip，选择不是ignore的第一个即可
     *
     * @see #Key$ipHeader
     */
    private Map<String, String> ipHeader = emptyMap();
    public static final String Key$ipHeader = Key + ".ip-header";

    /**
     * 用户设备信息头，选择所有信息
     *
     * @see #Key$agentHeader
     */
    private Map<String, String> agentHeader = emptyMap();
    public static final String Key$agentHeader = Key + ".agent-header";
}
