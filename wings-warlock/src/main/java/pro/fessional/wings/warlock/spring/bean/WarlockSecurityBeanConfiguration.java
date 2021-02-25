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
import pro.fessional.wings.warlock.security.handler.LoginSuccessHandler;
import pro.fessional.wings.warlock.security.handler.LogoutOkHandler;
import pro.fessional.wings.warlock.security.loginpage.JustAuthLoginPageCombo;
import pro.fessional.wings.warlock.security.loginpage.ListAllLoginPageCombo;
import pro.fessional.wings.warlock.security.userdetails.CommonUserDetailsCombo;
import pro.fessional.wings.warlock.security.userdetails.JustAuthUserAuthnSaver;
import pro.fessional.wings.warlock.security.userdetails.JustAuthUserDetailsCombo;
import pro.fessional.wings.warlock.service.auth.impl.CommonUserAuthnSaver;
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
    @ConditionalOnMissingBean
    public WingsAuthTypeParser wingsAuthTypeParser() {
        logger.info("Wings conf wingsAuthTypeParser");
        final Map<String, Enum<?>> authType = securityProp.mapAuthTypeEnum();
        return new DefaultWingsAuthTypeParser(authType);
    }

    ///////// handler /////////
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

    ///////// UserDetails /////////
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
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$comboJustAuthUserDetails, havingValue = "true")
    public JustAuthUserDetailsCombo justAuthUserDetailsCombo() {
        logger.info("Wings conf justAuthUserDetailsCombo");
        final JustAuthUserDetailsCombo combo = new JustAuthUserDetailsCombo();
        combo.setAutoCreate(securityProp.isAutoCreateUserAuto());
        return combo;
    }

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$comboCommonUserDetails, havingValue = "true")
    public CommonUserDetailsCombo commonUserDetailsCombo() {
        logger.info("Wings conf commonUserDetailsCombo");
        final CommonUserDetailsCombo combo = new CommonUserDetailsCombo();
        combo.setAutoCreate(securityProp.isAutoCreateUserAuto());
        return combo;
    }

    ///////// login page /////////
    @Bean
    @ConditionalOnMissingBean
    public WingsAuthPageHandler wingsAuthPageHandler(ObjectProvider<ComboWingsAuthPageHandler.Combo> combos) {
        logger.info("Wings conf wingsAuthPageHandler");
        ComboWingsAuthPageHandler uds = new ComboWingsAuthPageHandler();
        combos.orderedStream().forEach(it -> {
            logger.info("Wings conf wingsAuthPageHandler add " + it.getClass().getName());
            uds.add(it);
        });
        return uds;
    }

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$comboListAllLoginPage, havingValue = "true")
    public ListAllLoginPageCombo listAllLoginPageCombo() {
        logger.info("Wings conf listAllLoginPageCombo");
        return new ListAllLoginPageCombo();
    }

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$comboJustAuthLoginPage, havingValue = "true")
    public JustAuthLoginPageCombo justAuthLoginPageCombo() {
        return new JustAuthLoginPageCombo();
    }

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$saverJustAuthUserAuthn, havingValue = "true")
    public JustAuthUserAuthnSaver justAuthUserAuthnSaver() {
        // autowired
        return new JustAuthUserAuthnSaver();
    }

    @Bean
    public CommonUserAuthnSaver commonUserAuthnSaver() {
        // autowired
        return new CommonUserAuthnSaver();
    }

    ///////// Listener /////////
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
}
