package pro.fessional.wings.tiny.mail.sender;

import lombok.Getter;
import org.springframework.mail.MailException;

/**
 * @author trydofor
 * @since 2023-01-03
 */
public class MailWaitException extends MailException {

    /**
     * 等待的epoch毫秒数
     */
    @Getter
    private final long waitEpoch;
    /**
     * 是否为host级别的等待
     */
    @Getter
    private final boolean hostLevel;

    /**
     * 相比于等待，更建议停止发送
     */
    @Getter
    private final boolean stopFirst;

    public MailWaitException(long epoch, boolean host, boolean stop, Throwable cause) {
        super("need wait epoch=" + epoch + ", host=" + host + ", stop=" + stop + " for exception", cause);
        this.waitEpoch = epoch;
        this.hostLevel = host;
        this.stopFirst = stop;
    }
}
