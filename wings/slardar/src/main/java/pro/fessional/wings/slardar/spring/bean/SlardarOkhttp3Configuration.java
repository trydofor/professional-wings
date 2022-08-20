package pro.fessional.wings.slardar.spring.bean;

import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.CookieJar;
import okhttp3.Dns;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import pro.fessional.wings.slardar.httprest.OkHttpClientHelper;
import pro.fessional.wings.slardar.httprest.OkHttpInterceptor;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.slardar.spring.prop.SlardarOkHttpProp;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author trydofor
 * @link https://docs.spring.io/spring-boot/docs/2.6.6/reference/htmlsingle/#boot-features-resttemplate-customization
 * @see RestTemplateAutoConfiguration#RestTemplateAutoConfiguration()
 * @since 2020-05-22
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(OkHttpClient.class)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$okhttp, havingValue = "true")
public class SlardarOkhttp3Configuration {

    private static final Log logger = LogFactory.getLog(SlardarOkhttp3Configuration.class);

    @Bean
    @ConditionalOnMissingBean(ConnectionPool.class)
    public ConnectionPool okHttp3ConnectionPool(SlardarOkHttpProp config) {
        logger.info("Wings conf okHttp3ConnectionPool");
        int maxIdleConnections = config.getMaxIdle();
        return new ConnectionPool(maxIdleConnections, config.getKeepAlive(), TimeUnit.SECONDS);
    }

    @Bean
    @ConditionalOnMissingBean(RestTemplateBuilder.class)
    public RestTemplateBuilder restTemplateBuilder(RestTemplateBuilderConfigurer configurer, OkHttpClient client) {
        logger.info("Wings conf restTemplateBuilder");
        final RestTemplateBuilder builder = configurer.configure(new RestTemplateBuilder());
        return builder.requestFactory(() -> OkHttpClientHelper.requestFactory(client));
    }

    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    public RestTemplate okRestTemplate(RestTemplateBuilder builder) {
        logger.info("Wings conf okRestTemplate");
        return builder.build();
    }

    @Bean
    @ConditionalOnProperty(value = SlardarOkHttpProp.Key$hostCookie, havingValue = "true")
    public CookieJar hostCookieJar() {
        logger.info("Wings conf hostCookieJar");
        return new OkHttpClientHelper.HostCookieJar();
    }

    @Bean
    @ConditionalOnMissingBean(OkHttpClient.class)
    public OkHttpClient okHttpClient(
            ObjectProvider<Cache> cacheProvier,
            ObjectProvider<CookieJar> cookieProvider,
            ObjectProvider<Dns> dnsProvider,
            ConnectionPool connectionPool,
            ObjectProvider<Interceptor> interceptors,
            SlardarOkHttpProp properties
    ) {
        logger.info("Wings conf okHttpClient");
        // check builder return new ...
        final OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(properties.getTimeoutConn()))
                .readTimeout(Duration.ofSeconds(properties.getTimeoutRead()))
                .writeTimeout(Duration.ofSeconds(properties.getTimeoutWrite()))
                .pingInterval(Duration.ofSeconds(properties.getPingInterval()))
                .followRedirects(properties.isFollowRedirect())
                .followSslRedirects(properties.isFollowRedirectSsl())
                .retryOnConnectionFailure(properties.isRetryFailure());

        // interceptors
        interceptors.orderedStream().forEach(it -> {
            if (it instanceof OkHttpInterceptor && ((OkHttpInterceptor) it).isNetwork()) {
                logger.info("Wings conf okHttpClient addNetworkInterceptor:" + it.getClass());
                builder.addNetworkInterceptor(it);
            }
            else {
                logger.info("Wings conf okHttpClient addInterceptor:" + it.getClass());
                builder.addInterceptor(it);
            }
        });

        // cache
        Cache cacheBean = cacheProvier.getIfAvailable();
        if (cacheBean == null) {
            int mbs = properties.getCacheMegabyte();
            if (mbs > 0) {
                File cacheDir = properties.getCacheDirectory();
                try {
                    if (cacheDir == null) {
                        cacheDir = Files.createTempDirectory("wings-okhttp-cache").toFile();
                    }
                    builder.cache(new Cache(cacheDir, mbs * 1024L * 1024L));
                }
                catch (Exception e) {
                    logger.warn("failed to create okhttp cache on dir=" + cacheDir, e);
                }
                logger.info("Wings conf okHttpClient cache-dir=" + properties.getCacheDirectory());
            }
            else {
                logger.info("Wings conf okHttpClient no-cache");
            }
        }
        else {
            builder.cache(cacheBean);
            logger.info("Wings conf okHttpClient cache=" + cacheBean.getClass().getName());
        }

        builder.connectionPool(connectionPool);

        final CookieJar ck = cookieProvider.getIfAvailable();
        if (ck != null) {
            logger.info("Wings conf okHttpClient CookieJar=" + ck.getClass().getName());
            builder.cookieJar(ck);
        }
        final Dns dns = dnsProvider.getIfAvailable();
        if (dns != null) {
            logger.info("Wings conf okHttpClient dns=" + dns.getClass().getName());
            builder.dns(dns);
        }

        if (properties.isSslTrustAll()) {
            logger.info("Wings conf okHttpClient sslTrustAll");
            OkHttpClientHelper.sslTrustAll(builder);
        }
        return builder.build();
    }
}
