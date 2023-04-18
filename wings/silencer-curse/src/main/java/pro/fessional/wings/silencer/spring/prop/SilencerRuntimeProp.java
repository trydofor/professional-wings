package pro.fessional.wings.silencer.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.silencer.modulate.ApiMode;
import pro.fessional.wings.silencer.modulate.RunMode;

/**
 * Runtime Mode of the Application.
 * wings-warlock-runtime-77.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-10-09
 */
@Data
@ConfigurationProperties(SilencerRuntimeProp.Key)
public class SilencerRuntimeProp {

    public static final String Key = "wings.silencer.runtime";

    /**
     * RunMode of the application
     *
     * @see #Key$runMode
     */
    private RunMode runMode = RunMode.Local;
    public static final String Key$runMode = Key + ".run-mode";

    /**
     * ApiMode of the application
     *
     * @see #Key$apiMode
     */
    private ApiMode apiMode = ApiMode.Nothing;
    public static final String Key$apiMode = Key + ".api-mode";
}
