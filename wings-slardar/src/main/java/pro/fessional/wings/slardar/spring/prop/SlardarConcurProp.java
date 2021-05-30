package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * wings-concur-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Getter
@ConfigurationProperties(SlardarConcurProp.Key)
public class SlardarConcurProp {

    public static final String Key = "wings.slardar.concur";

    private final FirstBlood firstBlood = new FirstBlood();
    private final DoubleKill doubleKill = new DoubleKill();

    @Data
    public static class FirstBlood {

        /**
         * 识别用户时使用的header和session的key
         *
         * @see #Key$clientTicketKey
         */
        private String clientTicketKey = "";
        public static final String Key$clientTicketKey = Key + ".first-blood.client-ticket-key";

        /**
         * 生成图形验证码的参数，时间戳
         *
         * @see #Key$freshCaptchaKey
         */
        private String freshCaptchaKey = "";
        public static final String Key$freshCaptchaKey = Key + ".first-blood.fresh-captcha-key";

        /**
         * 图形验证验证码的参数，客户输入的验证码
         *
         * @see #Key$checkCaptchaKey
         */
        private String checkCaptchaKey = "";
        public static final String Key$checkCaptchaKey = Key + ".first-blood.check-captcha-key";


        /**
         * 图片以base64返回的key，用在fresh-captcha-image=base64+时间戳
         *
         * @see #Key$base64CaptchaKey
         */
        private String base64CaptchaKey = "";
        public static final String Key$base64CaptchaKey = Key + ".base-64-captcha-key";

        /**
         * 图片以base64返回的格式，{b64} 占位为 `data:image/jpeg;base64,/9j/4AAQSkZ.....`
         *
         * @see #Key$base64CaptchaBody
         */
        private String base64CaptchaBody = "";
        public static final String Key$base64CaptchaBody = Key + ".base-64-captcha-body";

        /**
         * 是否使用中文验证码
         *
         * @see #Key$chineseCaptcha
         */
        private boolean chineseCaptcha = true;
        public static final String Key$chineseCaptcha = Key + ".first-blood.chinese-captcha";

        /**
         * @see #Key$captchaPrefix
         */
        private String captchaPrefix = "image";
        public static final String Key$captchaPrefix = Key + ".captcha-prefix";


        /**
         * @see #Key$httpStatus
         */
        private int httpStatus = 406;
        public static final String Key$httpStatus = Key + ".first-blood.http-status";

        /**
         * 告知需要验证的content-type
         *
         * @see #Key$contentType
         */
        private String contentType = "";
        public static final String Key$contentType = Key + ".first-blood.content-type";

        /**
         * 告知验证码的回复文本内容
         *
         * @see #Key$responseBody
         */
        private String responseBody = "";
        public static final String Key$responseBody = Key + ".first-blood.response-body";
    }

    @Data
    public static class DoubleKill {
        /**
         * DoubleKillExceptionResolver 回复的http-status
         *
         * @see #Key$httpStatus
         */
        private int httpStatus = 200;
        public static final String Key$httpStatus = Key + ".double-kill.http-status";

        /**
         * DoubleKillExceptionResolver 回复的content-type
         *
         * @see #Key$contentType
         */
        private String contentType = "";
        public static final String Key$contentType = Key + ".double-kill.content-type";

        /**
         * DoubleKillExceptionResolver 回复的文本内容。
         * 支持变量 {key} 和 {ttl}
         *
         * @see #Key$responseBody
         */
        private String responseBody = "";
        public static final String Key$responseBody = Key + ".double-kill.response-body";
    }
}
