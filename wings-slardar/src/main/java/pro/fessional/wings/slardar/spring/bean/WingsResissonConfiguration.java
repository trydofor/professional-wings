package pro.fessional.wings.slardar.spring.bean;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;

import static pro.fessional.wings.slardar.spring.bean.WingsCacheConfiguration.MANAGER_REDISSON;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration
@EnableCaching
@Slf4j
@ConditionalOnProperty(name = "spring.wings.slardar.redis.enabled", havingValue = "true")
@ConditionalOnClass(name = "org.springframework.data.redis.connection.RedisConnectionFactory")
public class WingsResissonConfiguration {

    // //////////////////// spring redis ////////////////////
    @Bean
    @ConditionalOnBean(RedisConnectionFactory.class)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        val template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);
        log.info("config redisson RedisTemplate");
        return template;
    }

    @Bean
    @ConditionalOnBean(RedisConnectionFactory.class)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        val template = new StringRedisTemplate();
        template.setConnectionFactory(factory);
        return template;
    }

    // //////////////////// redisson ////////////////////

    @Primary
    @Bean(MANAGER_REDISSON)
    @ConditionalOnBean(RedissonClient.class)
    @ConditionalOnClass(name = "org.redisson.spring.cache.RedissonSpringCacheManager")
    public RedissonSpringCacheManager redissonCacheManager(RedissonClient redissonClient, WingsCacheConfiguration.CacheLevel conf) {

        return new RedissonSpringCacheManager(redissonClient) {
            private final ThreadLocal<String> cacheName = new ThreadLocal<>();

            @Override
            public org.springframework.cache.Cache getCache(String name) {
                cacheName.set(name);
                return super.getCache(name);
            }

            @Override
            protected CacheConfig createDefaultConfig() {
                String name = cacheName.get();
                cacheName.remove();
                if (name != null) {
                    for (Map.Entry<String, WingsCacheConfiguration.Conf> entry : conf.getLevel().entrySet()) {
                        if (name.startsWith(entry.getKey())) {
                            WingsCacheConfiguration.Conf v = entry.getValue();
                            return newRedisson(v.getMaxSize(), v.getTtl(), v.getMaxIdleTime());
                        }
                    }
                }

                return newRedisson(conf.getMaxSize(), conf.getTtl(), conf.getMaxIdleTime());
            }
        };
    }

    private CacheConfig newRedisson(int max, long ttl, long idle) {
        CacheConfig c = new CacheConfig(ttl * 1000, idle * 1000);
        c.setMaxSize(max);
        return c;
    }

    @Bean
    @ConditionalOnBean(RedissonClient.class)
    @ConditionalOnClass(name = "org.redisson.spring.data.connection.RedissonConnectionFactory")
    public RedisConnectionFactory redisConnectionFactory(RedissonClient redissonClient) {
        return new RedissonConnectionFactory(redissonClient);
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnClass(name = "org.redisson.api.RedissonClient")
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        val config = new Config();
        config.useSingleServer()
              .setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort())
              .setConnectTimeout((int) redisProperties.getTimeout().toMillis())
              .setDatabase(redisProperties.getDatabase())
              .setPassword(redisProperties.getPassword())
        ;
        val client = Redisson.create(config);
        log.info("config RedissonClient");
        return client;
    }
}
