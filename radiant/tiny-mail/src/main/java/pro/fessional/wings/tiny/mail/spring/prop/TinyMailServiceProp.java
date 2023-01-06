package pro.fessional.wings.tiny.mail.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author trydofor
 * @since 2022-12-29
 */
@Data
@ConfigurationProperties(TinyMailServiceProp.Key)
public class TinyMailServiceProp {
    public static final String Key = "wings.tiny.mail.service";

    /**
     * 同一邮件最大失败次数
     *
     * @see #Key$maxFail
     */
    private int maxFail = 3;
    public static final String Key$maxFail = Key + ".max-fail";

    /**
     * 同一邮件最大发送次数
     *
     * @see #Key$maxSend
     */
    private int maxSend = 0;
    public static final String Key$maxSend = Key + ".max-send";

    /**
     * 超过多少时间的邮件不需要发送，默认1天
     *
     * @see #Key$maxNext
     */
    private Duration maxNext = Duration.ofDays(1);
    public static final String Key$maxNext = Key + ".max-next";
}
