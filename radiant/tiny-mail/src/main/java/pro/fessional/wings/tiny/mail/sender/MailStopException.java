package pro.fessional.wings.tiny.mail.sender;

import lombok.Getter;
import org.springframework.mail.MailException;

/**
 * stop sending, maybe format, prepare error
 *
 * @author trydofor
 * @since 2023-01-03
 */
@Getter
public class MailStopException extends MailException {

    public MailStopException(String msg) {
        super(msg);
    }

    public MailStopException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
