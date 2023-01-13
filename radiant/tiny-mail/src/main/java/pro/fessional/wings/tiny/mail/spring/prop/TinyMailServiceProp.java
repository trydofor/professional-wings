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
     * 同一邮件最大成功次数
     *
     * @see #Key$maxDone
     */
    private int maxDone = 0;
    public static final String Key$maxDone = Key + ".max-done";

    /**
     * 超过多少时间的邮件不需要发送，默认1天
     *
     * @see #Key$maxNext
     */
    private Duration maxNext = Duration.ofDays(1);
    public static final String Key$maxNext = Key + ".max-next";

    /**
     * 失败后多久进行重试，默认1分钟
     *
     * @see #Key$tryNext
     */
    private Duration tryNext = Duration.ofMinutes(1);
    public static final String Key$tryNext = Key + ".try-next";

    /**
     * 批量发送时，一次发的最大件数
     *
     * @see #Key$batchSize
     */
    private int batchSize = 10;
    public static final String Key$batchSize = Key + ".batch-size";

    /**
     * 超过此容量时，以Warn记录日志
     *
     * @see #Key$warnSize
     */
    private int warnSize = 1000;
    public static final String Key$warnSize = Key + ".warn-size";

    /**
     * 启动后多少秒，扫描未发送的邮件，-1为不扫描
     *
     * @see #Key$bootScan
     */
    private Duration bootScan = Duration.ofSeconds(60);
    public static final String Key$bootScan = Key + ".boot-scan";

    /**
     * 是否仅发送本app的邮件
     *
     * @see #Key$onlyApp
     */
    private boolean onlyApp = false;
    public static final String Key$onlyApp = Key + ".only-app";

    /**
     * 是否仅发送本RumMode的邮件
     *
     * @see #Key$onlyRun
     */
    private boolean onlyRun = true;
    public static final String Key$onlyRun = Key + ".only-run";
}
