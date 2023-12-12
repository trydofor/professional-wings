package pro.fessional.wings.silencer.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

/**
 * @author trydofor
 * @since 2023-10-27
 */
@Data
@ConfigurationProperties(SilencerScannerProp.Key)
public class SilencerScannerProp {

    public static final String Key = "wings.silencer.scanner";

    /**
     * scan component from `*&#42;/spring/bean/*&#42;/*.class` on ApplicationPreparedEvent
     *
     * @see #Key$bean
     */
    private List<String> bean = Collections.emptyList();
    public static final String Key$bean = Key + ".bean";
}
