package pro.fessional.wings.tiny.mail.sender;

import lombok.Getter;
import org.springframework.mail.MailException;

/**
 * should retry at nextEpoch
 *
 * @author trydofor
 * @since 2023-01-03
 */
@Getter
public class MailRetryException extends MailException {

    /**
     * Epoch mills to retry
     */
    private final long nextEpoch;

    public MailRetryException(long next, Throwable cause) {
        super("retry at epoch=" + next + " for exception", cause);
        this.nextEpoch = next;
    }
}
