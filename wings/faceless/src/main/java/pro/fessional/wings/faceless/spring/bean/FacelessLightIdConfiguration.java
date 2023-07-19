package pro.fessional.wings.faceless.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
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
import pro.fessional.wings.faceless.service.lightid.BlockIdProvider;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.faceless.service.lightid.impl.BlockingLightIdProvider;
import pro.fessional.wings.faceless.service.lightid.impl.DefaultBlockIdProvider;
import pro.fessional.wings.faceless.service.lightid.impl.LightIdMysqlLoader;
import pro.fessional.wings.faceless.service.lightid.impl.LightIdServiceImpl;
import pro.fessional.wings.faceless.spring.prop.FacelessEnabledProp;
import pro.fessional.wings.faceless.spring.prop.LightIdInsertProp;
import pro.fessional.wings.faceless.spring.prop.LightIdLayoutProp;
import pro.fessional.wings.faceless.spring.prop.LightIdProviderProp;
import pro.fessional.wings.spring.consts.OrderedFacelessConst;

/**
 * @author trydofor
 * @since 2019-06-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = FacelessEnabledProp.Key$lightid, havingValue = "true")
@AutoConfigureOrder(OrderedFacelessConst.LightIdConfiguration)
public class FacelessLightIdConfiguration {

    private static final Log log = LogFactory.getLog(FacelessLightIdConfiguration.class);

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
    public LightSequenceModify lightSequenceModify(LightIdProviderProp providerProp, JdbcTemplate jdbcTemplate) {
        log.info("Faceless spring-bean lightSequenceModify");
        return new LightSequenceModifyJdbc(jdbcTemplate, providerProp.getSequenceInsert(), providerProp.getSequenceUpdate());
    }

    @Bean
    @ConditionalOnMissingBean(LightIdProvider.Loader.class)
    public LightIdProvider.Loader lightIdLoader(LightSequenceSelect lightSequenceSelect,
                                                LightSequenceModify lightSequenceModify,
                                                LightIdInsertProp insertProp) {
        log.info("Faceless spring-bean lightIdLoader");
        return new LightIdMysqlLoader(lightSequenceSelect, lightSequenceModify, insertProp);
    }

    @Bean
    @ConditionalOnMissingBean(LightIdProvider.class)
    public LightIdProvider lightIdProvider(LightIdProvider.Loader lightIdLoader,
                                           LightIdProviderProp providerProp,
                                           ObjectProvider<LightIdBufferedProvider.SequenceHandler> sequenceHandler) {
        final String mono = providerProp.getMonotonic();
        log.info("Faceless spring-bean lightIdProvider in " + mono);
        if ("jvm".equalsIgnoreCase(mono)) {
            // avg=0.039ms
            LightIdBufferedProvider provider = new LightIdBufferedProvider(lightIdLoader);
            provider.setTimeout(providerProp.getTimeout());
            provider.setErrAlive(providerProp.getErrAlive());
            provider.setMaxError(providerProp.getMaxError());
            provider.setMaxCount(providerProp.getMaxCount());
            sequenceHandler.ifAvailable(provider::setSequenceHandler);
            return provider;
        }
        else if ("db".equalsIgnoreCase(mono)) {
            // avg=10.723ms
            log.warn("the BlockingLightIdProvider is slow, about 10ms per id");
            final BlockingLightIdProvider provider = new BlockingLightIdProvider(lightIdLoader);
            provider.setTimeout(providerProp.getTimeout());
            return provider;
        }
        else {
            throw new IllegalArgumentException("unsupported monotonic type=" + mono);
        }
    }

    @Bean
    @ConditionalOnMissingBean(BlockIdProvider.class)
    @ConditionalOnExpression("!'${" + LightIdProviderProp.Key$blockType + "}'.equals('biz')")
    public BlockIdProvider blockProvider(LightIdProviderProp providerProp,
                                         ObjectProvider<JdbcTemplate> jdbcTemplate) {
        final String blockType = providerProp.getBlockType();
        log.info("Faceless spring-bean lightIdProvider" + blockType);
        if ("sql".equalsIgnoreCase(blockType)) {
            return new DefaultBlockIdProvider(providerProp.getBlockPara(), jdbcTemplate.getIfAvailable());
        }
        else if ("fix".equalsIgnoreCase(blockType)) {
            final int id = Integer.parseInt(providerProp.getBlockPara());
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

    @Autowired
    public void autowireLightIdLayout(@NotNull LightIdLayoutProp prop) {
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
}
