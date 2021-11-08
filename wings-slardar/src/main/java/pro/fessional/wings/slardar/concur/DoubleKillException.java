package pro.fessional.wings.slardar.concur;

import pro.fessional.mirana.pain.NoStackRuntimeException;

/**
 * @author trydofor
 * @since 2021-03-15
 */
public class DoubleKillException extends NoStackRuntimeException {

    private final String progressKey;
    private final long runningSecond;

    public DoubleKillException(String progressKey, long runningSecond) {
        super(progressKey + ", run=" + runningSecond);
        this.progressKey = progressKey;
        this.runningSecond = runningSecond;
    }

    public DoubleKillException(String progressKey, long started, long now) {
        this(progressKey, (now - started) / 1000);
    }

    public String getProgressKey() {
        return progressKey;
    }

    public long getRunningSecond() {
        return runningSecond;
    }
}
