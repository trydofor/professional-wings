package pro.fessional.wings.warlock.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
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
import pro.fessional.mirana.bits.Aes128;
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
import pro.fessional.wings.warlock.constants.WarlockOrderConst;
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
import pro.fessional.wings.warlock.service.grant.impl.WarlockGrantServiceDummy;
import pro.fessional.wings.warlock.service.other.TerminalJournalService;
import pro.fessional.wings.warlock.service.perm.WarlockPermNormalizer;
import pro.fessional.wings.warlock.service.perm.WarlockPermService;
import pro.fessional.wings.warlock.service.perm.WarlockRoleService;
import pro.fessional.wings.warlock.service.perm.impl.WarlockPermServiceDummy;
import pro.fessional.wings.warlock.service.perm.impl.WarlockRoleServiceDummy;
import pro.fessional.wings.warlock.service.user.WarlockUserAuthnService;
import pro.fessional.wings.warlock.service.user.WarlockUserBasisService;
import pro.fessional.wings.warlock.service.user.WarlockUserLoginService;
import pro.fessional.wings.warlock.service.user.impl.WarlockUserAuthnServiceDummy;
import pro.fessional.wings.warlock.service.user.impl.WarlockUserBasisServiceDummy;
import pro.fessional.wings.warlock.service.user.impl.WarlockUserLoginServiceDummy;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockJustAuthProp;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp.Ma;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp.Mu;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;
import static pro.fessional.wings.silencer.spring.help.CommonPropHelper.validValue;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = WarlockEnabledProp.Key$securityBean, havingValue = "true")
@RequiredArgsConstructor
public class WarlockSecurityBeanConfiguration {

    private final static Log log = LogFactory.getLog(WarlockSecurityBeanConfiguration.class);

    private final WarlockSecurityProp securityProp;
    private final ApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean(WingsAuthTypeParser.class)
    public WingsAuthTypeParser wingsAuthTypeParser() {
        log.info("Wings conf wingsAuthTypeParser");
        final Map<String, Enum<?>> authType = securityProp.mapAuthTypeEnum();
        final Enum<?> atd = securityProp.mapAuthTypeDefault();
        return new DefaultWingsAuthTypeParser(atd, authType);
    }

    @Bean
    @ConditionalOnProperty(name = SlardarEnabledProp.Key$terminal, havingValue = "true")
    public TerminalJournalService terminalJournalService(
            @SuppressWarnings("all") LightIdService lightIdService,
            @SuppressWarnings("all") BlockIdProvider blockIdProvider,
            @SuppressWarnings("all") CommitJournalModify journalModify
    ) {
        log.info("Wings conf terminalJournalService");
        return new TerminalJournalService(lightIdService, blockIdProvider, journalModify);
    }

    ///////// handler /////////
    @Bean
    @ConditionalOnMissingBean(AuthenticationSuccessHandler.class)
    @ConditionalOnExpression("!'${" + WarlockSecurityProp.Key$loginSuccessBody + "}'.isEmpty()")
    public AuthenticationSuccessHandler loginSuccessHandler() {
        log.info("Wings conf loginSuccessHandler");
        return new LoginSuccessHandler();
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationFailureHandler.class)
    @ConditionalOnExpression("!'${" + WarlockSecurityProp.Key$loginFailureBody + "}'.isEmpty()")
    public AuthenticationFailureHandler loginFailureHandler() {
        log.info("Wings conf loginFailureHandler");
        return new LoginFailureHandler();
    }

    @Bean
    @ConditionalOnMissingBean(LogoutSuccessHandler.class)
    @ConditionalOnExpression("!'${" + WarlockSecurityProp.Key$logoutSuccessBody + "}'.isEmpty()")
    public LogoutSuccessHandler logoutSuccessHandler() {
        log.info("Wings conf logoutSuccessHandler");
        return new LogoutOkHandler();
    }

    ///////// AuthZ & AuthN /////////

    @Bean
    @ConditionalOnMissingBean(WarlockPermNormalizer.class)
    public WarlockPermNormalizer warlockPermNormalizer(@SuppressWarnings("all") GrantedAuthorityDefaults gad) {
        log.info("Wings conf warlockPermNormalizer");
        final WarlockPermNormalizer bean = new WarlockPermNormalizer();
        bean.setRolePrefix(gad.getRolePrefix());
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(ComboWarlockAuthnService.class)
    public ComboWarlockAuthnService comboWarlockAuthnService() {
        log.info("Wings conf comboWarlockAuthnService");
        return new ComboWarlockAuthnService();
    }

    @Bean
    @ConditionalOnMissingBean(ComboWarlockAuthzService.class)
    public ComboWarlockAuthzService comboWarlockAuthzService() {
        log.info("Wings conf comboWarlockAuthzService");
        final ComboWarlockAuthzService bean = new ComboWarlockAuthzService();
        bean.setAuthorityRole(securityProp.isAuthorityRole());
        bean.setAuthorityPerm(securityProp.isAuthorityPerm());
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(DefaultPermRoleCombo.class)
    public DefaultPermRoleCombo defaultPermRoleCombo() {
        log.info("Wings conf defaultPermRoleCombo");
        return new DefaultPermRoleCombo();
    }

    @Bean
    @ConditionalOnMissingBean(DefaultUserDetailsCombo.class)
    public DefaultUserDetailsCombo defaultUserDetailsCombo() {
        log.info("Wings conf defaultUserDetailsCombo");
        final DefaultUserDetailsCombo bean = new DefaultUserDetailsCombo();
        bean.setAutoRegisterType(securityProp.mapAutoregAuthEnum());
        return bean;
    }

    @Bean
    @ConditionalOnExpression("${" + WarlockEnabledProp.Key$justAuth + ":false} "
                             + " && ${" + WarlockEnabledProp.Key$comboJustAuthAutoreg + ":false}")
    @ConditionalOnMissingBean(JustAuthUserAuthnAutoReg.class)
    public JustAuthUserAuthnAutoReg justAuthUserAuthnAutoReg() {
        log.info("Wings conf justAuthUserAuthnAutoReg");
        final JustAuthUserAuthnAutoReg bean = new JustAuthUserAuthnAutoReg();
        bean.setOrder(WarlockOrderConst.JustAuthUserAuthnAutoReg);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(DefaultUserAuthnAutoReg.class)
    public DefaultUserAuthnAutoReg defaultUserAuthnAutoReg() {
        // 存在子类，则不需要此bean，如JustAuthUserAuthnAutoReg
        log.info("Wings conf defaultUserAuthnAutoReg");
        return new DefaultUserAuthnAutoReg();
    }

    @Bean
    @ConditionalOnMissingBean(WarlockGrantService.class)
    public WarlockGrantService warlockGrantService() {
        // 存在子类，则不需要此bean，如JustAuthUserAuthnAutoReg
        log.info("Wings conf WarlockGrantServiceDummy");
        return new WarlockGrantServiceDummy();
    }

    @Bean
    @ConditionalOnMissingBean(WarlockPermService.class)
    public WarlockPermService warlockPermService() {
        log.info("Wings conf WarlockPermServiceDummy");
        return new WarlockPermServiceDummy();
    }

    @Bean
    @ConditionalOnMissingBean(WarlockRoleService.class)
    public WarlockRoleService warlockRoleService() {
        log.info("Wings conf WarlockRoleServiceDummy");
        return new WarlockRoleServiceDummy();
    }

    @Bean
    @ConditionalOnMissingBean(WarlockUserAuthnService.class)
    public WarlockUserAuthnService warlockUserAuthnService() {
        log.info("Wings conf WarlockUserAuthnServiceDummy");
        return new WarlockUserAuthnServiceDummy();
    }

    ///////// UserDetails /////////

    @Bean
    @ConditionalOnMissingBean(WarlockUserBasisService.class)
    public WarlockUserBasisService warlockUserBasisService() {
        log.info("Wings conf warlockUserBasisService");
        return new WarlockUserBasisServiceDummy();
    }

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$comboNonceUserDetails, havingValue = "true")
    @ConditionalOnMissingBean(NonceUserDetailsCombo.class)
    public NonceUserDetailsCombo nonceUserDetailsCombo() {
        log.info("Wings conf nonceUserDetailsCombo");
        final NonceUserDetailsCombo bean = new NonceUserDetailsCombo();
        bean.setOrder(WarlockOrderConst.NonceUserDetailsCombo);
        bean.setAcceptNonceType(securityProp.mapNonceAuthEnum());
        final String cn = WingsCache.Level.join(securityProp.getNonceCacheLevel(), "NonceUserDetailsCombo");
        bean.setCacheName(cn);
        final CacheManager cm = applicationContext.getBean(securityProp.getNonceCacheManager(), CacheManager.class);
        bean.setCacheManager(cm);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(MemoryUserDetailsCombo.class)
    public MemoryUserDetailsCombo memoryUserDetailsCombo(@SuppressWarnings("all") WingsAuthTypeParser typeParser) {
        log.info("Wings conf memoryUserDetailsCombo");
        final MemoryUserDetailsCombo bean = new MemoryUserDetailsCombo();
        bean.setOrder(WarlockOrderConst.MemoryUserDetailsCombo);
        for (Map.Entry<String, Mu> en : securityProp.getMemUser().entrySet()) {
            log.info("Wings conf add MemUser=" + en.getKey());
            final Mu mu = en.getValue();
            Set<String> ats = mu.getAuthType();
            if (ats == null || ats.isEmpty()) {
                ats = Collections.singleton("");
            }
            for (String at : ats) {
                Details dtl = new Details();
                dtl.setUserId(mu.getUserId());
                dtl.setAuthType(typeParser.parse(at));
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
        }

        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(AuthZonePermChecker.class)
    @ConditionalOnProperty(name = "spring.wings.warlock.enabled.zone-perm-check", havingValue = "true")
    public AuthZonePermChecker authZonePermChecker() {
        log.info("Wings conf authZonePermChecker");
        final AuthZonePermChecker bean = new AuthZonePermChecker();
        bean.setZonePerm(securityProp.getZonePerm());
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(AuthAppPermChecker.class)
    @ConditionalOnProperty(name = "spring.wings.warlock.enabled.app-perm-check", havingValue = "true")
    public AuthAppPermChecker authAppPermChecker(@Value("${spring.application.name:wings-default}") String appName) {
        log.info("Wings conf authAppPermChecker");
        final AuthAppPermChecker bean = new AuthAppPermChecker();
        final Set<String> perms = new HashSet<>();
        final AntPathMatcher matcher = new AntPathMatcher();
        for (Map.Entry<String, Set<String>> en : securityProp.getAppPerm().entrySet()) {
            final String ptn = en.getKey();
            if (matcher.match(ptn, appName)) {
                log.info("Wings conf authAppPermChecker, " + appName + " matches " + ptn);
                perms.addAll(en.getValue());
            }
        }
        bean.setAppPerm(perms);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(ComboWingsAuthCheckService.class)
    public ComboWingsAuthCheckService comboWingsAuthCheckService(ObjectProvider<ComboWingsAuthCheckService.Combo> combos) {
        log.info("Wings conf comboWingsAuthCheckService");
        final List<ComboWingsAuthCheckService.Combo> list = combos.orderedStream().collect(Collectors.toList());
        final ComboWingsAuthCheckService bean = new ComboWingsAuthCheckService();
        bean.setCombos(list);
        return bean;
    }

    @Bean
    @ConditionalOnExpression("${" + WarlockEnabledProp.Key$justAuth + ":false} "
                             + " && ${" + WarlockEnabledProp.Key$comboJustAuthUserDetails + ":false}")
    @ConditionalOnMissingBean(JustAuthUserDetailsCombo.class)
    public JustAuthUserDetailsCombo justAuthUserDetailsCombo() {
        log.info("Wings conf justAuthUserDetailsCombo");
        final JustAuthUserDetailsCombo bean = new JustAuthUserDetailsCombo();
        bean.setOrder(WarlockOrderConst.JustAuthUserDetailsCombo);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(UserDetailsService.class)
    public WingsUserDetailsService wingsUserDetailsService(ObjectProvider<ComboWingsUserDetailsService.Combo<?>> combos) {
        log.info("Wings conf wingsUserDetailsService");
        ComboWingsUserDetailsService uds = new ComboWingsUserDetailsService();
        combos.orderedStream().forEach(it -> {
            log.info("Wings conf wingsUserDetailsService add " + it.getClass().getName());
            uds.add(it);
        });
        return uds;
    }

    @Bean
    @ConditionalOnMissingBean(MemoryTypedAuthzCombo.class)
    public MemoryTypedAuthzCombo memoryTypedAuthzCombo(
            @SuppressWarnings("all") WingsAuthTypeParser typeParser,
            @SuppressWarnings("all") WarlockPermNormalizer normalizer) {
        log.info("Wings conf memoryTypedAuthzCombo");
        final MemoryTypedAuthzCombo bean = new MemoryTypedAuthzCombo();
        for (Map.Entry<String, Ma> en : securityProp.getMemAuth().entrySet()) {
            final Ma ma = en.getValue();
            final Set<String> role = ma.getAuthRole()
                                       .stream()
                                       .map(normalizer::role)
                                       .collect(Collectors.toSet());
            final Set<String> perm = ma.getAuthPerm();
            final long uid = ma.getUserId();
            log.info("Wings conf add MemAuth, userId=" + uid);
            bean.addAuthz(uid, role);
            bean.addAuthz(uid, perm);
            if (uid < 0) {
                log.warn("should NOT use negative UserId");
            }

            final String un = ma.getUsername();
            if (hasText(un)) {
                final String tm = ma.getAuthType();
                final Enum<?> at = typeParser.parse(tm);
                log.info("Wings conf add MemAuth, username=" + un + ", auth-type=" + tm);
                bean.addAuthz(un, at, role);
                bean.addAuthz(un, at, perm);
            }
            else {
                log.info("Wings conf skip MemAuth, empty username");
            }
        }
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(WingsAuthDetailsSource.class)
    public WingsAuthDetailsSource<?> wingsAuthDetailsSource(ObjectProvider<ComboWingsAuthDetailsSource.Combo<?>> combos) {
        log.info("Wings conf wingsAuthDetailsSource");
        ComboWingsAuthDetailsSource uds = new ComboWingsAuthDetailsSource();

        combos.orderedStream().forEach(it -> {
            log.info("Wings conf wingsAuthDetailsSource add " + it.getClass().getName());
            uds.add(it);
        });

        final Set<String> set = new HashSet<>();
        set.add(securityProp.getPasswordPara());
        uds.setIgnoredMetaKey(set);
        return uds;
    }

    ///////// login /////////

    @Bean
    @ConditionalOnMissingBean(AuthStateBuilder.class)
    public AuthStateBuilder authStateBuilder(WarlockJustAuthProp prop, ObjectProvider<Aes128> aes128Provider) {
        final AuthStateBuilder bean = new AuthStateBuilder(validValue(prop.getSafeState()));
        final Aes128 aes128 = aes128Provider.getIfAvailable();
        if (aes128 != null) {
            bean.setAes128(aes128);
            log.info("Wings conf authStateBuilder with Global Aes128 Bean");
        }
        else {
            log.info("Wings conf authStateBuilder with Random Aes128 Bean");
        }
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(WarlockUserLoginService.class)
    public WarlockUserLoginService warlockUserLoginService() {
        log.info("Wings conf WarlockUserLoginServiceDummy");
        return new WarlockUserLoginServiceDummy();
    }

    @Bean
    @ConditionalOnMissingBean(WingsAuthPageHandler.class)
    public WingsAuthPageHandler wingsAuthPageHandler(ObjectProvider<ComboWingsAuthPageHandler.Combo> combos) {
        log.info("Wings conf wingsAuthPageHandler");
        ComboWingsAuthPageHandler uds = new ComboWingsAuthPageHandler();
        combos.orderedStream().forEach(it -> {
            log.info("Wings conf wingsAuthPageHandler add " + it.getClass().getName());
            uds.add(it);
        });
        return uds;
    }

    @Bean
    @ConditionalOnProperty(name = WarlockEnabledProp.Key$comboListAllLoginPage, havingValue = "true")
    @ConditionalOnMissingBean(ListAllLoginPageCombo.class)
    public ListAllLoginPageCombo listAllLoginPageCombo() {
        log.info("Wings conf listAllLoginPageCombo");
        return new ListAllLoginPageCombo();
    }

    @Bean
    @ConditionalOnExpression("${" + WarlockEnabledProp.Key$justAuth + ":false} "
                             + " && ${" + WarlockEnabledProp.Key$comboJustAuthLoginPage + ":false}")
    @ConditionalOnMissingBean(JustAuthLoginPageCombo.class)
    public JustAuthLoginPageCombo justAuthLoginPageCombo() {
        log.info("Wings conf justAuthLoginPageCombo");
        return new JustAuthLoginPageCombo();
    }

    @Bean
    @ConditionalOnMissingBean(GrantedAuthorityDefaults.class)
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        log.info("Wings conf grantedAuthorityDefaults");
        return new GrantedAuthorityDefaults(securityProp.getRolePrefix());
    }

    ///////// Listener /////////
    @Bean
    public WarlockSuccessLoginListener warlockSuccessLoginListener() {
        log.info("Wings conf warlockSuccessLoginListener");
        return new WarlockSuccessLoginListener();
    }

    @Bean
    public WarlockFailedLoginListener warlockFailedLoginListener() {
        log.info("Wings conf warlockFailedLoginListener");
        return new WarlockFailedLoginListener();
    }
}
