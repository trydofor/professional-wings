package pro.fessional.wings.warlock.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
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
import pro.fessional.wings.warlock.security.handler.LoginNgHandler;
import pro.fessional.wings.warlock.security.handler.LoginOkHandler;
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
@RequiredArgsConstructor
public class WarlockAuthBeanConfiguration {

    private final static Log logger = LogFactory.getLog(WarlockAuthBeanConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationSuccessHandler loginOkHandler() {
        logger.info("Wings conf loginOkHandler");
        return new LoginOkHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationFailureHandler loginNgHandler() {
        logger.info("Wings conf loginNgHandler");
        return new LoginNgHandler();
    }

    @Bean
    @ConditionalOnMissingBean
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
    public WingsAuthTypeParser wingsAuthTypeParser(WarlockSecurityProp config) {
        logger.info("Wings conf wingsAuthTypeParser");
        final Map<String, Enum<?>> authType = config.mapAuthTypeEnum();
        return new DefaultWingsAuthTypeParser(authType);
    }
}
