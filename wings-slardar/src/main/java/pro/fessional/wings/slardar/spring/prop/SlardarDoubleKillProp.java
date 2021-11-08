package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * wings-concur-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SlardarDoubleKillProp.Key)
public class SlardarDoubleKillProp {

    public static final String Key = "wings.slardar.double-kill";

    /**
     * DoubleKillExceptionResolver 回复的http-status
     *
     * @see #Key$httpStatus
     */
    private int httpStatus = 200;
    public static final String Key$httpStatus = Key + ".http-status";

    /**
     * DoubleKillExceptionResolver 回复的content-type
     *
     * @see #Key$contentType
     */
    private String contentType = "";
    public static final String Key$contentType = Key + ".content-type";

    /**
     * DoubleKillExceptionResolver 回复的文本内容。
     * 支持变量 {key} 和 {ttl}
     *
     * @see #Key$responseBody
     */
    private String responseBody = "";
    public static final String Key$responseBody = Key + ".response-body";
}
