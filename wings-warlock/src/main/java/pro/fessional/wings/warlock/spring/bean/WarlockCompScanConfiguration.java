package pro.fessional.wings.warlock.spring.bean;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.warlock.database.autogen.Tables;
import pro.fessional.wings.warlock.errorhandle.bind.BindExceptionAdvice;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration
public class WarlockCompScanConfiguration {


    @Configuration
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$jooqAutogen, havingValue = "true")
    @ComponentScan(basePackageClasses = Tables.class)
    public static class WarlockJooqDaoConfiguration {
    }

    @Configuration
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$bindExceptionAdvice, havingValue = "true")
    @ComponentScan(basePackageClasses = BindExceptionAdvice.class)
    public static class BindingErrorConfig {
    }
}
