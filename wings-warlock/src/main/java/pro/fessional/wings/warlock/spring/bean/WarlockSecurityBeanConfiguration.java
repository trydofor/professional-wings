package pro.fessional.wings.warlock.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import pro.fessional.wings.faceless.database.manual.single.modify.commitjournal.CommitJournalModify;
import pro.fessional.wings.faceless.service.lightid.BlockIdProvider;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.slardar.cache.WingsCache;
import pro.fessional.wings.slardar.security.WingsAuthDetailsSource;
import pro.fessional.wings.slardar.security.WingsAuthPageHandler;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;
import pro.fessional.wings.slardar.security.WingsUserDetailsService;
import pro.fessional.wings.slardar.security.impl.ComboWingsAuthDetailsSource;
import pro.fessional.wings.slardar.security.impl.ComboWingsAuthPageHandler;
import pro.fessional.wings.slardar.security.impl.ComboWingsUserDetailsService;
import pro.fessional.wings.slardar.security.impl.DefaultWingsAuthTypeParser;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.slardar.spring.prop.SlardarSessionProp;
import pro.fessional.wings.warlock.security.handler.LoginFailureHandler;
import pro.fessional.wings.warlock.security.handler.LoginSuccessHandler;
import pro.fessional.wings.warlock.security.handler.LogoutOkHandler;
import pro.fessional.wings.warlock.security.listener.WarlockFailedLoginListener;
import pro.fessional.wings.warlock.security.listener.WarlockSuccessLoginListener;
import pro.fessional.wings.warlock.security.loginpage.JustAuthLoginPageCombo;
import pro.fessional.wings.warlock.security.loginpage.ListAllLoginPageCombo;
import pro.fessional.wings.warlock.security.userdetails.JustAuthUserAuthnCombo;
import pro.fessional.wings.warlock.security.userdetails.JustAuthUserDetailsCombo;
import pro.fessional.wings.warlock.security.userdetails.NonceUserDetailsCombo;
import pro.fessional.wings.warlock.service.auth.impl.ComboWarlockAuthnService;
import pro.fessional.wings.warlock.service.auth.impl.ComboWarlockAuthzService;
import pro.fessional.wings.warlock.service.auth.impl.DefaultPermRoleCombo;
import pro.fessional.wings.warlock.service.auth.impl.DefaultUserAuthnCombo;
import pro.fessional.wings.warlock.service.auth.impl.DefaultUserDetailsCombo;
import pro.fessional.wings.warlock.service.other.TerminalJournalService;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

import java.util.Map;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration
@ConditionalOnProperty(name = WarlockEnabledProp.Key$securityBean, havingValue = "true")
@RequiredArgsConstructor
public class WarlockSecurityBeanConfiguration {

    private final static Log logger = LogFactory.getLog(WarlockSecurityBeanConfiguration.class);

    private final WarlockSecurityProp securityProp;
    private final ApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean(WingsAuthTypeParser.class)
    public WingsAuthTypeParser wingsAuthTypeParser() {
        logger.info("Wings conf wingsAuthTypeParser");
        final Map<String, Enum<?>> authType = securityProp.mapAuthTypeEnum();
        return new DefaultWingsAuthTypeParser(authType);
    }

    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @ConditionalOnProperty(name = SlardarEnabledProp.Key$terminal, havingValue = "true")
    public TerminalJournalService terminalJournalService(
            LightIdService lightIdService,
            BlockIdProvider blockIdProvider,
            CommitJournalModify journalModify
    ) {
        logger.info("Wings conf terminalJournalService");
        return new TerminalJournalService(lightIdService, blockIdProvider, journalModify);
    }

    ///////// handler /////////
    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @ConditionalOnMissingBean(AuthenticationSuccessHandler.class)
    public AuthenticationSuccessHandler loginSuccessHandler(SlardarSessionProp sessionProp) {
        final String headerName = sessionProp.getHeaderName();
        logger.info("Wings conf loginSuccessHandler by header.name=" + headerName);
        return new LoginSuccessHandler(securityProp.getLoginSuccessBody(), headerName);
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationFailureHandler.class)
    public AuthenticationFailureHandler loginFailureHandler() {
        logger.info("Wings conf loginFailureHandler");
        return new LoginFailureHandler(securityProp.getLoginFailureBody());
    }

    @Bean
    @ConditionalOnMissingBean(LogoutSuccessHandler.class)
    public LogoutSuccessHandler logoutSuccessHandler() {
        logger.info("Wings conf logoutSuccessHandler");
        return new LogoutOkHandler(securityProp.getLogoutSuccessBody());
    }

    ///////// Authz & AuthN /////////

    @Bean
    @ConditionalOnMissingBean(ComboWarlockAuthnService.class)
    public ComboWarlockAuthnService comboWarlockAuthnService() {
        logger.info("Wings conf comboWarlockAuthnService");
        return new ComboWarlockAuthnService();
    }

    @Bean
    @ConditionalOnMissingBean(ComboWarlockAuthzService.class)
    public ComboWarlockAuthzService comboWarlockAuthzService() {
        logger.info("Wings conf comboWarlockAuthzService");
        return new ComboWarlockAuthzService();
    }

    @Bean
    @ConditionalOnMissingBean(DefaultPermRoleCombo.class)
    public DefaultPermRoleCombo defaultPermRoleCombo() {
        logger.info("Wings conf defaultPermRoleCombo");
        return new DefaultPermRoleCombo();
    }

    @Bean
    @ConditionalOnMissingBean(DefaultUserAuthnCombo.class)
    public DefaultUserAuthnCombo defaultUserAuthnCombo() {
        logger.info("Wings conf defaultUserAuthnCombo");
        return new DefaultUserAuthnCombo();
    }

    @Bean
    @ConditionalOnMissingBean(DefaultUserDetailsCombo.class)
    public DefaultUserDetailsCombo defaultUserDetailsCombo() {
        logger.info("Wings conf defaultUserDetailsCombo");
        return new DefaultUserDetailsCombo();
    }

    ///////// UserDetails /////////
    @Bean
    @ConditionalOnMissingBean(WingsUserDetailsService.class)
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
    @ConditionalOnMissingBean(WingsAuthDetailsSource.class)
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
        return new JustAuthUserDetailsCombo();
    }

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$comboNonceUserDetails, havingValue = "true")
    public NonceUserDetailsCombo nonceUserDetailsCombo() {
        logger.info("Wings conf nonceUserDetailsCombo");
        final NonceUserDetailsCombo combo = new NonceUserDetailsCombo();
        combo.setAcceptNonceType(securityProp.mapNonceAuthEnum());
        final String cn = WingsCache.Level.join(securityProp.getNonceCacheLevel(), "NonceUserDetailsCombo");
        combo.setCacheName(cn);
        final CacheManager cm = applicationContext.getBean(securityProp.getNonceCacheManager(), CacheManager.class);
        combo.setCacheManager(cm);
        return combo;
    }

    ///////// login page /////////
    @Bean
    @ConditionalOnMissingBean(WingsAuthPageHandler.class)
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
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$comboJustAuthUserAuthn, havingValue = "true")
    public JustAuthUserAuthnCombo justAuthUserAuthnCombo() {
        // autowired
        return new JustAuthUserAuthnCombo();
    }

    @Bean
    @ConditionalOnMissingBean(GrantedAuthorityDefaults.class)
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(securityProp.getRolePrefix());
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
