package pro.fessional.wings.slardar.spring.bean;

import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.util.Assert;
import pro.fessional.wings.slardar.security.MemoryRedisTokenStore;
import pro.fessional.wings.slardar.servlet.WingsFilterOrder;
import pro.fessional.wings.slardar.servlet.WingsOAuth2xFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2019-07-09
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.wings.filter.oauth2x", name = "enabled", havingValue = "true")
public class WingsOAuth2xConfiguration {

    private final Log logger = LogFactory.getLog(WingsOAuth2xConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder(@Value("${wings.security.password-encoder}") String encoder) {
        logger.info("Wings conf PasswordEncoder bean");
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("noop", NoOpPasswordEncoder.getInstance());
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
        encoders.put("scrypt", new SCryptPasswordEncoder());
        Assert.isTrue(encoders.containsKey(encoder), "unsupported encoder: " + encoder);
        return new DelegatingPasswordEncoder(encoder, encoders);
    }

    @Configuration
    @ConditionalOnClass(RedisConnectionFactory.class)
    @ConditionalOnMissingBean(TokenStore.class)
    @Order(Ordered.LOWEST_PRECEDENCE - 100)
    public class Redis {
        @Bean
        public TokenStore tokenStore(RedisConnectionFactory factory) {
            logger.info("Wings conf Memory-Redis token store");
            return new MemoryRedisTokenStore(factory);
        }
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    @ConditionalOnMissingBean(TokenStore.class)
    public TokenStore tokenStore() {
        logger.info("Wings conf Memory token store");
        return new InMemoryTokenStore();
    }

    @Bean
    @ConfigurationProperties("spring.wings.filter.oauth2x")
    public WingsOAuth2xFilter.Config wingsOAuth2exFilterConfig() {
        return new WingsOAuth2xFilter.Config();
    }

    @Bean
    public WingsOAuth2xFilter wingsOAuth2exFilter(WingsOAuth2xFilter.Config config) {
        logger.info("Wings conf OAuth2x filter");
        WingsOAuth2xFilter filter = new WingsOAuth2xFilter(config);
        filter.setOrder(WingsFilterOrder.OAUTH2X);
        return filter;
    }

    @Bean
    @Lazy
    public Helper wingsOAuth2xConfigurationHelper() {
        return new Helper();
    }

    @Setter(onMethod = @__({@Autowired}))
    public static class Helper {

        private TokenStore tokenStore;
        private WingsOAuth2xFilter.Config config;
        private AuthenticationManager authenticationManager;
        private UserDetailsService userDetailsService;
        private PasswordEncoder passwordEncoder;

        public ClientDetailsServiceConfigurer configure(ClientDetailsServiceConfigurer clients) throws Exception {
            InMemoryClientDetailsServiceBuilder builder = clients.inMemory();
            for (WingsOAuth2xFilter.Client c : config.getClient().values()) {
                String secret = passwordEncoder.encode(c.getClientSecret());
                builder.withClient(c.getClientId())
                       .secret(secret)
                       .redirectUris(c.getRedirectUri())
                       .authorizedGrantTypes(c.getGrantType())
                       .scopes(c.getScope())
                       .accessTokenValiditySeconds(config.getAccessTokenLive())
                       .refreshTokenValiditySeconds(config.getRefreshTokenLive())
                       .autoApprove(c.isAutoApprove())
                ;
            }
            return clients;
        }

        public AuthorizationServerEndpointsConfigurer configure(AuthorizationServerEndpointsConfigurer endpoints) {
            endpoints.authenticationManager(authenticationManager)
                     .userDetailsService(userDetailsService)
                     .tokenStore(tokenStore)
            ;
            return endpoints;
        }

        public AuthorizationServerSecurityConfigurer configure(AuthorizationServerSecurityConfigurer security) {
            security.allowFormAuthenticationForClients()
                    .passwordEncoder(passwordEncoder)
                    .tokenKeyAccess("permitAll()")
                    .checkTokenAccess("isAuthenticated()")
            ;
            return security;
        }

        public HttpSecurity configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                .antMatchers("/oauth/**", "/error").permitAll()
                .antMatchers("/login", "/logout").permitAll()
            ;
            return http;
        }

        public ResourceServerSecurityConfigurer configure(ResourceServerSecurityConfigurer resources) {
            resources.tokenStore(tokenStore)
            ;
            return resources;
        }
    }
}
