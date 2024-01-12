package pro.fessional.wings.warlock.spring.bean;

import jakarta.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.warlock.caching.CacheConst;
import pro.fessional.wings.warlock.database.autogen.tables.WinPermEntryTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinRoleEntryTable;
import pro.fessional.wings.warlock.service.auth.impl.DefaultDaoAuthnCombo;
import pro.fessional.wings.warlock.service.auth.impl.WarlockDangerServiceImpl;
import pro.fessional.wings.warlock.service.grant.impl.WarlockGrantServiceImpl;
import pro.fessional.wings.warlock.service.perm.impl.WarlockPermServiceImpl;
import pro.fessional.wings.warlock.service.perm.impl.WarlockRoleServiceImpl;
import pro.fessional.wings.warlock.service.user.impl.WarlockUserAuthnServiceImpl;
import pro.fessional.wings.warlock.service.user.impl.WarlockUserBasisServiceImpl;
import pro.fessional.wings.warlock.service.user.impl.WarlockUserLoginServiceImpl;
import pro.fessional.wings.warlock.spring.prop.WarlockDangerProp;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class WarlockBondBeanConfiguration {

    private final static Log log = LogFactory.getLog(WarlockBondBeanConfiguration.class);

    @PostConstruct
    public void autoRegisterCacheConst() {
        CacheConst.WarlockPermService.EventTables.add(WinPermEntryTable.WinPermEntry.getName());
        log.info("WarlockBond spring-conf WarlockPermService.EventTables");

        CacheConst.WarlockRoleService.EventTables.add(WinRoleEntryTable.WinRoleEntry.getName());
        log.info("WarlockBond spring-conf WinRoleEntryTable.EventTables");
    }
    ///////// AuthZ & AuthN /////////

    @Bean
    @ConditionalWingsEnabled
    public WarlockDangerServiceImpl warlockDangerService(WarlockDangerProp prop) {
        log.info("WarlockBond spring-bean warlockDangerService");
        return new WarlockDangerServiceImpl(prop.getCacheSize(), (int) prop.getCacheTtl().toSeconds());
    }

    @Bean
    @ConditionalWingsEnabled
    public DefaultDaoAuthnCombo defaultDaoAuthnCombo() {
        log.info("WarlockBond spring-bean defaultDaoAuthnCombo");
        return new DefaultDaoAuthnCombo();
    }

    @Bean
    @ConditionalWingsEnabled
    public WarlockGrantServiceImpl warlockGrantService() {
        // not needed if subclass bean exists e.g. JustAuthUserAuthnAutoReg
        log.info("WarlockBond spring-bean warlockGrantService");
        return new WarlockGrantServiceImpl();
    }


    @Bean
    @ConditionalWingsEnabled
    public WarlockPermServiceImpl.Caching warlockPermServiceCaching() {
        log.info("WarlockBond spring-bean warlockPermServiceCaching");
        return new WarlockPermServiceImpl.Caching();
    }

    @Bean
    @ConditionalWingsEnabled
    public WarlockPermServiceImpl warlockPermService(WarlockPermServiceImpl.Caching caching) {
        log.info("WarlockBond spring-bean warlockPermService");
        return new WarlockPermServiceImpl(caching);
    }

    @Bean
    @ConditionalWingsEnabled
    public WarlockRoleServiceImpl.Caching warlockRoleServiceCaching() {
        log.info("WarlockBond spring-bean warlockRoleServiceCaching");
        return new WarlockRoleServiceImpl.Caching();
    }

    @Bean
    @ConditionalWingsEnabled
    public WarlockRoleServiceImpl warlockRoleService(WarlockRoleServiceImpl.Caching caching) {
        log.info("WarlockBond spring-bean warlockRoleService");
        return new WarlockRoleServiceImpl(caching);
    }

    @Bean
    @ConditionalWingsEnabled
    public WarlockUserAuthnServiceImpl warlockUserAuthnService() {
        log.info("WarlockBond spring-bean warlockUserAuthnService");
        return new WarlockUserAuthnServiceImpl();
    }

    ///////// UserDetails /////////
    @Bean
    @ConditionalWingsEnabled
    public WarlockUserBasisServiceImpl warlockUserBasisService() {
        log.info("WarlockBond spring-bean warlockUserBasisService");
        return new WarlockUserBasisServiceImpl();
    }

    ///////// login /////////

    @Bean
    @ConditionalWingsEnabled
    public WarlockUserLoginServiceImpl warlockUserLoginService() {
        log.info("WarlockBond spring-bean warlockUserLoginService");
        return new WarlockUserLoginServiceImpl();
    }

}
