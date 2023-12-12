package pro.fessional.wings.warlock.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.warlock.service.grant.WarlockGrantService;
import pro.fessional.wings.warlock.service.grant.impl.WarlockGrantServiceDummy;
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


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled(abs = WarlockEnabledProp.Key$dummyService, value = false)
public class WarlockSecurityDummyConfiguration {

    private final static Log log = LogFactory.getLog(WarlockSecurityDummyConfiguration.class);

    @Bean
    @ConditionalWingsEnabled
    @ConditionalOnMissingBean(WarlockGrantService.class)
    public WarlockGrantService warlockGrantService() {
        log.info("WarlockShadow spring-bean WarlockGrantServiceDummy");
        return new WarlockGrantServiceDummy();
    }

    @Bean
    @ConditionalWingsEnabled
    @ConditionalOnMissingBean(WarlockPermService.class)
    public WarlockPermService warlockPermService() {
        log.info("WarlockShadow spring-bean WarlockPermServiceDummy");
        return new WarlockPermServiceDummy();
    }

    @Bean
    @ConditionalWingsEnabled
    @ConditionalOnMissingBean(WarlockRoleService.class)
    public WarlockRoleService warlockRoleService() {
        log.info("WarlockShadow spring-bean WarlockRoleServiceDummy");
        return new WarlockRoleServiceDummy();
    }

    @Bean
    @ConditionalWingsEnabled
    @ConditionalOnMissingBean(WarlockUserAuthnService.class)
    public WarlockUserAuthnService warlockUserAuthnService() {
        log.info("WarlockShadow spring-bean WarlockUserAuthnServiceDummy");
        return new WarlockUserAuthnServiceDummy();
    }

    @Bean
    @ConditionalWingsEnabled
    @ConditionalOnMissingBean(WarlockUserBasisService.class)
    public WarlockUserBasisService warlockUserBasisService() {
        log.info("WarlockShadow spring-bean warlockUserBasisService");
        return new WarlockUserBasisServiceDummy();
    }

    @Bean
    @ConditionalWingsEnabled
    @ConditionalOnMissingBean(WarlockUserLoginService.class)
    public WarlockUserLoginService warlockUserLoginService() {
        log.info("WarlockShadow spring-bean WarlockUserLoginServiceDummy");
        return new WarlockUserLoginServiceDummy();
    }
}
