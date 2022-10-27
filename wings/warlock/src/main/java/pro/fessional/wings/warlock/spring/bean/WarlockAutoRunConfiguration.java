package pro.fessional.wings.warlock.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import pro.fessional.wings.faceless.database.helper.DatabaseChecker;
import pro.fessional.wings.faceless.enums.LanguageEnumUtil;
import pro.fessional.wings.faceless.enums.StandardLanguageEnum;
import pro.fessional.wings.faceless.enums.StandardTimezoneEnum;
import pro.fessional.wings.faceless.enums.TimezoneEnumUtil;
import pro.fessional.wings.warlock.service.conf.RuntimeConfService;
import pro.fessional.wings.warlock.service.conf.mode.ApiMode;
import pro.fessional.wings.warlock.service.conf.mode.RunMode;
import pro.fessional.wings.warlock.service.conf.mode.RuntimeMode;
import pro.fessional.wings.warlock.spring.prop.WarlockCheckProp;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockI18nProp;
import pro.fessional.wings.warlock.spring.prop.WarlockRuntimeProp;

import javax.sql.DataSource;

/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
public class WarlockAutoRunConfiguration {

    private final static Log log = LogFactory.getLog(WarlockAutoRunConfiguration.class);

    @Bean
    public CommandLineRunner registerEnumUtilRunner(ObjectProvider<WarlockI18nProp> provider) {
        log.info("Warlock spring-runs registerEnumUtilRunner");
        return (arg) -> {
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
        };
    }

    @Bean    // 静态注入，执行一次即可
    public CommandLineRunner registerRuntimeModeRunner(ObjectProvider<RuntimeConfService> provider, ObjectProvider<WarlockRuntimeProp> properties) {
        log.info("Warlock spring-runs registerRuntimeModeRunner");
        return (arg) -> {
            final RuntimeConfService confService = provider.getIfAvailable();
            if (confService == null) {
                log.info("Warlock conf skip registerRuntimeMode for NULL ");
                return;
            }

            final WarlockRuntimeProp prop = properties.getIfAvailable();

            RuntimeMode.setRunMode(() -> {
                RunMode runMode = confService.getEnum(RunMode.class);
                if (runMode == null && prop != null) {
                    runMode = prop.getRunMode();
                }
                log.info("Warlock conf registerRuntimeMode RunMode=" + runMode);
                return runMode == null ? RunMode.Local : runMode;
            });

            RuntimeMode.setApiMode(() -> {
                ApiMode apiMode = confService.getEnum(ApiMode.class);
                if (apiMode == null && prop != null) {
                    apiMode = prop.getApiMode();
                }

                log.info("Warlock conf registerRuntimeMode apiMode=" + apiMode);
                return apiMode == null ? ApiMode.Nothing : apiMode;
            });
        };
    }

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$checkDatabase, havingValue = "true")
    @Order(Ordered.HIGHEST_PRECEDENCE + 1000)
    public CommandLineRunner databaseChecker(DataSource dataSource, WarlockCheckProp prop) {
        log.info("Warlock spring-runs databaseChecker");
        return args -> {
            DatabaseChecker.version(dataSource);
            DatabaseChecker.timezone(dataSource, prop.getTzOffset(), prop.isTzFail());
        };
    }
}
