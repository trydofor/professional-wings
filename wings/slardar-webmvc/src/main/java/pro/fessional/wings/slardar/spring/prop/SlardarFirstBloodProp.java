package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.slardar.webmvc.SimpleResponse;

/**
 * Resource protection features, such as CAPTCHA,
 * wings-firstblood-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(SlardarFirstBloodProp.Key)
public class SlardarFirstBloodProp extends SimpleResponse {

    public static final String Key = "wings.slardar.first-blood";


    /**
     * key of the header and session used to identify the user.
     *
     * @see #Key$clientTicketKey
     */
    private String clientTicketKey = "";
    public static final String Key$clientTicketKey = Key + ".client-ticket-key";

    /**
     * key to generate image CAPTCHA, timestamp or specific prefix.
     *
     * @see #Key$questCaptchaKey
     */
    private String questCaptchaKey = "";
    public static final String Key$questCaptchaKey = Key + ".quest-captcha-key";

    /**
     * key to verify image CAPTCHA, client input the code.
     *
     * @see #Key$checkCaptchaKey
     */
    private String checkCaptchaKey = "";
    public static final String Key$checkCaptchaKey = Key + ".check-captcha-key";


    /**
     * key to return image in base64, used in fresh-captcha-image=base64+timestamp
     *
     * @see #Key$base64CaptchaKey
     */
    private String base64CaptchaKey = "";
    public static final String Key$base64CaptchaKey = Key + ".base-64-captcha-key";

    /**
     * format of returned base64 image, with `{base64}` placeholder.
     * The default configuration will output `data:image/jpeg;base64,/9j/4AAQSkZ.....`
     *
     * @see #Key$base64CaptchaBody
     */
    private String base64CaptchaBody = "";
    public static final String Key$base64CaptchaBody = Key + ".base-64-captcha-body";

    /**
     * whether to use Chinese char.
     *
     * @see #Key$chineseCaptcha
     */
    private boolean chineseCaptcha = true;
    public static final String Key$chineseCaptcha = Key + ".chinese-captcha";

    /**
     * whether to ignore case.
     *
     * @see #Key$caseIgnore
     */
    private boolean caseIgnore = true;
    public static final String Key$caseIgnore = Key + ".case-ignore";

    /**
     * scene prefix for image graphic captcha.
     *
     * @see #Key$captchaPrefix
     */
    private String captchaPrefix = "image";
    public static final String Key$captchaPrefix = Key + ".captcha-prefix";
}
