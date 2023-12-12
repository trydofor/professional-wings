package pro.fessional.wings.warlock.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.concur.impl.RighterInterceptor;
import pro.fessional.wings.slardar.context.SecurityContextUtil;
import pro.fessional.wings.warlock.controller.MvcController;
import pro.fessional.wings.warlock.errorhandle.auto.BindExceptionAdvice;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class WarlockOtherBeanConfiguration {

    private final static Log log = LogFactory.getLog(WarlockOtherBeanConfiguration.class);

    @Configuration(proxyBeanMethods = false)
    @ConditionalWingsEnabled
    @ComponentScan(basePackageClasses = BindExceptionAdvice.class)
    public static class BindingErrorScan {
        public BindingErrorScan() {
            log.info("WarlockShadow spring-scan BindExceptionAdvice");
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalWingsEnabled
    @ComponentScan(basePackageClasses = MvcController.class)
    public static class MvcRestScan {
        public MvcRestScan() {
            log.info("WarlockShadow spring-scan controller");
        }
    }

    @Bean
    @ConditionalWingsEnabled
    public RighterInterceptor.SecretProvider righterSecretProvider() {
        log.info("WarlockShadow spring-bean righterSecretProvider");
        return ss -> {
            final Long uid = SecurityContextUtil.getUserId(false);
            return uid == null ? null : ss.getId() + uid;
        };
    }
}
