package pro.fessional.wings.silencer.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * spring-wings-enabled-79.properties
 * @author trydofor
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties("spring.wings.silencer.enabled")
public class SilencerEnabledProp {

    /**
     * 是否显示wings的conditional信息
     */
    private boolean verbose = false;
    /**
     * 是否自动加载 /wings-i18n/
     */
    private boolean message = true;
    /**
     * 是否自动载所有classpath*下的 ** /spring/bean/ **
     */
    private boolean scanner = true;
}
