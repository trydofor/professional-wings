package pro.fessional.wings.warlock.spring.prop;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.slardar.webmvc.MessageResponse;

/**
 * wings-warlock-error-77.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-17
 */
@Data
@ConfigurationProperties(WarlockErrorProp.Key)
public class WarlockErrorProp {

    public static final String Key = "wings.warlock.error";

    private MessageResponse defaultException = new MessageResponse();
    private MessageResponse codeException = new MessageResponse();

    public void defaultIfAbsent(@NotNull MessageResponse res) {

        final int hs = res.getHttpStatus();
        if (hs <= 0) {
            res.setHttpStatus(defaultException.getHttpStatus());
        }

        final String ct = res.getContentType();
        if (ct == null || ct.isEmpty()) {
            res.setContentType(defaultException.getContentType());
        }

        final String mb = res.getMessageBody();
        if (mb == null || mb.isEmpty()) {
            res.setMessageBody(defaultException.getMessageBody());
        }

        final String db = res.getDefaultBody();
        if (db == null || db.isEmpty()) {
            res.setDefaultBody(defaultException.getDefaultBody());
        }
    }
}
