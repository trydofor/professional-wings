package pro.fessional.wings.warlock.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import pro.fessional.wings.silencer.modulate.ApiMode;
import pro.fessional.wings.silencer.modulate.RunMode;
import pro.fessional.wings.silencer.modulate.RuntimeMode;
import pro.fessional.wings.silencer.runner.CommandLineRunnerOrdered;
import pro.fessional.wings.slardar.serialize.JsonConversion;
import pro.fessional.wings.slardar.serialize.KryoConversion;
import pro.fessional.wings.spring.consts.OrderedWarlockConst;
import pro.fessional.wings.warlock.database.autogen.tables.daos.SysConstantEnumDao;
import pro.fessional.wings.warlock.service.conf.RuntimeConfService;
import pro.fessional.wings.warlock.service.conf.impl.RuntimeConfServiceImpl;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(OrderedWarlockConst.AwesomeConfiguration)
public class WarlockAwesomeConfiguration {

    private final static Log log = LogFactory.getLog(WarlockAwesomeConfiguration.class);

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$jooqAutogen, havingValue = "true")
    @ComponentScan(basePackageClasses = SysConstantEnumDao.class)
    public static class WarlockJooqDaoConfiguration {
    }

    @Bean
    @ConditionalOnMissingBean(RuntimeConfService.class)
    public RuntimeConfService runtimeConfService(ObjectProvider<ConversionService> conversionProvider) {
        log.info("Warlock spring-bean runtimeConfService");
        final RuntimeConfServiceImpl bean = new RuntimeConfServiceImpl();
        conversionProvider.ifAvailable(it -> bean.addHandler(RuntimeConfServiceImpl.PropHandler, it));
        bean.addHandler(RuntimeConfServiceImpl.JsonHandler, new JsonConversion());
        bean.addHandler(RuntimeConfServiceImpl.KryoHandler, new KryoConversion());
        return bean;
    }


    /**
     * Database values override project config
     */
    @Bean
    public CommandLineRunnerOrdered runnerRegisterRuntimeMode(ObjectProvider<RuntimeConfService> provider) {
        log.info("Warlock spring-runs runnerRegisterRuntimeMode");
        return new CommandLineRunnerOrdered(OrderedWarlockConst.RunnerRegisterRuntimeMode, ignored -> {
            final RuntimeConfService confService = provider.getIfAvailable();
            if (confService == null) {
                log.info("Warlock conf skip registerRuntimeMode for NULL ");
                return;
            }

            final RunMode dbRunMode = confService.getEnum(RunMode.class);
            final ApiMode dbApiMode = confService.getEnum(ApiMode.class);

            new RuntimeMode() {{
                if (dbRunMode != null) {
                    runMode = dbRunMode;
                }
                if (dbApiMode != null) {
                    apiMode = dbApiMode;
                }
            }};
        });
    }
}
