package pro.fessional.wings.warlock.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.spring.help.CommandLineRunnerOrdered;
import pro.fessional.wings.spring.consts.OrderedWarlockConst;
import pro.fessional.wings.warlock.caching.CacheConst;
import pro.fessional.wings.warlock.database.autogen.tables.WinRoleEntryTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserAuthnTable;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserBasisTable;

/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(OrderedWarlockConst.BondAutoRunConfiguration)
public class WarlockBondAutoRunConfiguration {

    private final static Log log = LogFactory.getLog(WarlockBondAutoRunConfiguration.class);

    @Bean    // 静态注入，执行一次即可
    public CommandLineRunnerOrdered runnerRegisterCacheConst() {
        log.info("WarlockBond spring-runs runnerRegisterCacheConst");
        return new CommandLineRunnerOrdered(OrderedWarlockConst.RunnerRegisterCacheConst, ignoredArgs -> {
            CacheConst.WarlockAuthnService.EventTables.add(WinUserBasisTable.WinUserBasis.getName());
            CacheConst.WarlockAuthnService.EventTables.add(WinUserAuthnTable.WinUserAuthn.getName());
            log.info("WarlockBond conf WarlockAuthnService.EventTables");

            CacheConst.WarlockPermService.EventTables.add(WinUserBasisTable.WinUserBasis.getName());
            CacheConst.WarlockPermService.EventTables.add(WinUserAuthnTable.WinUserAuthn.getName());
            log.info("WarlockBond conf WarlockPermService.EventTables");

            CacheConst.WarlockRoleService.EventTables.add(WinRoleEntryTable.WinRoleEntry.getName());
            log.info("WarlockBond conf WinRoleEntryTable.EventTables");
        });
    }
}
