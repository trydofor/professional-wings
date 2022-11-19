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

    public void fillAbsent(@NotNull MessageResponse res) {
        res.fillAbsent(defaultException);
    }
}
