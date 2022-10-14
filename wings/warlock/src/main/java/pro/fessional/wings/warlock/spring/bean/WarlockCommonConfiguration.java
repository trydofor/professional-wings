package pro.fessional.wings.warlock.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import pro.fessional.wings.slardar.serialize.JsonConversion;
import pro.fessional.wings.slardar.serialize.KryoConversion;
import pro.fessional.wings.warlock.database.autogen.tables.daos.SysConstantEnumDao;
import pro.fessional.wings.warlock.service.conf.RuntimeConfService;
import pro.fessional.wings.warlock.service.conf.impl.RuntimeConfServiceImpl;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
public class WarlockCommonConfiguration {

    private final static Log log = LogFactory.getLog(WarlockCommonConfiguration.class);

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$jooqAutogen, havingValue = "true")
    @ComponentScan(basePackageClasses = SysConstantEnumDao.class)
    public static class WarlockJooqDaoConfiguration {
    }

    @Bean
    @ConditionalOnMissingBean
    public RuntimeConfService runtimeConfService(ObjectProvider<ConversionService> conversionProvider) {
        log.info("Warlock spring-bean runtimeConfService");
        final RuntimeConfServiceImpl bean = new RuntimeConfServiceImpl();
        conversionProvider.ifAvailable(it -> bean.addHandler(RuntimeConfServiceImpl.PropHandler, it));
        bean.addHandler(RuntimeConfServiceImpl.JsonHandler, new JsonConversion());
        bean.addHandler(RuntimeConfServiceImpl.KryoHandler, new KryoConversion());
        return bean;
    }
}
