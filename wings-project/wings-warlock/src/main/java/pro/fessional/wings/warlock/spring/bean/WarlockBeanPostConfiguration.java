package pro.fessional.wings.warlock.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
public class WarlockBeanPostConfiguration {

    private final static Log logger = LogFactory.getLog(WarlockBeanPostConfiguration.class);

    @Autowired    // 静态注入，执行一次即可
    public void registerEnumUtil(ObjectProvider<WarlockI18nProp> provider) throws Exception {
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
    }

    @Autowired    // 静态注入，执行一次即可
    public void registerRuntimeMode(ObjectProvider<RuntimeConfService> provider) {
        final RuntimeConfService confService = provider.getIfAvailable();
        if (confService == null) {
            logger.info("Wings conf skip registerRuntimeMode for NULL ");
            return;
        }

        RuntimeMode.setRunMode(() -> {
            final RunMode runMode = confService.getEnum(RunMode.class);
            logger.info("Wings conf registerRuntimeMode RunMode=" + runMode);
            return runMode == null ? RunMode.Local : runMode;
        });

        RuntimeMode.setApiMode(() -> {
            final ApiMode apiMode = confService.getEnum(ApiMode.class);
            logger.info("Wings conf registerRuntimeMode apiMode=" + apiMode);
            return apiMode == null ? ApiMode.Nothing : apiMode;
        });
    }
}
