package pro.fessional.wings.warlock.spring.bean;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration
@ConditionalOnProperty(name = WarlockEnabledProp.Key$jooqDao, havingValue = "true")
@ComponentScan("pro.fessional.wings.warlock.database.autogen")
public class WarlockJooqBeanConfiguration {
}
