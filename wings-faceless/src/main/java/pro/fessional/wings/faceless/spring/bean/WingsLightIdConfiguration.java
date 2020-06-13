package pro.fessional.wings.faceless.spring.bean;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.mirana.id.LightIdBufferedProvider;
import pro.fessional.mirana.id.LightIdProvider;
import pro.fessional.wings.faceless.database.manual.single.modify.lightsequence.LightSequenceModify;
import pro.fessional.wings.faceless.database.manual.single.modify.lightsequence.impl.LightSequenceModifyJdbc;
import pro.fessional.wings.faceless.database.manual.single.select.lightsequence.LightSequenceSelect;
import pro.fessional.wings.faceless.database.manual.single.select.lightsequence.impl.LightSequenceSelectJdbc;
import pro.fessional.wings.faceless.service.lightid.BlockIdProvider;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.faceless.service.lightid.impl.DefaultBlockIdProvider;
import pro.fessional.wings.faceless.service.lightid.impl.LightIdMysqlLoader;
import pro.fessional.wings.faceless.service.lightid.impl.LightIdServiceImpl;
import pro.fessional.wings.faceless.spring.conf.WingsLightIdInsertProperties;
import pro.fessional.wings.faceless.spring.conf.WingsLightIdLoaderProperties;

/**
 * @author trydofor
 * @since 2019-06-01
 */
@Configuration
@ConditionalOnProperty(name = "spring.wings.lightid.enabled", havingValue = "true")
public class WingsLightIdConfiguration {

    @Bean
    @ConditionalOnMissingBean(LightSequenceSelect.class)
    public LightSequenceSelect lightSequenceSelect(JdbcTemplate jdbcTemplate) {
        return new LightSequenceSelectJdbc(jdbcTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(LightSequenceModify.class)
    public LightSequenceModify lightSequenceModify(JdbcTemplate jdbcTemplate) {
        return new LightSequenceModifyJdbc(jdbcTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(LightIdProvider.Loader.class)
    public LightIdProvider.Loader lightIdLoader(LightSequenceSelect lightSequenceSelect,
                                                LightSequenceModify lightSequenceModify,
                                                WingsLightIdInsertProperties properties) {
        return new LightIdMysqlLoader(lightSequenceSelect, lightSequenceModify, properties);
    }

    @Bean
    @ConditionalOnMissingBean(LightIdProvider.class)
    public LightIdProvider lightIdProvider(LightIdProvider.Loader lightIdLoader,
                                           WingsLightIdLoaderProperties properties) {

        LightIdBufferedProvider provider = new LightIdBufferedProvider(lightIdLoader);
        provider.setTimeout(properties.getTimeout());
        provider.setErrAlive(properties.getErrAlive());
        provider.setMaxError(properties.getMaxError());
        provider.setMaxCount(properties.getMaxCount());

        return provider;
    }

    @Bean
    @ConditionalOnMissingBean(LightIdService.class)
    public LightIdService lightIdService(LightIdProvider lightIdProvider,
                                         BlockIdProvider blockIdProvider) {
        return new LightIdServiceImpl(lightIdProvider, blockIdProvider);
    }

    @Bean
    @ConditionalOnMissingBean(BlockIdProvider.class)
    @ConditionalOnProperty(name = "wings.lightid.block.provider.type", havingValue = "sql")
    public BlockIdProvider blockProvider(@Value("${wings.lightid.block.provider.type}") String type,
                                         @Value("${wings.lightid.block.provider.para}") String sqlOrId,
                                         ObjectProvider<JdbcTemplate> jdbcTemplate) {
        if (type.equalsIgnoreCase("sql")) {
            return new DefaultBlockIdProvider(sqlOrId, jdbcTemplate.getIfAvailable());
        } else {
            final int id = Integer.parseInt(sqlOrId);
            return () -> id;
        }
    }

    @Bean
    @ConfigurationProperties("wings.lightid.insert")
    public WingsLightIdInsertProperties insertProperties() {
        return new WingsLightIdInsertProperties();
    }

    @Bean
    @ConfigurationProperties("wings.lightid.loader")
    public WingsLightIdLoaderProperties loaderProperties() {
        return new WingsLightIdLoaderProperties();
    }
}
