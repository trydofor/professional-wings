package pro.fessional.wings.faceless.spring.bean;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.faceless.database.autogen.tables.daos.SysConstantEnumDao;
import pro.fessional.wings.faceless.database.autogen.tables.daos.SysStandardI18nDao;
import pro.fessional.wings.faceless.service.wini18n.StandardI18nService;
import pro.fessional.wings.faceless.service.wini18n.impl.StandardI18nServiceImpl;
import pro.fessional.wings.silencer.message.CombinableMessageSource;

/**
 * @author trydofor
 * @since 2019-06-01
 */
@Configuration
@ConditionalOnProperty(name = "spring.wings.enumi18n.enabled", havingValue = "true")
public class WingsEnumI18nConfiguration {

    @Bean
    @ConditionalOnMissingBean(StandardI18nService.class)
    public StandardI18nService standardI18nService(
            SysStandardI18nDao sysStandardI18nDao,
            ObjectProvider<CombinableMessageSource> combinableMessageSource
    ) {
        return new StandardI18nServiceImpl(sysStandardI18nDao, combinableMessageSource.getIfAvailable());
    }

    @Bean
    @ConditionalOnMissingBean(SysConstantEnumDao.class)
    public SysConstantEnumDao sysConstantEnumDao(org.jooq.Configuration configuration) {
        return new SysConstantEnumDao(configuration);
    }


    @Bean
    @ConditionalOnMissingBean(SysStandardI18nDao.class)
    public SysStandardI18nDao sysStandardI18nDao(org.jooq.Configuration configuration) {
        return new SysStandardI18nDao(configuration);
    }
}
