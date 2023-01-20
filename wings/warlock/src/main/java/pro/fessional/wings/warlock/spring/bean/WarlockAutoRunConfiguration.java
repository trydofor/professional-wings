package pro.fessional.wings.warlock.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.faceless.database.helper.DatabaseChecker;
import pro.fessional.wings.faceless.enums.LanguageEnumUtil;
import pro.fessional.wings.faceless.enums.StandardLanguageEnum;
import pro.fessional.wings.faceless.enums.StandardTimezoneEnum;
import pro.fessional.wings.faceless.enums.TimezoneEnumUtil;
import pro.fessional.wings.silencer.modulate.ApiMode;
import pro.fessional.wings.silencer.modulate.RunMode;
import pro.fessional.wings.silencer.modulate.RuntimeMode;
import pro.fessional.wings.silencer.spring.help.CommandLineRunnerOrdered;
import pro.fessional.wings.warlock.constants.WarlockOrderConst;
import pro.fessional.wings.warlock.service.conf.RuntimeConfService;
import pro.fessional.wings.warlock.spring.prop.WarlockCheckProp;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockI18nProp;

import javax.sql.DataSource;

/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(WarlockOrderConst.AutoRunConfiguration)
public class WarlockAutoRunConfiguration {

    private final static Log log = LogFactory.getLog(WarlockAutoRunConfiguration.class);

    @Bean
    public CommandLineRunnerOrdered runnerRegisterEnumUtil(ObjectProvider<WarlockI18nProp> provider) {
        log.info("Warlock spring-runs runnerRegisterEnumUtil");
        return new CommandLineRunnerOrdered(WarlockOrderConst.RunnerRegisterEnumUtil, args -> {
            final WarlockI18nProp warlockI18nProp = provider.getIfAvailable();
            if (warlockI18nProp == null) {
                log.info("Warlock conf skip registerEnumUtil for NULL ");
                return;
            }

            for (String s : warlockI18nProp.getLocaleEnum()) {
                log.info("Warlock conf locale enum " + s);
                final Class<?> cz = Class.forName(s);
                if (!(cz.isEnum() && StandardLanguageEnum.class.isAssignableFrom(cz))) {
                    throw new IllegalArgumentException(s + " is not enum and StandardLanguageEnum");
                }
                for (Object o : cz.getEnumConstants()) {
                    LanguageEnumUtil.register((StandardLanguageEnum) o);
                }
            }

            for (String s : warlockI18nProp.getZoneidEnum()) {
                log.info("Warlock conf zoneid enum " + s);
                final Class<?> cz = Class.forName(s);
                if (!(cz.isEnum() && StandardTimezoneEnum.class.isAssignableFrom(cz))) {
                    throw new IllegalArgumentException(s + " is not enum and StandardTimezoneEnum");
                }
                for (Object o : cz.getEnumConstants()) {
                    TimezoneEnumUtil.register((StandardTimezoneEnum) o);
                }
            }
        });
    }

    @Bean    // 数据库值覆盖工程配置
    public CommandLineRunnerOrdered runnerRegisterRuntimeMode(ObjectProvider<RuntimeConfService> provider) {
        log.info("Warlock spring-runs runnerRegisterRuntimeMode");
        return new CommandLineRunnerOrdered(WarlockOrderConst.RunnerRegisterRuntimeMode, args -> {
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

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$checkDatabase, havingValue = "true")
    public CommandLineRunnerOrdered runnerDatabaseChecker(DataSource dataSource, WarlockCheckProp prop) {
        log.info("Warlock spring-runs runnerDatabaseChecker");
        return new CommandLineRunnerOrdered(WarlockOrderConst.RunnerDatabaseChecker, args -> {
            DatabaseChecker.version(dataSource);
            DatabaseChecker.timezone(dataSource, prop.getTzOffset(), prop.isTzFail());
        });
    }
}
