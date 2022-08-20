package pro.fessional.wings.warlock.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.warlock.service.conf.mode.ApiMode;
import pro.fessional.wings.warlock.service.conf.mode.RunMode;

/**
 * wings-warlock-runtime-77.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-10-09
 */
@Data
@ConfigurationProperties(WarlockRuntimeProp.Key)
public class WarlockRuntimeProp {

    public static final String Key = "wings.warlock.runtime";

    /**
     * @see #Key$runMode
     */
    private RunMode runMode = RunMode.Local;
    public static final String Key$runMode = Key + ".run-mode";

    /**
     * @see #Key$apiMode
     */
    private ApiMode apiMode = ApiMode.Nothing;
    public static final String Key$apiMode = Key + ".api-mode";
}
