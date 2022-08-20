package pro.fessional.wings.batrider.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Set;

/**
 * @author trydofor
 * @since 2022-08-12
 */
@Data
@ConfigurationProperties(BatriderHandlerProp.Key)
public class BatriderHandlerProp {
    public static final String Key = "wings.batrider.handler";

    /**
     * 不需要验证的schemaId
     *
     * @see #Key$authSkipSchema
     */
    private Set<String> authSkipSchema = Collections.emptySet();
    public static final String Key$authSkipSchema = Key + ".auth-skip-schema";

}
