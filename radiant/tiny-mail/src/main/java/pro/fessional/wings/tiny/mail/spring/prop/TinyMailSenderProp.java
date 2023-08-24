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
     * biz-id Header to locate mail by business, default mail id.
     *
     * @see #Key$bizId
     */
    private String bizId = "";
    public static final String Key$bizId = Key + ".biz-id";

    /**
     * biz-mark Header to locate data by business, eg. orderNumber.
     *
     * @see #Key$bizMark
     */
    private String bizMark = "";
    public static final String Key$bizMark = Key + ".biz-mark";

    /**
     * how much time to wait if MailSendException, default 5 minutes.
     *
     * @see #Key$errSend
     */
    private Duration errSend = Duration.ofMinutes(5);
    public static final String Key$errSend = Key + ".err-send";

    /**
     * how much time to wait if MailAuthenticationException, default 1 hour.
     *
     * @see #Key$errAuth
     */
    private Duration errAuth = Duration.ofHours(1);
    public static final String Key$errAuth = Key + ".err-auth";

    /**
     * how many seconds to wait for the host if it contains the
     * following exception message. seconds is the key, the fraction is only used to make
     * key unique, negative number means stop resending.
     *
     * @see #Key$errHost
     */
    private Map<BigDecimal, String> errHost = Collections.emptyMap();
    public static final String Key$errHost = Key + ".err-host";

    /**
     * how many seconds to wait to resend this email if it contains the
     * following exception message. seconds is the key, the fraction is only used to make key unique,
     * negative number means stop resending.
     *
     * @see #Key$errMail
     */
    private Map<BigDecimal, String> errMail = Collections.emptyMap();
    public static final String Key$errMail = Key + ".err-mail";

    /**
     * interval of each login of the same mailhost, avoid limit frequency, 0 is ignored.
     *
     * @see #Key$perIdle
     */
    private Map<String, Duration> perIdle = Collections.emptyMap();
    public static final String Key$perIdle = Key + ".per-idle";

    /**
     * max wait time for the same mailhost, if less then wait,
     * otherwise throw MailWaitException, 0 is ignored.
     *
     * @see #Key$maxIdle
     */
    private Map<String, Duration> maxIdle = Collections.emptyMap();
    public static final String Key$maxIdle = Key + ".max-idle";

    /**
     * force to replace the real "to", string arrays, comma separated.
     *
     * @see #Key$forceTo
     */
    private String[] forceTo = null;
    public static final String Key$forceTo = Key + ".force-to";

    /**
     * force to replace the real "cc", string arrays, comma separated.
     *
     * @see #Key$forceCc
     */
    private String[] forceCc = null;
    public static final String Key$forceCc = Key + ".force-cc";

    /**
     * force to replace the real "bcc", string arrays, comma separated.
     *
     * @see #Key$forceBcc
     */
    private String[] forceBcc = null;
    public static final String Key$forceBcc = Key + ".force-bcc";

    /**
     * force to add prefix to the real subject.
     *
     * @see #Key$forcePrefix
     */
    private String forcePrefix = "";
    public static final String Key$forcePrefix = Key + ".force-prefix";

}
