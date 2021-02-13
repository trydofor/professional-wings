package pro.fessional.wings.faceless.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * spring-wings-enabled-79.properties
 *
 * @author trydofor
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties("spring.wings.faceless.flywave.enabled")
public class FlywaveEnabledProp {

    /**
     * 是否注入flywave
     */
    private boolean module = false;
}
