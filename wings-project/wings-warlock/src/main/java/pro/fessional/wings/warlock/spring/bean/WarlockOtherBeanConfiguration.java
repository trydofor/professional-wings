package pro.fessional.wings.warlock.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.servlet.HandlerExceptionResolver;
import pro.fessional.wings.faceless.database.helper.DatabaseChecker;
import pro.fessional.wings.slardar.concur.impl.RighterInterceptor;
import pro.fessional.wings.slardar.context.GlobalAttributeHolder;
import pro.fessional.wings.slardar.security.WingsUserDetails;
import pro.fessional.wings.slardar.serialize.JsonConversion;
import pro.fessional.wings.slardar.serialize.KryoConversion;
import pro.fessional.wings.warlock.errorhandle.AllExceptionResolver;
import pro.fessional.wings.warlock.errorhandle.CodeExceptionResolver;
import pro.fessional.wings.warlock.service.conf.RuntimeConfService;
import pro.fessional.wings.warlock.service.conf.impl.RuntimeConfServiceImpl;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockErrorProp;

import javax.sql.DataSource;

import static pro.fessional.wings.warlock.service.user.WarlockUserAttribute.SaltByUid;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
public class WarlockOtherBeanConfiguration {

    private final static Log logger = LogFactory.getLog(WarlockOtherBeanConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(name = "codeExceptionResolver")
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$codeExceptionHandler, havingValue = "true")
    public HandlerExceptionResolver codeExceptionResolver(MessageSource messageSource, WarlockErrorProp prop) {
        logger.info("Wings conf codeExceptionResolver");
        final WarlockErrorProp.CodeException cp = prop.getCodeException();
        final CodeExceptionResolver bean = new CodeExceptionResolver(messageSource, cp.getHttpStatus(), cp.getContentType(), cp.getResponseBody());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1000);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(name = "allExceptionResolver")
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$allExceptionHandler, havingValue = "true")
    public HandlerExceptionResolver allExceptionResolver(WarlockErrorProp prop) {
        logger.info("Wings conf allExceptionResolver");
        final WarlockErrorProp.CodeException cp = prop.getAllException();
        final AllExceptionResolver bean = new AllExceptionResolver(cp.getHttpStatus(), cp.getContentType(), cp.getResponseBody());
        bean.setOrder(Ordered.LOWEST_PRECEDENCE);
        return bean;
    }

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$checkDatabase, havingValue = "true")
    public CommandLineRunner databaseChecker(DataSource dataSource, ApplicationContext context) {
        logger.info("Wings conf databaseChecker");
        return args -> {
            try {
                DatabaseChecker.version(dataSource);
                DatabaseChecker.timezone(dataSource);
            }
            catch (Exception e) {
                logger.error("failed to check timezone", e);
                SpringApplication.exit(context);
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public RighterInterceptor.SecretProvider righterInterceptorSecretProvider() {
        logger.info("Wings conf righterInterceptorSecretProvider");
        return auth -> {
            final Object dtl = auth.getDetails();
            if (dtl instanceof WingsUserDetails) {
                GlobalAttributeHolder.getAttr(SaltByUid, ((WingsUserDetails) dtl).getUserId());
            }
            return null;
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public RuntimeConfService runtimeConfService(ConversionService conversion) {
        logger.info("Wings conf runtimeConfService");
        final RuntimeConfServiceImpl bean = new RuntimeConfServiceImpl();
        bean.addHandler(RuntimeConfServiceImpl.PropHandler, conversion);
        bean.addHandler(RuntimeConfServiceImpl.JsonHandler, new JsonConversion());
        bean.addHandler(RuntimeConfServiceImpl.KryoHandler, new KryoConversion());
        return bean;
    }
}
