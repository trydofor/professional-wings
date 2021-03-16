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
        super(progressKey);
        this.progressKey = progressKey;
        this.runningSecond = runningSecond;
    }

    public DoubleKillException(String progressKey, long started, long now) {
        super(progressKey);
        this.progressKey = progressKey;
        this.runningSecond = (now - started) / 1000;
    }

    public String getProgressKey() {
        return progressKey;
    }

    public long getRunningSecond() {
        return runningSecond;
    }
}
