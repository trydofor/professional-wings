package pro.fessional.wings.warlock.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.spring.consts.OrderedWarlockConst;
import pro.fessional.wings.warlock.caching.CacheConst;
import pro.fessional.wings.warlock.database.autogen.tables.WinPermEntryTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinRoleEntryTable;
import pro.fessional.wings.warlock.service.auth.WarlockDangerService;
import pro.fessional.wings.warlock.service.auth.impl.DefaultDaoAuthnCombo;
import pro.fessional.wings.warlock.service.auth.impl.WarlockDangerServiceImpl;
import pro.fessional.wings.warlock.service.grant.WarlockGrantService;
import pro.fessional.wings.warlock.service.grant.impl.WarlockGrantServiceImpl;
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
import pro.fessional.wings.warlock.spring.prop.WarlockDangerProp;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = WarlockEnabledProp.Key$securityBean, havingValue = "true")
@AutoConfigureBefore(WarlockSecurityBeanConfiguration.class)
@AutoConfigureOrder(OrderedWarlockConst.BondBeanConfiguration)
public class WarlockBondBeanConfiguration {

    private final static Log log = LogFactory.getLog(WarlockBondBeanConfiguration.class);

    @Autowired
    public void autoRegisterCacheConst() {
        CacheConst.WarlockPermService.EventTables.add(WinPermEntryTable.WinPermEntry.getName());
        log.info("WarlockBond spring-conf WarlockPermService.EventTables");

        CacheConst.WarlockRoleService.EventTables.add(WinRoleEntryTable.WinRoleEntry.getName());
        log.info("WarlockBond spring-conf WinRoleEntryTable.EventTables");
    }
    ///////// AuthZ & AuthN /////////

    @Bean
    @ConditionalOnMissingBean(WarlockDangerService.class)
    public WarlockDangerService warlockDangerService(WarlockDangerProp warlockDangerProp) {
        log.info("WarlockBond spring-bean warlockDangerService");
        return new WarlockDangerServiceImpl(warlockDangerProp.getCacheSize(), (int) warlockDangerProp.getCacheTtl().toSeconds());
    }

    @Bean
    @ConditionalOnMissingBean(DefaultDaoAuthnCombo.class)
    public DefaultDaoAuthnCombo defaultDaoAuthnCombo() {
        log.info("WarlockBond spring-bean defaultDaoAuthnCombo");
        return new DefaultDaoAuthnCombo();
    }

    @Bean
    @ConditionalOnMissingBean(WarlockGrantService.class)
    public WarlockGrantService warlockGrantService() {
        // not needed if subclass bean exists e.g. JustAuthUserAuthnAutoReg
        log.info("WarlockBond spring-bean warlockGrantService");
        return new WarlockGrantServiceImpl();
    }


    @Bean
    @ConditionalOnMissingBean(WarlockPermServiceImpl.Caching.class)
    public WarlockPermServiceImpl.Caching warlockPermServiceCaching() {
        log.info("WarlockBond spring-bean warlockPermServiceCaching");
        return new WarlockPermServiceImpl.Caching();
    }

    @Bean
    @ConditionalOnMissingBean(WarlockPermService.class)
    public WarlockPermService warlockPermService(WarlockPermServiceImpl.Caching caching) {
        log.info("WarlockBond spring-bean warlockPermService");
        return new WarlockPermServiceImpl(caching);
    }

    @Bean
    @ConditionalOnMissingBean(WarlockRoleServiceImpl.Caching.class)
    public WarlockRoleServiceImpl.Caching warlockRoleServiceCaching() {
        log.info("WarlockBond spring-bean warlockRoleServiceCaching");
        return new WarlockRoleServiceImpl.Caching();
    }

    @Bean
    @ConditionalOnMissingBean(WarlockRoleService.class)
    public WarlockRoleService warlockRoleService(WarlockRoleServiceImpl.Caching caching) {
        log.info("WarlockBond spring-bean warlockRoleService");
        return new WarlockRoleServiceImpl(caching);
    }

    @Bean
    @ConditionalOnMissingBean(WarlockUserAuthnService.class)
    public WarlockUserAuthnService warlockUserAuthnService() {
        log.info("WarlockBond spring-bean warlockUserAuthnService");
        return new WarlockUserAuthnServiceImpl();
    }

    ///////// UserDetails /////////
    @Bean
    @ConditionalOnMissingBean(WarlockUserBasisService.class)
    public WarlockUserBasisService warlockUserBasisService() {
        log.info("WarlockBond spring-bean warlockUserBasisService");
        return new WarlockUserBasisServiceImpl();
    }

    ///////// login /////////

    @Bean
    @ConditionalOnMissingBean(WarlockUserLoginService.class)
    public WarlockUserLoginService warlockUserLoginService() {
        log.info("WarlockBond spring-bean warlockUserLoginService");
        return new WarlockUserLoginServiceImpl();
    }

}
