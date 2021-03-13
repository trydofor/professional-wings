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
@ConfigurationProperties(SlardarConcurFbProp.Key)
public class SlardarConcurFbProp {

    public static final String Key = "wings.slardar.concur.first-blood";

    /**
     * 识别用户时使用的header和session的key
     *
     * @see #Key$clientTicketKey
     */
    private String clientTicketKey = "";
    public static final String Key$clientTicketKey = Key + ".client-ticket-key";

    /**
     * 生成图形验证码的参数，时间戳
     *
     * @see #Key$freshCaptchaKey
     */
    private String freshCaptchaKey = "";
    public static final String Key$freshCaptchaKey = Key + ".fresh-captcha-key";

    /**
     * 图形验证验证码的参数，客户输入的验证码
     *
     * @see #Key$checkCaptchaKey
     */
    private String checkCaptchaKey = "";
    public static final String Key$checkCaptchaKey = Key + ".check-captcha-key";


    /**
     * 是否使用中文验证码
     *
     * @see #Key$chineseCaptcha
     */
    private boolean chineseCaptcha = true;
    public static final String Key$chineseCaptcha = Key + ".chinese-captcha";

    /**
     * @see #Key$httpStatus
     */
    private int httpStatus = 406;
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
