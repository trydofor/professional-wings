package pro.fessional.wings.slardar.spring.bean;

import lombok.Data;
import lombok.Setter;
import lombok.val;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
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
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.util.Assert;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import pro.fessional.mirana.code.LeapCode;
import pro.fessional.wings.slardar.security.JdkSerializationStrategy;
import pro.fessional.wings.slardar.security.WingsOAuth2xLogin;
import pro.fessional.wings.slardar.security.WingsTokenEnhancer;
import pro.fessional.wings.slardar.security.WingsTokenStore;
import pro.fessional.wings.slardar.servlet.WingsFilterOrder;
import pro.fessional.wings.slardar.servlet.WingsOAuth2xFilter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2019-07-09
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.wings.slardar.oauth2x", name = "enabled", havingValue = "true")
public class WingsOAuth2xConfiguration {

    private final Log logger = LogFactory.getLog(WingsOAuth2xConfiguration.class);

    @Bean
    @ConfigurationProperties("wings.slardar.security")
    public Actoken wingsOAuth2xConfigurationSecurity() {
        return new Actoken();
    }

    /**
     * #{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
     * #{noop}password
     * #{pbkdf2}5d923b44a6d129f3ddf3e3c8d29412723dcbde72445e8ef6bf3b508fbf17fa4ed4d6b99ca763d8dc
     * #{scrypt}$e0801$8bWJaSu2IKSn9Z9kM+TPXfOc/9bdYSrN1oD9qfVThWEwdRTnO7re7Ei+fUZRJ68k9lTyuTeUp4of4g24hHnazw==$OAOec05+bXxvuu/1qZ6NUR+xQYvYv7BeL1QxwRpY5Pc=
     */
    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder(@Value("${wings.slardar.security.password-encoder}") String encoder) {
        logger.info("Wings conf PasswordEncoder bean");
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("noop", NoOpPasswordEncoder.getInstance());
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
        encoders.put("scrypt", new SCryptPasswordEncoder());
        Assert.isTrue(encoders.containsKey(encoder), "unsupported encoder: " + encoder);
        return new DelegatingPasswordEncoder(encoder, encoders);
    }

    @Bean
    @ConditionalOnMissingBean(WingsOAuth2xLogin.class)
    public WingsOAuth2xLogin wingsOAuth2xLogin(Actoken actoken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        WingsOAuth2xLogin login = new WingsOAuth2xLogin();
        login.setHeaders(headers);
        login.setThirdTokenKey(actoken.thirdTokenKey);
        login.setTokenLiveKey(actoken.tokenLiveKey);
        login.setRenewTokenKey(actoken.renewTokenKey);

        return login;
    }

    @Bean
    @ConditionalOnMissingBean(TokenStore.class)
    public WingsTokenStore tokenStore(Actoken actoken) {
        logger.info("Wings conf InMemoryTokenStore to WingsTokenStore");
        WingsTokenStore store = new WingsTokenStore();
        store.addStore(new InMemoryTokenStore());
        store.setRenewTokenKey(actoken.renewTokenKey);
        return store;
    }

    @Configuration
    @ConditionalOnClass(RedisConnectionFactory.class)
    @ConditionalOnProperty(prefix = "wings.slardar.actoken", name = "redis-store", havingValue = "true")
    @Order(Ordered.LOWEST_PRECEDENCE - 100)
    public class Redis {
        @Autowired
        public void tokenStore(TokenStore tokenStore, RedisConnectionFactory factory) {
            if (tokenStore instanceof WingsTokenStore) {
                logger.info("Wings conf RedisTokenStore to WingsTokenStore");
                RedisTokenStore redis = new RedisTokenStore(factory);
                redis.setSerializationStrategy(new JdkSerializationStrategy());
                ((WingsTokenStore) tokenStore).addStore(redis);
            }
        }
    }

    @Bean
    @ConditionalOnProperty(prefix = "wings.slardar.actoken", name = "wings-enhance", havingValue = "true")
    public WingsTokenEnhancer tokenEnhancer(Actoken actoken, LeapCode leapCode) {
        logger.info("Wings conf WingsTokenEnhancer");
        WingsTokenEnhancer enhancer = new WingsTokenEnhancer();
        enhancer.setThirdTokenKey(actoken.getThirdTokenKey());
        enhancer.setTokenLiveKey(actoken.getTokenLiveKey());
        enhancer.setWingsPrefix(actoken.getWingsPrefix());
        enhancer.setLeapCode(leapCode);
        return enhancer;
    }

    @Bean
    @ConfigurationProperties("wings.slardar.oauth2x")
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

    public static class Helper {

        @Setter(onMethod = @__({@Autowired}))
        private WingsOAuth2xFilter.Config config;
        @Setter(onMethod = @__({@Autowired}))
        private PasswordEncoder passwordEncoder;
        @Setter(onMethod = @__({@Autowired}))
        private ObjectProvider<TokenStore> tokenStore;
        @Setter(onMethod = @__({@Autowired}))
        private ObjectProvider<AuthenticationManager> authenticationManager;
        @Setter(onMethod = @__({@Autowired}))
        private ObjectProvider<UserDetailsService> userDetailsService;
        @Setter(onMethod = @__({@Autowired}))
        private ObjectProvider<TokenEnhancer> tokenEnhancer;

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
            endpoints.authenticationManager(authenticationManager.getIfAvailable())
                     .userDetailsService(userDetailsService.getIfAvailable())
                     .tokenStore(tokenStore.getIfAvailable())
                     .tokenEnhancer(tokenEnhancer.getIfAvailable())
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

        public ResourceServerSecurityConfigurer configure(ResourceServerSecurityConfigurer resources) {
            resources.tokenStore(tokenStore.getIfAvailable())
            ;
            return resources;
        }

        public ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry permitAll(HttpSecurity http) throws Exception {
            val registry = permitAllCors(http);
            permitLogin(registry);
            permitOAuth2(registry);
            permitSwagger2(registry);
            return registry;
        }

        public ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry permitAllCors(HttpSecurity http) throws Exception {
            // https://stackoverflow.com/questions/36968963
            // CorsConfiguration#applyPermitDefaultValues
            return http
                    .cors().configurationSource(corsAllowAll())
                    .and()
                    .authorizeRequests();
        }

        public ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry permitLogin(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
            registry.antMatchers(loginAntPaths()).permitAll()
            ;
            return registry;
        }

        public ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry permitOAuth2(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
            registry.antMatchers(oauth2AntPaths()).permitAll()
            ;
            return registry;
        }

        public ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry permitSwagger2(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
            registry.antMatchers(swagger2AntPaths()).permitAll()
            ;
            return registry;
        }

        public CorsConfigurationSource corsAllowAll() {
            return request -> {
                CorsConfiguration conf = new CorsConfiguration();
                conf.addAllowedHeader("*");
                conf.addAllowedOrigin("*");
                conf.addAllowedMethod("*");
                conf.setMaxAge(1800L);
                return conf;
            };
        }

        public String[] oauth2AntPaths() {
            return new String[]{"/oauth/**", "/error"};
        }

        public String[] loginAntPaths() {
            return new String[]{"/login", "/login/**", "/logout"};
        }

        public String[] swagger2AntPaths() {
            return new String[]{"/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**"};
        }

    }

    @Data
    public static class Actoken {
        /**
         * wing下access_token前缀，用以区分
         */
        private String wingsPrefix = "WG-";
        /**
         * 第三方token的parameter key
         */
        private String thirdTokenKey = "access_token_3rd";
        /**
         * 是否获取新的token，意味着作废老token
         */
        private String renewTokenKey = "access_token_renew";

        /**
         * 定义更短的access-token-live，必须小于默认时长
         */
        private String tokenLiveKey = "access_token_live";
    }
}
