package pro.fessional.wings.faceless.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.mirana.id.LightIdBufferedProvider;
import pro.fessional.mirana.id.LightIdProvider;
import pro.fessional.mirana.id.LightIdUtil;
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
import pro.fessional.wings.faceless.spring.prop.LightIdLayoutProp;
import pro.fessional.wings.faceless.spring.prop.LightIdLoaderProp;
import pro.fessional.wings.faceless.spring.prop.LightIdProviderProp;

/**
 * @author trydofor
 * @since 2019-06-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = FacelessEnabledProp.Key$lightid, havingValue = "true")
public class FacelessLightIdConfiguration {

    private static final Log log = LogFactory.getLog(FacelessLightIdConfiguration.class);

    @Autowired
    public void forceLightIdLayout(LightIdLayoutProp prop) {
        final Boolean bf = prop.getBlockFirst();
        if (bf != null) {
            log.info("Faceless spring-auto forceLightIdLayout forceBlockFirst=" + bf);
            LightIdUtil.forceBlockFirst(bf);
        }
        final Integer bb = prop.getBlockBits();
        if (bb != null) {
            log.info("Faceless spring-auto forceLightIdLayout forceBlockBit=" + bb);
            LightIdUtil.forceBlockBit(bb);
        }
    }

    @Bean
    @ConditionalOnMissingBean(LightSequenceSelect.class)
    public LightSequenceSelect lightSequenceSelect(LightIdProviderProp prop, JdbcTemplate jdbcTemplate) {
        log.info("Faceless spring-bean lightSequenceSelect");
        return new LightSequenceSelectJdbc(
                jdbcTemplate,
                prop.getSequenceGetOne(),
                prop.getSequenceGetAll(),
                prop.getSequenceAdjust());
    }

    @Bean
    @ConditionalOnMissingBean(LightSequenceModify.class)
    public LightSequenceModify lightSequenceModify(LightIdProviderProp provider, JdbcTemplate jdbcTemplate) {
        log.info("Faceless spring-bean lightSequenceModify");
        return new LightSequenceModifyJdbc(jdbcTemplate, provider.getSequenceInsert(), provider.getSequenceUpdate());
    }

    @Bean
    @ConditionalOnMissingBean(LightIdProvider.Loader.class)
    public LightIdProvider.Loader lightIdLoader(LightSequenceSelect lightSequenceSelect,
                                                LightSequenceModify lightSequenceModify,
                                                LightIdInsertProp properties) {
        log.info("Faceless spring-bean lightIdLoader");
        return new LightIdMysqlLoader(lightSequenceSelect, lightSequenceModify, properties);
    }

    @Bean
    @ConditionalOnMissingBean(LightIdProvider.class)
    public LightIdProvider lightIdProvider(LightIdProvider.Loader lightIdLoader,
                                           LightIdLoaderProp properties,
                                           ObjectProvider<LightIdBufferedProvider.SequenceHandler> sequenceHandler) {
        log.info("Faceless spring-bean lightIdProvider");
        LightIdBufferedProvider provider = new LightIdBufferedProvider(lightIdLoader);
        provider.setTimeout(properties.getTimeout());
        provider.setErrAlive(properties.getErrAlive());
        provider.setMaxError(properties.getMaxError());
        provider.setMaxCount(properties.getMaxCount());
        sequenceHandler.ifAvailable(provider::setSequenceHandler);
        return provider;
    }

    @Bean
    @ConditionalOnMissingBean(BlockIdProvider.class)
    @ConditionalOnExpression("!'${" + LightIdProviderProp.Key$blockType + "}'.equals('biz')")
    public BlockIdProvider blockProvider(LightIdProviderProp provider,
                                         ObjectProvider<JdbcTemplate> jdbcTemplate) {
        final String blockType = provider.getBlockType();
        log.info("Faceless spring-bean lightIdProvider" + blockType);
        if ("sql".equalsIgnoreCase(blockType)) {
            return new DefaultBlockIdProvider(provider.getBlockPara(), jdbcTemplate.getIfAvailable());
        }
        else if ("fix".equalsIgnoreCase(blockType)) {
            final int id = Integer.parseInt(provider.getBlockPara());
            return () -> id;
        }
        else if ("biz".equalsIgnoreCase(blockType)) {
            throw new IllegalArgumentException("should not be here for user biz bean");
        }
        else {
            throw new IllegalArgumentException("unsupported wings.faceless.lightid.provider.block-type=" + blockType);
        }
    }

    @Bean
    @ConditionalOnMissingBean(LightIdService.class)
    public LightIdService lightIdService(LightIdProvider lightIdProvider,
                                         BlockIdProvider blockIdProvider) {
        log.info("Faceless spring-bean lightIdService");
        return new LightIdServiceImpl(lightIdProvider, blockIdProvider);
    }

    @Bean
    @ConditionalOnMissingBean(FlakeIdService.class)
    public FlakeIdService flakeIdService(LightIdService lightIdService) {
        log.info("Faceless spring-bean flakeIdService");
        return new FlakeIdLightIdImpl(lightIdService);
    }
}
