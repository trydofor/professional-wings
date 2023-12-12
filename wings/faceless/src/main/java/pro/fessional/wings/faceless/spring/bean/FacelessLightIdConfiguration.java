package pro.fessional.wings.faceless.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
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
import pro.fessional.wings.faceless.spring.prop.LightIdInsertProp;
import pro.fessional.wings.faceless.spring.prop.LightIdLayoutProp;
import pro.fessional.wings.faceless.spring.prop.LightIdProviderProp;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;

/**
 * @author trydofor
 * @since 2019-06-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class FacelessLightIdConfiguration {

    private static final Log log = LogFactory.getLog(FacelessLightIdConfiguration.class);

    @Configuration(proxyBeanMethods = false)
    @ConditionalWingsEnabled
    public static class LayoutWired {
        @Autowired
        public void auto(@NotNull LightIdLayoutProp prop) {
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


    @Bean
    @ConditionalWingsEnabled
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
    @ConditionalWingsEnabled
    public LightSequenceSelect lightSequenceSelect(LightIdProviderProp prop, JdbcTemplate jdbcTemplate) {
        log.info("Faceless spring-bean lightSequenceSelect");
        return new LightSequenceSelectJdbc(
                jdbcTemplate,
                prop.getSequenceGetOne(),
                prop.getSequenceGetAll(),
                prop.getSequenceAdjust());
    }

    @Bean
    @ConditionalWingsEnabled
    public LightSequenceModify lightSequenceModify(LightIdProviderProp providerProp, JdbcTemplate jdbcTemplate) {
        log.info("Faceless spring-bean lightSequenceModify");
        return new LightSequenceModifyJdbc(jdbcTemplate, providerProp.getSequenceInsert(), providerProp.getSequenceUpdate());
    }

    @Bean
    @ConditionalWingsEnabled
    public LightIdProvider.Loader lightIdLoader(LightSequenceSelect lightSequenceSelect,
                                                LightSequenceModify lightSequenceModify,
                                                LightIdInsertProp insertProp) {
        log.info("Faceless spring-bean lightIdLoader");
        return new LightIdMysqlLoader(lightSequenceSelect, lightSequenceModify, insertProp);
    }

    @Bean
    @ConditionalWingsEnabled
    @ConditionalOnProperty(name = LightIdProviderProp.Key$monotonic, havingValue = "jvm")
    public LightIdProvider jvmLightIdProvider(LightIdProvider.Loader loader,
                                              LightIdProviderProp prop,
                                              ObjectProvider<LightIdProvider.Generator> generator) {
        log.info("Faceless spring-bean jvmLightIdProvider");
        // avg=0.039ms
        LightIdBufferedProvider provider = new LightIdBufferedProvider(loader);
        provider.setTimeout(prop.getTimeout());
        provider.setErrAlive(prop.getErrAlive());
        provider.setMaxError(prop.getMaxError());
        provider.setMaxCount(prop.getMaxCount());
        // default LightIdUtil.toId
        generator.ifAvailable(provider::setGenerator);
        return provider;
    }

    @Bean
    @ConditionalWingsEnabled
    @ConditionalOnProperty(name = LightIdProviderProp.Key$monotonic, havingValue = "db")
    public LightIdProvider dbLightIdProvider(LightIdProvider.Loader loader, LightIdProviderProp prop) {
        log.info("Faceless spring-bean dbLightIdProvider");

        // avg=10.723ms
        log.warn("the BlockingLightIdProvider is slow, about 10ms per id");
        final BlockingLightIdProvider provider = new BlockingLightIdProvider(loader);
        provider.setTimeout(prop.getTimeout());
        return provider;
    }

    @Bean
    @ConditionalWingsEnabled
    public LightIdService lightIdService(LightIdProvider lightIdProvider,
                                         BlockIdProvider blockIdProvider) {
        log.info("Faceless spring-bean lightIdService");
        return new LightIdServiceImpl(lightIdProvider, blockIdProvider);
    }

}
