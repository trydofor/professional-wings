package pro.fessional.wings.warlock.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import pro.fessional.wings.faceless.enums.LanguageEnumUtil;
import pro.fessional.wings.faceless.enums.StandardLanguageEnum;
import pro.fessional.wings.faceless.enums.StandardTimezoneEnum;
import pro.fessional.wings.faceless.enums.TimezoneEnumUtil;
import pro.fessional.wings.warlock.errorhandle.CodeExceptionResolver;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockErrorProp;
import pro.fessional.wings.warlock.spring.prop.WarlockI18nProp;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration
public class WarlockOtherBeanConfiguration {

    private final static Log logger = LogFactory.getLog(WarlockOtherBeanConfiguration.class);

    @Autowired
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

    @Bean
    @ConditionalOnMissingBean(name = "codeExceptionResolver")
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$codeExceptionHandler, havingValue = "true")
    public HandlerExceptionResolver codeExceptionResolver(MessageSource messageSource, WarlockErrorProp prop) {
        logger.info("Wings conf codeExceptionResolver");
        final WarlockErrorProp.CodeException cp = prop.getCodeException();
        return new CodeExceptionResolver(messageSource, cp.getHttpStatus(), cp.getContentType(), cp.getResponseBody());
    }
}
