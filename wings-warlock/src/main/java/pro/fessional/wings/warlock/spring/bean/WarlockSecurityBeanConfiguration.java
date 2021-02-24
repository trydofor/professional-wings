package pro.fessional.wings.warlock.spring.bean;

import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import pro.fessional.wings.slardar.security.WingsAuthDetailsSource;
import pro.fessional.wings.slardar.security.WingsAuthPageHandler;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;
import pro.fessional.wings.slardar.security.WingsUserDetailsService;
import pro.fessional.wings.slardar.security.impl.ComboWingsAuthDetailsSource;
import pro.fessional.wings.slardar.security.impl.ComboWingsAuthPageHandler;
import pro.fessional.wings.slardar.security.impl.ComboWingsUserDetailsService;
import pro.fessional.wings.slardar.security.impl.DefaultWingsAuthTypeParser;
import pro.fessional.wings.warlock.security.events.WarlockFailedLoginListener;
import pro.fessional.wings.warlock.security.events.WarlockSuccessLoginListener;
import pro.fessional.wings.warlock.security.handler.LoginFailureHandler;
import pro.fessional.wings.warlock.security.handler.LoginPageDefaultHandler;
import pro.fessional.wings.warlock.security.handler.LoginSuccessHandler;
import pro.fessional.wings.warlock.security.handler.LogoutOkHandler;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

import java.util.Map;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration
@ConditionalOnProperty(name = WarlockEnabledProp.Key$security, havingValue = "true")
public class WarlockSecurityBeanConfiguration {

    private final static Log logger = LogFactory.getLog(WarlockSecurityBeanConfiguration.class);

    @Setter(onMethod = @__({@Autowired}))
    private WarlockSecurityProp securityProp;

    @Bean
    @ConditionalOnMissingBean(AuthenticationSuccessHandler.class)
    public AuthenticationSuccessHandler loginSuccessHandler() {
        logger.info("Wings conf loginOkHandler");
        return new LoginSuccessHandler();
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationFailureHandler.class)
    public AuthenticationFailureHandler loginFailureHandler() {
        logger.info("Wings conf loginNgHandler");
        return new LoginFailureHandler();
    }

    @Bean
    @ConditionalOnMissingBean(LogoutSuccessHandler.class)
    public LogoutSuccessHandler logoutOkHandler() {
        logger.info("Wings conf logoutOkHandler");
        return new LogoutOkHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public WingsUserDetailsService wingsUserDetailsService(ObjectProvider<ComboWingsUserDetailsService.Combo<?>> combos) {
        logger.info("Wings conf wingsUserDetailsService");
        ComboWingsUserDetailsService uds = new ComboWingsUserDetailsService();
        combos.orderedStream().forEach(it -> {
            logger.info("Wings conf wingsUserDetailsService add " + it.getClass().getName());
            uds.add(it);
        });
        return uds;
    }

    @Bean
    @ConditionalOnMissingBean
    public WingsAuthDetailsSource<?> wingsAuthDetailsSource(ObjectProvider<ComboWingsAuthDetailsSource.Combo<?>> combos) {
        logger.info("Wings conf wingsAuthDetailsSource");
        ComboWingsAuthDetailsSource uds = new ComboWingsAuthDetailsSource();
        combos.orderedStream().forEach(it -> {
            logger.info("Wings conf wingsAuthDetailsSource add " + it.getClass().getName());
            uds.add(it);
        });
        return uds;
    }

    @Bean
    @ConditionalOnMissingBean
    public WingsAuthPageHandler wingsAuthPageHandler(ObjectProvider<ComboWingsAuthPageHandler.Combo> combos) {
        logger.info("Wings conf wingsAuthDetailsSource");
        ComboWingsAuthPageHandler uds = new ComboWingsAuthPageHandler();
        combos.orderedStream().forEach(it -> {
            logger.info("Wings conf wingsAuthPageHandler add " + it.getClass().getName());
            uds.add(it);
        });
        return uds;
    }

    @Bean
    @ConditionalOnMissingBean
    public WingsAuthTypeParser wingsAuthTypeParser() {
        logger.info("Wings conf wingsAuthTypeParser");
        final Map<String, Enum<?>> authType = securityProp.mapAuthTypeEnum();
        return new DefaultWingsAuthTypeParser(authType);
    }

    @Bean
    public WarlockSuccessLoginListener warlockSuccessLoginListener() {
        logger.info("Wings conf authSuccessListener");
        return new WarlockSuccessLoginListener();
    }

    @Bean
    public WarlockFailedLoginListener warlockFailedLoginListener() {
        logger.info("Wings conf authSuccessListener");
        return new WarlockFailedLoginListener();
    }

    @Bean
    public LoginPageDefaultHandler loginPageDefaultHandler() {
        logger.info("Wings conf loginPageDefaultHandler");
        final LoginPageDefaultHandler handler = new LoginPageDefaultHandler();
        handler.setWarlockSecurityProp(securityProp);
        return handler;
    }
}
