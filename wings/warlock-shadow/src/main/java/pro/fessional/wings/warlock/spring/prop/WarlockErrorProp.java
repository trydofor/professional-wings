package pro.fessional.wings.warlock.spring.prop;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.slardar.webmvc.MessageResponse;

/**
 * Global Exception handling. `CodeException` supports variable `{message}`.
 * `default` handles all exceptions and provides defaults for other similar types.
 * <p>
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

    /**
     * handle all exceptions at last.
     *
     * @see #Key$defaultException
     */
    private MessageResponse defaultException = new MessageResponse();
    public static final String Key$defaultException = Key + ".default-exception";

    /**
     * handle CodeException
     *
     * @see #Key$codeException
     */
    private MessageResponse codeException = new MessageResponse();
    public static final String Key$codeException = Key + ".code-exception";

    public void fillAbsent(@NotNull MessageResponse res) {
        res.fillAbsent(defaultException);
    }
}
