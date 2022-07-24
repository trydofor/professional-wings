package pro.fessional.wings.warlock.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.faceless.enums.LanguageEnumUtil;
import pro.fessional.wings.faceless.enums.StandardLanguageEnum;
import pro.fessional.wings.faceless.enums.StandardTimezoneEnum;
import pro.fessional.wings.faceless.enums.TimezoneEnumUtil;
import pro.fessional.wings.warlock.service.conf.RuntimeConfService;
import pro.fessional.wings.warlock.service.conf.mode.ApiMode;
import pro.fessional.wings.warlock.service.conf.mode.RunMode;
import pro.fessional.wings.warlock.service.conf.mode.RuntimeMode;
import pro.fessional.wings.warlock.spring.prop.WarlockI18nProp;
import pro.fessional.wings.warlock.spring.prop.WarlockRuntimeProp;

/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
public class WarlockPostRunnerConfiguration {

    private final static Log logger = LogFactory.getLog(WarlockPostRunnerConfiguration.class);

    @Bean
    public CommandLineRunner registerEnumUtilRunner(ObjectProvider<WarlockI18nProp> provider) {
        return (arg) -> {
            final WarlockI18nProp warlockI18nProp = provider.getIfAvailable();
            if (warlockI18nProp == null) {
                logger.info("Wings conf skip registerEnumUtil for NULL ");
                return;
            }

            for (String s : warlockI18nProp.getLocaleEnum()) {
                logger.info("Wings conf locale enum " + s);
                final Class<?> cz = Class.forName(s);
                if (!(cz.isEnum() && StandardLanguageEnum.class.isAssignableFrom(cz))) {
                    throw new IllegalArgumentException(s + " is not enum and StandardLanguageEnum");
                }
                for (Object o : cz.getEnumConstants()) {
                    LanguageEnumUtil.register((StandardLanguageEnum) o);
                }
            }

            for (String s : warlockI18nProp.getZoneidEnum()) {
                logger.info("Wings conf zoneid enum " + s);
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
        return (arg) -> {
            final RuntimeConfService confService = provider.getIfAvailable();
            if (confService == null) {
                logger.info("Wings conf skip registerRuntimeMode for NULL ");
                return;
            }

            final WarlockRuntimeProp prop = properties.getIfAvailable();

            RuntimeMode.setRunMode(() -> {
                RunMode runMode = confService.getEnum(RunMode.class);
                if (runMode == null && prop != null) {
                    runMode = prop.getRunMode();
                }
                logger.info("Wings conf registerRuntimeMode RunMode=" + runMode);
                return runMode == null ? RunMode.Local : runMode;
            });

            RuntimeMode.setApiMode(() -> {
                ApiMode apiMode = confService.getEnum(ApiMode.class);
                if (apiMode == null && prop != null) {
                    apiMode = prop.getApiMode();
                }

                logger.info("Wings conf registerRuntimeMode apiMode=" + apiMode);
                return apiMode == null ? ApiMode.Nothing : apiMode;
            });
        };
    }
}
