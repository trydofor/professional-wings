package pro.fessional.wings.warlock.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.faceless.enums.LanguageEnumUtil;
import pro.fessional.wings.faceless.enums.StandardLanguageEnum;
import pro.fessional.wings.faceless.enums.StandardTimezoneEnum;
import pro.fessional.wings.faceless.enums.TimezoneEnumUtil;
import pro.fessional.wings.warlock.spring.prop.WarlockI18nProp;

/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
public class WarlockBeanPostConfiguration {

    private final static Log logger = LogFactory.getLog(WarlockBeanPostConfiguration.class);

    @Autowired    // 静态注入，执行一次即可
    public void registerEnumUtil(WarlockI18nProp warlockI18nProp) throws Exception {
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
}
