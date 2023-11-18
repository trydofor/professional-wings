package pro.fessional.wings.faceless.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.faceless.flywave.RevisionFitness.Fit;

import java.util.Collections;
import java.util.Map;

/**
 * Flywave checks for dependent `flywave-init` versions.
 *
 * @author trydofor
 * @since 2022-03-15
 */
@Data
@ConfigurationProperties(FlywaveFitProp.Key)
public class FlywaveFitProp {
    public static final String Key = "wings.faceless.flywave";

    /**
     * whether to allow auto init, non-empty database, preferably manual init
     *
     * @see #Key$autoInit
     */
    private boolean autoInit = false;
    public static final String Key$autoInit = Key + ".auto-init";

    /**
     * whether flywave performs version checking for database.
     *
     * @see #Key$checker
     */
    private boolean checker = true;
    public static final String Key$checker = Key + ".checker";

    /**
     * Specific dependencies
     *
     * @see #Key$fit
     */
    private Map<String, Fit> fit = Collections.emptyMap();
    public static final String Key$fit = Key + ".fit";
}
