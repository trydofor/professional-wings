package pro.fessional.wings.faceless.spring.bean;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.mirana.id.LightIdBufferedProvider;
import pro.fessional.mirana.id.LightIdProvider;
import pro.fessional.wings.faceless.database.manual.single.modify.lightsequence.LightSequenceModify;
import pro.fessional.wings.faceless.database.manual.single.modify.lightsequence.impl.LightSequenceModifyJdbc;
import pro.fessional.wings.faceless.database.manual.single.select.lightsequence.LightSequenceSelect;
import pro.fessional.wings.faceless.database.manual.single.select.lightsequence.impl.LightSequenceSelectJdbc;
import pro.fessional.wings.faceless.service.flakeid.FlakeIdService;
import pro.fessional.wings.faceless.service.flakeid.impl.FlakeIdLightIdImpl;
import pro.fessional.wings.faceless.service.lightid.BlockIdProvider;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.faceless.service.lightid.impl.DefaultBlockIdProvider;
import pro.fessional.wings.faceless.service.lightid.impl.LightIdMysqlLoader;
import pro.fessional.wings.faceless.service.lightid.impl.LightIdServiceImpl;
import pro.fessional.wings.faceless.spring.prop.FacelessEnabledProp;
import pro.fessional.wings.faceless.spring.prop.LightIdInsertProp;
import pro.fessional.wings.faceless.spring.prop.LightIdLoaderProp;
import pro.fessional.wings.faceless.spring.prop.LightIdProviderProp;

/**
 * @author trydofor
 * @since 2019-06-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = FacelessEnabledProp.Key$lightid, havingValue = "true")
public class FacelessLightIdConfiguration {

    @Bean
    @ConditionalOnMissingBean(LightSequenceSelect.class)
    public LightSequenceSelect lightSequenceSelect(LightIdProviderProp prop, JdbcTemplate jdbcTemplate) {
        return new LightSequenceSelectJdbc(
                jdbcTemplate,
                prop.getSequenceGetOne(),
                prop.getSequenceGetAll(),
                prop.getSequenceAdjust());
    }

    @Bean
    @ConditionalOnMissingBean(LightSequenceModify.class)
    public LightSequenceModify lightSequenceModify(LightIdProviderProp provider, JdbcTemplate jdbcTemplate) {
        return new LightSequenceModifyJdbc(jdbcTemplate, provider.getSequenceInsert(), provider.getSequenceUpdate());
    }

    @Bean
    @ConditionalOnMissingBean(LightIdProvider.Loader.class)
    public LightIdProvider.Loader lightIdLoader(LightSequenceSelect lightSequenceSelect,
                                                LightSequenceModify lightSequenceModify,
                                                LightIdInsertProp properties) {
        return new LightIdMysqlLoader(lightSequenceSelect, lightSequenceModify, properties);
    }

    @Bean
    @ConditionalOnMissingBean(LightIdProvider.class)
    public LightIdProvider lightIdProvider(LightIdProvider.Loader lightIdLoader,
                                           LightIdLoaderProp properties) {

        LightIdBufferedProvider provider = new LightIdBufferedProvider(lightIdLoader);
        provider.setTimeout(properties.getTimeout());
        provider.setErrAlive(properties.getErrAlive());
        provider.setMaxError(properties.getMaxError());
        provider.setMaxCount(properties.getMaxCount());

        return provider;
    }

    @Bean
    @ConditionalOnMissingBean(BlockIdProvider.class)
    public BlockIdProvider blockProvider(LightIdProviderProp provider,
                                         ObjectProvider<JdbcTemplate> jdbcTemplate) {
        if ("sql".equalsIgnoreCase(provider.getBlockType())) {
            return new DefaultBlockIdProvider(provider.getBlockPara(), jdbcTemplate.getIfAvailable());
        }
        else {
            final int id = Integer.parseInt(provider.getBlockPara());
            return () -> id;
        }
    }

    @Bean
    @ConditionalOnMissingBean(LightIdService.class)
    public LightIdService lightIdService(LightIdProvider lightIdProvider,
                                         BlockIdProvider blockIdProvider) {
        return new LightIdServiceImpl(lightIdProvider, blockIdProvider);
    }

    @Bean
    @ConditionalOnMissingBean(FlakeIdService.class)
    public FlakeIdService flakeIdService(LightIdService lightIdService) {
        return new FlakeIdLightIdImpl(lightIdService);
    }
}
