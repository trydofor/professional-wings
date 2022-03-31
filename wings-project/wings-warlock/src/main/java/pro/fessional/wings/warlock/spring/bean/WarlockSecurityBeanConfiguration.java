package pro.fessional.wings.warlock.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.util.AntPathMatcher;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.faceless.database.manual.single.modify.commitjournal.CommitJournalModify;
import pro.fessional.wings.faceless.service.lightid.BlockIdProvider;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.slardar.cache.WingsCache;
import pro.fessional.wings.slardar.security.WingsAuthDetailsSource;
import pro.fessional.wings.slardar.security.WingsAuthPageHandler;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;
import pro.fessional.wings.slardar.security.WingsUserDetailsService;
import pro.fessional.wings.slardar.security.impl.ComboWingsAuthCheckService;
import pro.fessional.wings.slardar.security.impl.ComboWingsAuthDetailsSource;
import pro.fessional.wings.slardar.security.impl.ComboWingsAuthPageHandler;
import pro.fessional.wings.slardar.security.impl.ComboWingsUserDetailsService;
import pro.fessional.wings.slardar.security.impl.DefaultWingsAuthTypeParser;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.warlock.security.handler.LoginFailureHandler;
import pro.fessional.wings.warlock.security.handler.LoginSuccessHandler;
import pro.fessional.wings.warlock.security.handler.LogoutOkHandler;
import pro.fessional.wings.warlock.security.justauth.AuthStateBuilder;
import pro.fessional.wings.warlock.security.listener.WarlockFailedLoginListener;
import pro.fessional.wings.warlock.security.listener.WarlockSuccessLoginListener;
import pro.fessional.wings.warlock.security.loginpage.JustAuthLoginPageCombo;
import pro.fessional.wings.warlock.security.loginpage.ListAllLoginPageCombo;
import pro.fessional.wings.warlock.security.userdetails.JustAuthUserAuthnAutoReg;
import pro.fessional.wings.warlock.security.userdetails.JustAuthUserDetailsCombo;
import pro.fessional.wings.warlock.security.userdetails.MemoryUserDetailsCombo;
import pro.fessional.wings.warlock.security.userdetails.NonceUserDetailsCombo;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService.Details;
import pro.fessional.wings.warlock.service.auth.impl.AuthAppPermChecker;
import pro.fessional.wings.warlock.service.auth.impl.AuthZonePermChecker;
import pro.fessional.wings.warlock.service.auth.impl.ComboWarlockAuthnService;
import pro.fessional.wings.warlock.service.auth.impl.ComboWarlockAuthzService;
import pro.fessional.wings.warlock.service.auth.impl.DefaultPermRoleCombo;
import pro.fessional.wings.warlock.service.auth.impl.DefaultUserAuthnAutoReg;
import pro.fessional.wings.warlock.service.auth.impl.DefaultUserDetailsCombo;
import pro.fessional.wings.warlock.service.auth.impl.MemoryTypedAuthzCombo;
import pro.fessional.wings.warlock.service.grant.WarlockGrantService;
import pro.fessional.wings.warlock.service.grant.impl.WarlockGrantServiceImpl;
import pro.fessional.wings.warlock.service.other.TerminalJournalService;
import pro.fessional.wings.warlock.service.perm.WarlockPermNormalizer;
import pro.fessional.wings.warlock.service.perm.WarlockPermService;
import pro.fessional.wings.warlock.service.perm.WarlockRoleService;
import pro.fessional.wings.warlock.service.perm.impl.WarlockPermServiceImpl;
import pro.fessional.wings.warlock.service.perm.impl.WarlockRoleServiceImpl;
import pro.fessional.wings.warlock.service.user.WarlockUserAuthnService;
import pro.fessional.wings.warlock.service.user.WarlockUserBasisService;
import pro.fessional.wings.warlock.service.user.WarlockUserLoginService;
import pro.fessional.wings.warlock.service.user.impl.WarlockUserAuthnServiceImpl;
import pro.fessional.wings.warlock.service.user.impl.WarlockUserBasisServiceImpl;
import pro.fessional.wings.warlock.service.user.impl.WarlockUserLoginServiceImpl;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockJustAuthProp;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp.Ma;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp.Mu;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
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
        authType.put("null", Null.Enm);
        return new DefaultWingsAuthTypeParser(authType);
    }

    @Bean
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
    @ConditionalOnMissingBean(AuthenticationSuccessHandler.class)
    public AuthenticationSuccessHandler loginSuccessHandler() {
        logger.info("Wings conf loginSuccessHandler");
        return new LoginSuccessHandler();
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationFailureHandler.class)
    public AuthenticationFailureHandler loginFailureHandler() {
        logger.info("Wings conf loginFailureHandler");
        return new LoginFailureHandler();
    }

    @Bean
    @ConditionalOnMissingBean(LogoutSuccessHandler.class)
    public LogoutSuccessHandler logoutSuccessHandler() {
        logger.info("Wings conf logoutSuccessHandler");
        return new LogoutOkHandler();
    }

    ///////// AuthZ & AuthN /////////

    @Bean
    @ConditionalOnMissingBean(WarlockPermNormalizer.class)
    public WarlockPermNormalizer warlockPermNormalizer(GrantedAuthorityDefaults gad) {
        logger.info("Wings conf warlockPermNormalizer");
        final WarlockPermNormalizer bean = new WarlockPermNormalizer();
        bean.setRolePrefix(gad.getRolePrefix());
        return bean;
    }

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
        final ComboWarlockAuthzService bean = new ComboWarlockAuthzService();
        bean.setAuthorityRole(securityProp.isAuthorityRole());
        bean.setAuthorityPerm(securityProp.isAuthorityPerm());
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(DefaultPermRoleCombo.class)
    public DefaultPermRoleCombo defaultPermRoleCombo() {
        logger.info("Wings conf defaultPermRoleCombo");
        return new DefaultPermRoleCombo();
    }

    @Bean
    @ConditionalOnMissingBean(DefaultUserDetailsCombo.class)
    public DefaultUserDetailsCombo defaultUserDetailsCombo() {
        logger.info("Wings conf defaultUserDetailsCombo");
        final DefaultUserDetailsCombo bean = new DefaultUserDetailsCombo();
        bean.setAutoRegisterType(securityProp.mapAutoregAuthEnum());
        return bean;
    }

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$comboJustAuthAutoreg, havingValue = "true")
    @ConditionalOnMissingBean(JustAuthUserAuthnAutoReg.class)
    public JustAuthUserAuthnAutoReg justAuthUserAuthnAutoReg() {
        logger.info("Wings conf justAuthUserAuthnAutoReg");
        return new JustAuthUserAuthnAutoReg();
    }

    @Bean
    @ConditionalOnMissingBean(DefaultUserAuthnAutoReg.class)
    public DefaultUserAuthnAutoReg defaultUserAuthnAutoReg() {
        // 存在子类，则不需要此bean，如JustAuthUserAuthnAutoReg
        logger.info("Wings conf defaultUserAuthnAutoReg");
        return new DefaultUserAuthnAutoReg();
    }

    @Bean
    @ConditionalOnMissingBean(WarlockGrantService.class)
    public WarlockGrantService warlockGrantService() {
        // 存在子类，则不需要此bean，如JustAuthUserAuthnAutoReg
        logger.info("Wings conf warlockGrantService");
        return new WarlockGrantServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(WarlockPermService.class)
    public WarlockPermService warlockPermService() {
        logger.info("Wings conf warlockPermService");
        return new WarlockPermServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(WarlockRoleService.class)
    public WarlockRoleService warlockRoleService() {
        logger.info("Wings conf warlockRoleService");
        return new WarlockRoleServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(WarlockUserAuthnService.class)
    public WarlockUserAuthnService warlockUserAuthnService() {
        logger.info("Wings conf warlockUserAuthnService");
        return new WarlockUserAuthnServiceImpl();
    }

    ///////// UserDetails /////////

    @Bean
    @ConditionalOnMissingBean(WarlockUserBasisService.class)
    public WarlockUserBasisService warlockUserBasisService() {
        logger.info("Wings conf warlockUserBasisService");
        return new WarlockUserBasisServiceImpl();
    }

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$comboNonceUserDetails, havingValue = "true")
    @ConditionalOnMissingBean(NonceUserDetailsCombo.class)
    public NonceUserDetailsCombo nonceUserDetailsCombo(List<DefaultUserDetailsCombo> combos) {
        logger.info("Wings conf nonceUserDetailsCombo");
        final NonceUserDetailsCombo combo = new NonceUserDetailsCombo();
        combo.setAcceptNonceType(securityProp.mapNonceAuthEnum());
        final String cn = WingsCache.Level.join(securityProp.getNonceCacheLevel(), "NonceUserDetailsCombo");
        combo.setCacheName(cn);
        final CacheManager cm = applicationContext.getBean(securityProp.getNonceCacheManager(), CacheManager.class);
        combo.setCacheManager(cm);
        combo.setDetailCombos(combos);
        return combo;
    }

    @Bean
    @ConditionalOnMissingBean(MemoryUserDetailsCombo.class)
    public MemoryUserDetailsCombo memoryUserDetailsCombo(WingsAuthTypeParser typeParser) {
        logger.info("Wings conf memoryUserDetailsCombo");
        final MemoryUserDetailsCombo bean = new MemoryUserDetailsCombo();
        for (Map.Entry<String, Mu> en : securityProp.getMemUser().entrySet()) {
            logger.info("Wings conf add MemUser=" + en.getKey());
            final Mu mu = en.getValue();
            Details dtl = new Details();
            dtl.setUserId(mu.getUserId());
            dtl.setAuthType(typeParser.parse(mu.getAuthType()));
            dtl.setUsername(mu.getUsername());
            dtl.setPassword(mu.getPassword());
            dtl.setStatus(mu.getStatus());
            dtl.setNickname(hasText(mu.getNickname()) ? mu.getNickname() : mu.getUsername());
            dtl.setPasssalt(mu.getPasssalt());
            dtl.setLocale(mu.getLocale());
            dtl.setZoneId(mu.getZoneId());
            dtl.setExpiredDt(mu.getExpired());
            bean.addUser(dtl);
        }

        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(AuthZonePermChecker.class)
    @ConditionalOnProperty(name = "spring.wings.warlock.enabled.zone-perm-check", havingValue = "true")
    public AuthZonePermChecker authZonePermChecker() {
        logger.info("Wings conf authZonePermChecker");
        final AuthZonePermChecker bean = new AuthZonePermChecker();
        bean.setZonePerm(securityProp.getZonePerm());
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(AuthAppPermChecker.class)
    @ConditionalOnProperty(name = "spring.wings.warlock.enabled.app-perm-check", havingValue = "true")
    public AuthAppPermChecker authAppPermChecker(@Value("${spring.application.name:wings-default}") String appName) {
        logger.info("Wings conf authAppPermChecker");
        final AuthAppPermChecker bean = new AuthAppPermChecker();
        final Set<String> perms = new HashSet<>();
        final AntPathMatcher matcher = new AntPathMatcher();
        for (Map.Entry<String, Set<String>> en : securityProp.getAppPerm().entrySet()) {
            final String ptn = en.getKey();
            if (matcher.match(ptn, appName)) {
                logger.info("Wings conf authAppPermChecker, " + appName + " matches " + ptn);
                perms.addAll(en.getValue());
            }
        }
        bean.setAppPerm(perms);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(ComboWingsAuthCheckService.class)
    public ComboWingsAuthCheckService comboWingsAuthCheckService(ObjectProvider<ComboWingsAuthCheckService.Combo> combos) {
        logger.info("Wings conf comboWingsAuthCheckService");
        final List<ComboWingsAuthCheckService.Combo> list = combos.orderedStream().collect(Collectors.toList());
        final ComboWingsAuthCheckService bean = new ComboWingsAuthCheckService();
        bean.setCombos(list);
        return bean;
    }

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$comboJustAuthUserDetails, havingValue = "true")
    @ConditionalOnMissingBean(JustAuthUserDetailsCombo.class)
    public JustAuthUserDetailsCombo justAuthUserDetailsCombo() {
        logger.info("Wings conf justAuthUserDetailsCombo");
        return new JustAuthUserDetailsCombo();
    }

    @Bean
    @ConditionalOnMissingBean(UserDetailsService.class)
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
    @ConditionalOnMissingBean(MemoryTypedAuthzCombo.class)
    public MemoryTypedAuthzCombo memoryTypedAuthzCombo(WingsAuthTypeParser typeParser, WarlockPermNormalizer normalizer) {
        logger.info("Wings conf memoryTypedAuthzCombo");
        final MemoryTypedAuthzCombo bean = new MemoryTypedAuthzCombo();
        for (Map.Entry<String, Ma> en : securityProp.getMemAuth().entrySet()) {
            final Ma ma = en.getValue();
            final Set<String> role = ma.getAuthRole()
                                       .stream()
                                       .map(normalizer::role)
                                       .collect(Collectors.toSet());
            final Set<String> perm = ma.getAuthPerm();
            final long uid = ma.getUserId();
            if (uid == Long.MIN_VALUE) {
                logger.info("Wings conf skip MemAuth, userId=" + uid);
            }
            else {
                logger.info("Wings conf add MemAuth, userId=" + uid);
                bean.addAuthz(uid, role);
                bean.addAuthz(uid, perm);
            }

            final String un = ma.getUsername();
            if (hasText(un)) {
                final String tm = ma.getAuthType();
                final Enum<?> at = typeParser.parse(tm);
                logger.info("Wings conf add MemAuth, username=" + un + ", auth-type=" + tm);
                bean.addAuthz(un, at, role);
                bean.addAuthz(un, at, perm);
            }
            else {
                logger.info("Wings conf skip MemAuth, empty username");
            }
        }
        return bean;
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

    ///////// login /////////

    @Bean
    @ConditionalOnMissingBean(AuthStateBuilder.class)
    public AuthStateBuilder authStateBuilder(WarlockJustAuthProp prop) {
        logger.info("Wings conf authStateBuilder");
        return new AuthStateBuilder(prop.getSafeState());
    }

    @Bean
    @ConditionalOnMissingBean(WarlockUserLoginService.class)
    public WarlockUserLoginService warlockUserLoginService() {
        logger.info("Wings conf warlockUserLoginService");
        return new WarlockUserLoginServiceImpl();
    }

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
    @ConditionalOnMissingBean(ListAllLoginPageCombo.class)
    public ListAllLoginPageCombo listAllLoginPageCombo() {
        logger.info("Wings conf listAllLoginPageCombo");
        return new ListAllLoginPageCombo();
    }

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$comboJustAuthLoginPage, havingValue = "true")
    @ConditionalOnMissingBean(JustAuthLoginPageCombo.class)
    public JustAuthLoginPageCombo justAuthLoginPageCombo() {
        logger.info("Wings conf justAuthLoginPageCombo");
        return new JustAuthLoginPageCombo();
    }


    @Bean
    @ConditionalOnMissingBean(GrantedAuthorityDefaults.class)
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        logger.info("Wings conf grantedAuthorityDefaults");
        return new GrantedAuthorityDefaults(securityProp.getRolePrefix());
    }

    ///////// Listener /////////
    @Bean
    public WarlockSuccessLoginListener warlockSuccessLoginListener() {
        logger.info("Wings conf warlockSuccessLoginListener");
        return new WarlockSuccessLoginListener();
    }

    @Bean
    public WarlockFailedLoginListener warlockFailedLoginListener() {
        logger.info("Wings conf warlockFailedLoginListener");
        return new WarlockFailedLoginListener();
    }
}
