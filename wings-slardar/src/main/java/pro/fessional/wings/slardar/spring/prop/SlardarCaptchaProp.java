package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

/**
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SlardarCaptchaProp.Key)
public class SlardarCaptchaProp {

    public static final String Key = "wings.slardar.captcha";

    /**
     * header picker的 header 前缀
     *
     * @see #Key$pickerHeader
     */
    private List<String> pickerHeader = Collections.emptyList();
    public static final String Key$pickerHeader = Key + ".picker-header";

    /**
     * cookie picker的 cookie name
     *
     * @see #Key$pickerCookie
     */
    private List<String> pickerCookie = Collections.emptyList();
    public static final String Key$pickerCookie = Key + ".picker-cookie";

    /**
     * params picker的 参数名
     *
     * @see #Key$pickerParams
     */
    private List<String> pickerParams = Collections.emptyList();
    public static final String Key$pickerParams = Key + ".picker-params";

    /**
     * 验证码缓存的最大数量
     *
     * @see #Key$holderSize
     */
    private int holderSize = 1000;
    public static final String Key$holderSize = Key + ".holder-size";

    /**
     * 验证码缓存的存活秒数
     *
     * @see #Key$holderLive
     */
    private int holderLive = 60;
    public static final String Key$holderLive = Key + ".holder-live";

}
