package pro.fessional.wings.tiny.grow.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

import static pro.fessional.wings.tiny.grow.spring.prop.TinyTrackExcludeProp.Key;

/**
 * @author trydofor
 * @since 2024-07-27
 */
@Data
@ConfigurationProperties(Key)
public class TinyTrackExcludeProp {

    public static final String Key = "wings.tiny.grow.track.exclude";

    /**
     * exclude the property if it is instance of
     *
     * @see #Key$clazz
     */
    private Map<String, Class<?>> clazz = Collections.emptyMap();
    public static final String Key$clazz = Key + ".clazz";

    /**
     * exclude the property if its name equals
     *
     * @see #Key$equal
     */
    private Map<String, String> equal = Collections.emptyMap();
    public static final String Key$equal = Key + ".equal";

    /**
     * exclude the property if its name match regex
     *
     * @see #Key$regex
     */
    private Map<String, Pattern> regex = Collections.emptyMap();
    public static final String Key$regex = Key + ".regex";
}
