package pro.fessional.wings.warlock.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.faceless.enums.LanguageEnumUtil;
import pro.fessional.wings.faceless.enums.StandardLanguageEnum;
import pro.fessional.wings.faceless.enums.StandardTimezoneEnum;
import pro.fessional.wings.faceless.enums.TimezoneEnumUtil;
import pro.fessional.wings.slardar.context.GlobalAttributeHolder;
import pro.fessional.wings.slardar.context.RighterInterceptor;
import pro.fessional.wings.warlock.service.auth.impl.DefaultUserDetailsCombo;
import pro.fessional.wings.warlock.spring.prop.WarlockI18nProp;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

import java.util.function.Function;

import static pro.fessional.wings.warlock.service.user.WarlockUserAttribute.SaltByUid;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration
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

    @Bean
    @ConditionalOnBean(DefaultUserDetailsCombo.class)
    public BeanPostProcessor beanPostDefaultUserDetailsCombo(WarlockSecurityProp securityProp) {
        logger.info("Wings conf beanPostDefaultUserDetailsCombo");
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
                if (bean instanceof DefaultUserDetailsCombo) {
                    ((DefaultUserDetailsCombo) bean).addAutoRegisterType(securityProp.mapAutoregAuthEnum());
                }
                return bean;
            }
        };
    }

    @Bean
    @ConditionalOnBean(RighterInterceptor.class)
    public BeanPostProcessor beanPostRighterInterceptor() {
        logger.info("Wings conf beanPostRighterInterceptor");
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
                if (bean instanceof RighterInterceptor) {
                    RighterInterceptor ri = (RighterInterceptor) bean;
                    final Function<Object, String> ori = ri.getSecretProvider();
                    ri.setSecretProvider(key -> {
                        String pass = null;
                        if (key instanceof Long) {
                            pass = GlobalAttributeHolder.getAttr(SaltByUid, (Long) key);
                        }
                        if (pass == null) {
                            ori.apply(key);
                        }
                        return pass;
                    });
                }
                return bean;
            }
        };
    }
}
