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
@ConfigurationProperties("spring.wings.faceless.enabled")
public class FacelessEnabledProp {

    /**
     * 是否注入 jdbcTemplate
     */
    private boolean jdbctemplate = true;
    /**
     * 是否注入lingthid
     */
    private boolean lightid = true;
    /**
     * 是否注入journal
     */
    private boolean journal = true;
    /**
     * 是否注入StandardI18nService
     */
    private boolean enumi18n = true;
}
