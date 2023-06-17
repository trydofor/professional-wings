package pro.fessional.wings.warlock.spring.prop;

import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.slardar.webmvc.SimpleResponse;

import java.util.LinkedHashMap;

/**
 * Global Exception handling. supports variable `{message}` placeholder.
 * `default-exception` handles all exceptions and provides defaults for other similar types.
 * <p>
 * wings-warlock-error-77.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-17
 */

@ConfigurationProperties(WarlockErrorProp.Key)
public class WarlockErrorProp extends LinkedHashMap<String, SimpleResponse> implements InitializingBean {

    public static final String Key = "wings.warlock.error";

    /**
     * handle all exceptions at last.
     *
     * @see #Key$defaultException
     */
    @Getter
    private SimpleResponse defaultException = null;
    public static final String Key$defaultException = Key + ".default-exception";

    @Override
    public void afterPropertiesSet() {
        defaultException = get("default-exception");
        if (defaultException == null) {
            throw new IllegalStateException("must have 'default-exception' define");
        }

        for (SimpleResponse mr : values()) {
            if (mr != defaultException) {
                mr.fillIfAbsent(defaultException);
            }
        }
    }
}
