package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * wings-righter-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SlardarRighterProp.Key)
public class SlardarRighterProp {

    public static final String Key = "wings.slardar.righter";

    /**
     * 埋点header中的key
     *
     * @see #Key$header
     */
    private String header = "";
    public static final String Key$header = Key + ".header";

    /**
     * 编辑越权 回复的http-status
     *
     * @see #Key$httpStatus
     */
    private int httpStatus = 200;
    public static final String Key$httpStatus = Key + ".http-status";

    /**
     * 编辑越权 回复的content-type
     *
     * @see #Key$contentType
     */
    private String contentType = "";
    public static final String Key$contentType = Key + ".content-type";

    /**
     * 编辑越权 回复的文本内容。
     * 支持变量 {key} 和 {ttl}
     *
     * @see #Key$responseBody
     */
    private String responseBody = "";
    public static final String Key$responseBody = Key + ".response-body";

}
