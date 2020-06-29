package pro.fessional.wings.slardar.spring.bean;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

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
