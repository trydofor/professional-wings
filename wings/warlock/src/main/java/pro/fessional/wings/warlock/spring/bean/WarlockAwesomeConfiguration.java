package pro.fessional.wings.warlock.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import pro.fessional.wings.silencer.modulate.ApiMode;
import pro.fessional.wings.silencer.modulate.RunMode;
import pro.fessional.wings.silencer.modulate.RuntimeMode;
import pro.fessional.wings.silencer.runner.CommandLineRunnerOrdered;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.serialize.JsonConversion;
import pro.fessional.wings.slardar.serialize.KryoConversion;
import pro.fessional.wings.warlock.database.WarlockDatabase;
import pro.fessional.wings.warlock.service.conf.RuntimeConfService;
import pro.fessional.wings.warlock.service.conf.impl.RuntimeConfServiceImpl;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class WarlockAwesomeConfiguration {

    private final static Log log = LogFactory.getLog(WarlockAwesomeConfiguration.class);

    @Configuration(proxyBeanMethods = false)
    @ConditionalWingsEnabled
    @ComponentScan(basePackageClasses = WarlockDatabase.class)
    public static class JooqDaoScan {
        public JooqDaoScan() {
            log.info("Warlock spring-scan SysConstantEnumDao");
        }
    }

    /**
     * Database values override project config
     */
    @Bean
    @ConditionalWingsEnabled
    public CommandLineRunnerOrdered registerRuntimeModeRunner(ObjectProvider<RuntimeConfService> provider) {
        log.info("Warlock spring-runs registerRuntimeModeRunner");
        return new CommandLineRunnerOrdered(WingsOrdered.Lv3Service, ignored -> {
            final RuntimeConfService confService = provider.getIfAvailable();
            if (confService == null) {
                log.info("Warlock conf skip registerRuntimeMode for NULL ");
                return;
            }

            final RunMode dbRunMode = confService.getEnum(RunMode.class);
            final ApiMode dbApiMode = confService.getEnum(ApiMode.class);
            log.info("Warlock conf RuntimeMode, runMode=" + dbRunMode + ", apiMode=" + dbApiMode);
            new RuntimeMode(dbRunMode, dbApiMode) {};
        });
    }

    @Bean
    @ConditionalWingsEnabled
    public RuntimeConfService runtimeConfService(ObjectProvider<ConversionService> conversionProvider) {
        log.info("Warlock spring-bean runtimeConfService");
        final RuntimeConfServiceImpl bean = new RuntimeConfServiceImpl();
        conversionProvider.ifAvailable(it -> bean.putHandler(RuntimeConfServiceImpl.PropHandler, it));
        bean.putHandler(RuntimeConfServiceImpl.JsonHandler, new JsonConversion());
        bean.putHandler(RuntimeConfServiceImpl.KryoHandler, new KryoConversion());
        return bean;
    }
}
