package pro.fessional.wings.tiny.mail.sender;

import lombok.Getter;
import org.springframework.mail.MailException;

/**
 * should wait until waitEpoch, then may retry
 *
 * @author trydofor
 * @since 2023-01-03
 */
@Getter
public class MailWaitException extends MailException {

    /**
     * Epoch mills to wait
     */
    private final long waitEpoch;
    /**
     * Whether it is a host-level wait
     */
    private final boolean hostLevel;

    /**
     * Whether to stop sending than to wait
     */
    private final boolean stopRetry;

    public MailWaitException(long epoch, boolean host, boolean stop, Throwable cause) {
        super("need wait epoch=" + epoch + ", host=" + host + ", stop=" + stop + " for exception", cause);
        this.waitEpoch = epoch;
        this.hostLevel = host;
        this.stopRetry = stop;
    }
}
