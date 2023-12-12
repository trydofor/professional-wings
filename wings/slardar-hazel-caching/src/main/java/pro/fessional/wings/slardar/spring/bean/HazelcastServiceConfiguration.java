package pro.fessional.wings.slardar.spring.bean;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.mirana.id.LightIdProvider;
import pro.fessional.wings.faceless.service.flakeid.FlakeIdService;
import pro.fessional.wings.faceless.spring.prop.LightIdProviderProp;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.cache.WingsCache;
import pro.fessional.wings.slardar.cache.hazelcast.WingsHazelcastCacheManager;
import pro.fessional.wings.slardar.concur.HazelcastGlobalLock;
import pro.fessional.wings.slardar.event.EventPublishHelper;
import pro.fessional.wings.slardar.event.HazelcastSyncPublisher;
import pro.fessional.wings.slardar.service.flakeid.FlakeIdHazelcastImpl;
import pro.fessional.wings.slardar.service.lightid.HazelcastLightIdProvider;
import pro.fessional.wings.slardar.spring.prop.SlardarCacheProp;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class HazelcastServiceConfiguration {

    private static final Log log = LogFactory.getLog(HazelcastServiceConfiguration.class);

    @Configuration(proxyBeanMethods = false)
    @ConditionalWingsEnabled
    @ConditionalOnClass(FlakeIdService.class)
    public static class FlakeIdServiceBean {
        @Bean
        public FlakeIdService flakeIdService(HazelcastInstance instance) {
            log.info("SlardarHazelCaching spring-bean hazelcastFlakeId Overriding");
            return new FlakeIdHazelcastImpl(instance);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalWingsEnabled
    public static class GlobalPublisherWired {
        @Autowired
        public void auto(HazelcastInstance instance, ApplicationEventPublisher publisher) {
            HazelcastSyncPublisher global = new HazelcastSyncPublisher(instance, publisher);
            EventPublishHelper.setGlobalPublisher(global);
            log.info("SlardarHazelCaching spring-auto initHazelcastSyncPublisher, uuid=" + global.getMessageListenerUuid());
        }
    }

    @Bean
    @ConditionalWingsEnabled
    @ConditionalOnProperty(name = LightIdProviderProp.Key$monotonic, havingValue = "hz")
    public LightIdProvider hzLightIdProvider(LightIdProvider.Loader loader, LightIdProviderProp prop, HazelcastInstance instance) {
        log.info("Faceless spring-bean hzLightIdProvider");
        // avg=1.065ms
        HazelcastLightIdProvider provider = new HazelcastLightIdProvider(loader, instance);
        provider.setTimeout(prop.getTimeout());
        return provider;
    }

    @Bean
    @ConditionalWingsEnabled
    public HazelcastGlobalLock hazelcastGlobalLock(HazelcastInstance hazelcastInstance) {
        log.info("SlardarHazelCaching spring-bean hazelcastGlobalLock");
        return new HazelcastGlobalLock(hazelcastInstance);
    }

    @Bean(WingsCache.Manager.Server)
    @ConditionalWingsEnabled
    public HazelcastCacheManager hazelcastCacheManager(SlardarCacheProp conf, HazelcastInstance instance) {
        log.info("SlardarHazelCaching spring-bean hazelcast as " + WingsCache.Manager.Server);
        return new WingsHazelcastCacheManager(conf, instance);
    }
}
