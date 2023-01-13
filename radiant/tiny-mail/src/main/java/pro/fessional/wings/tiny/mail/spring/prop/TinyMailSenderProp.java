package pro.fessional.wings.tiny.mail.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;

/**
 * @author trydofor
 * @since 2022-12-29
 */
@Data
@ConfigurationProperties(TinyMailSenderProp.Key)
public class TinyMailSenderProp {
    public static final String Key = "wings.tiny.mail.sender";

    /**
     * biz-id的Header
     *
     * @see #Key$bizId
     */
    private String bizId = "";
    public static final String Key$bizId = Key + ".biz-id";

    /**
     * biz-mark的Header
     *
     * @see #Key$bizMark
     */
    private String bizMark = "";
    public static final String Key$bizMark = Key + ".biz-mark";

    /**
     * 发送失败 MailSendException 时，等待多少时间，默认5分钟
     *
     * @see #Key$errSend
     */
    private Duration errSend = Duration.ofMinutes(5);
    public static final String Key$errSend = Key + ".err-send";

    /**
     * 认证失败 MailAuthenticationException 时，等待多少时间，默认1小时
     *
     * @see #Key$errAuth
     */
    private Duration errAuth = Duration.ofHours(1);
    public static final String Key$errAuth = Key + ".err-auth";

    /**
     * 包括以下异常信息时，对此host进行多少秒的等待。秒为key，以小数部分仅用来区分key，负数为建议停止重发
     *
     * @see #Key$errHost
     */
    private Map<BigDecimal, String> errHost = Collections.emptyMap();
    public static final String Key$errHost = Key + ".err-host";

    /**
     * 包括以下异常信息时，对此邮件的重发进行多少秒的等待。秒为key，以小数部分仅用来区分key，负数为建议停止重发
     * 如 `501001.001`的意义为，501为错误号，001为host编号，.001为区别位
     * @see #Key$errMail
     */
    private Map<BigDecimal, String> errMail = Collections.emptyMap();
    public static final String Key$errMail = Key + ".err-mail";

    /**
     * 同一邮件host每次登录的间隔，避免限频，默认无视
     *
     * @see #Key$perIdle
     */
    private Map<String, Duration> perIdle = Collections.emptyMap();
    public static final String Key$perIdle = Key + ".per-idle";

    /**
     * 同一邮件host最多等待时间，小于时等待，否则抛出MailWaitException，默认无视
     *
     * @see #Key$maxIdle
     */
    private Map<String, Duration> maxIdle = Collections.emptyMap();
    public static final String Key$maxIdle = Key + ".max-idle";

    /**
     * 强制替换真实的to
     *
     * @see #Key$forceTo
     */
    private String[] forceTo = null;
    public static final String Key$forceTo = Key + ".force-to";

    /**
     * 强制替换真实的cc
     *
     * @see #Key$forceCc
     */
    private String[] forceCc = null;
    public static final String Key$forceCc = Key + ".force-cc";

    /**
     * 强制替换真实的bcc
     *
     * @see #Key$forceBcc
     */
    private String[] forceBcc = null;
    public static final String Key$forceBcc = Key + ".force-bcc";

    /**
     * @see #Key$forcePrefix
     */
    private String forcePrefix = "";
    public static final String Key$forcePrefix = Key + ".force-prefix";

}
