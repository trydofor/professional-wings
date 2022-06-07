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
@ConfigurationProperties(SlardarDebounceProp.Key)
public class SlardarDebounceProp {

    public static final String Key = "wings.slardar.debounce";

    /**
     * 等待区大小
     *
     * @see #Key$capacity
     */
    private long capacity = 10_000;
    public static final String Key$capacity = Key + ".capacity";

    /**
     * 最大等待秒
     *
     * @see #Key$maxWait
     */
    private int maxWait = 300;
    public static final String Key$maxWait = Key + ".max-wait";

    /**
     * @see #Key$httpStatus
     */
    private int httpStatus = 208;
    public static final String Key$httpStatus = Key + ".http-status";

    /**
     * 告知需要验证的content-type
     *
     * @see #Key$contentType
     */
    private String contentType = "";
    public static final String Key$contentType = Key + ".content-type";

    /**
     * 告知验证码的回复文本内容
     *
     * @see #Key$responseBody
     */
    private String responseBody = "";
    public static final String Key$responseBody = Key + ".response-body";
}
