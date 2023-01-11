package pro.fessional.wings.tiny.mail.sender;

import lombok.Getter;
import org.springframework.mail.MailException;

/**
 * @author trydofor
 * @since 2023-01-03
 */
public class MailWaitException extends MailException {

    @Getter
    private final long waitEpoch;

    public MailWaitException(long epoch, Throwable cause) {
        super("need wait until epoch " + epoch + "ms for exception", cause);
        this.waitEpoch = epoch;
    }

}
