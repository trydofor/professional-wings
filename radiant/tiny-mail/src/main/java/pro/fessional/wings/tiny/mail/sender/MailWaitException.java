package pro.fessional.wings.tiny.mail.sender;

import lombok.Getter;
import org.springframework.mail.MailException;

/**
 * @author trydofor
 * @since 2023-01-03
 */
public class MailWaitException extends MailException {

    /**
     * Epoch mills to wait
     */
    @Getter
    private final long waitEpoch;
    /**
     * Whether it is a host-level wait
     */
    @Getter
    private final boolean hostLevel;

    /**
     * Whether to stop sending than to wait
     */
    @Getter
    private final boolean stopRetry;

    public MailWaitException(long epoch, boolean host, boolean stop, Throwable cause) {
        super("need wait epoch=" + epoch + ", host=" + host + ", stop=" + stop + " for exception", cause);
        this.waitEpoch = epoch;
        this.hostLevel = host;
        this.stopRetry = stop;
    }
}
