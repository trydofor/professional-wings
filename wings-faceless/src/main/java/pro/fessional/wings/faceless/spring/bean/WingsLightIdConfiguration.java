package pro.fessional.wings.faceless.spring.bean;

import lombok.Data;
import org.springframework.beans.factory.ObjectProvider;
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
@ConditionalOnProperty(name = "spring.wings.faceless.lightid.enabled", havingValue = "true")
public class WingsLightIdConfiguration {

    @Bean
    @ConditionalOnMissingBean(LightSequenceSelect.class)
    public LightSequenceSelect lightSequenceSelect(LightIdProviderProperties provider, JdbcTemplate jdbcTemplate) {
        return new LightSequenceSelectJdbc(jdbcTemplate, provider.sequenceGetOne, provider.sequenceGetAll);
    }

    @Bean
    @ConditionalOnMissingBean(LightSequenceModify.class)
    public LightSequenceModify lightSequenceModify(LightIdProviderProperties provider, JdbcTemplate jdbcTemplate) {
        return new LightSequenceModifyJdbc(jdbcTemplate, provider.sequenceInsert, provider.sequenceUpdate);
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
    public BlockIdProvider blockProvider(LightIdProviderProperties provider,
                                         ObjectProvider<JdbcTemplate> jdbcTemplate) {
        if ("sql".equalsIgnoreCase(provider.blockType)) {
            return new DefaultBlockIdProvider(provider.blockPara, jdbcTemplate.getIfAvailable());
        } else {
            final int id = Integer.parseInt(provider.blockPara);
            return () -> id;
        }
    }

    @Bean
    @ConfigurationProperties("wings.faceless.lightid.insert")
    public WingsLightIdInsertProperties insertProperties() {
        return new WingsLightIdInsertProperties();
    }

    @Bean
    @ConfigurationProperties("wings.faceless.lightid.loader")
    public WingsLightIdLoaderProperties loaderProperties() {
        return new WingsLightIdLoaderProperties();
    }

    @Bean
    @ConfigurationProperties("wings.faceless.lightid.provider")
    public LightIdProviderProperties lightIdProviderProperties() {
        return new LightIdProviderProperties();
    }

    @Data
    public static class LightIdProviderProperties {
        private String blockType;
        private String blockPara;
        private String sequenceInsert;
        private String sequenceUpdate;
        private String sequenceGetOne;
        private String sequenceGetAll;
    }
}
