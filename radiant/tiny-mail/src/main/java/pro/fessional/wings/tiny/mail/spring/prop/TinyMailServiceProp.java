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
     * max failures for the same email.
     *
     * @see #Key$maxFail
     */
    private int maxFail = 3;
    public static final String Key$maxFail = Key + ".max-fail";

    /**
     * max success for the same email.
     *
     * @see #Key$maxDone
     */
    private int maxDone = 0;
    public static final String Key$maxDone = Key + ".max-done";

    /**
     * the email does not need to be sent anymore as it has been a certain amount of time. default 1 day.
     *
     * @see #Key$maxNext
     */
    private Duration maxNext = Duration.ofDays(1);
    public static final String Key$maxNext = Key + ".max-next";

    /**
     * how soon to retry after failure, default 1 minute.
     *
     * @see #Key$tryNext
     */
    private Duration tryNext = Duration.ofMinutes(1);
    public static final String Key$tryNext = Key + ".try-next";

    /**
     * max number of bulk emails sent at one time.
     *
     * @see #Key$batchSize
     */
    private int batchSize = 10;
    public static final String Key$batchSize = Key + ".batch-size";

    /**
     * if this capacity is exceeded, log it as Warn.
     *
     * @see #Key$warnSize
     */
    private int warnSize = 1000;
    public static final String Key$warnSize = Key + ".warn-size";

    /**
     * how long after start, scan for unsent mail, `0` for no scan.
     *
     * @see #Key$bootScan
     */
    private Duration bootScan = Duration.ofSeconds(60);
    public static final String Key$bootScan = Key + ".boot-scan";

    /**
     * whether to send emails from this app only.
     *
     * @see #Key$onlyApp
     */
    private boolean onlyApp = false;
    public static final String Key$onlyApp = Key + ".only-app";

    /**
     * whether to send emails from this RumMode only.
     *
     * @see #Key$onlyRun
     */
    private boolean onlyRun = true;
    public static final String Key$onlyRun = Key + ".only-run";
}
