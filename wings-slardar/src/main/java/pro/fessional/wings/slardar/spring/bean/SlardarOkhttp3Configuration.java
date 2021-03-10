package pro.fessional.wings.slardar.spring.bean;

import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.CookieJar;
import okhttp3.Dns;
import okhttp3.OkHttpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import pro.fessional.wings.slardar.httprest.OkHttpClientHelper;
import pro.fessional.wings.slardar.httprest.RestTemplateHelper;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.slardar.spring.prop.SlardarOkHttpProp;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author trydofor
 * @link https://docs.spring.io/spring-boot/docs/2.4.2/reference/htmlsingle/#boot-features-resttemplate-customization
 * @see RestTemplateAutoConfiguration#RestTemplateAutoConfiguration()
 * @since 2020-05-22
 */
@Configuration
@ConditionalOnClass(OkHttpClient.class)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$okhttp, havingValue = "true")
public class SlardarOkhttp3Configuration {

    private static final Log logger = LogFactory.getLog(SlardarOkhttp3Configuration.class);

    @Bean
    @ConditionalOnMissingBean
    public OkHttpClient okHttpClient(
            ObjectProvider<Cache> cache,
            ObjectProvider<CookieJar> cookieJar,
            ObjectProvider<Dns> dns,
            ConnectionPool connectionPool,
            SlardarOkHttpProp properties
    ) {
        // check builder return new ...
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(properties.getTimeoutConn()))
                .readTimeout(Duration.ofSeconds(properties.getTimeoutRead()))
                .writeTimeout(Duration.ofSeconds(properties.getTimeoutWrite()))
                .pingInterval(Duration.ofSeconds(properties.getPingInterval()))

                .followRedirects(properties.isFollowRedirect())
                .followSslRedirects(properties.isFollowRedirectSsl())
                .retryOnConnectionFailure(properties.isRetryFailure());

        // cache
        Cache cacheBean = cache.getIfAvailable();
        if (cacheBean == null) {
            int mbs = properties.getCacheMegabyte();
            if (mbs > 0) {
                File cacheDir = properties.getCacheDirectory();
                try {
                    if (cacheDir == null) {
                        cacheDir = Files.createTempDirectory("wings-okhttp-cache").toFile();
                    }
                    builder.cache(new Cache(cacheDir, mbs * 1024 * 1024));
                } catch (Exception e) {
                    logger.warn("failed to create okhttp cache on dir=" + cacheDir, e);
                }
            }
        } else {
            builder.cache(cacheBean);
        }


        cookieJar.ifUnique(builder::cookieJar);
        dns.ifUnique(builder::dns);
        builder.connectionPool(connectionPool);

        return properties.isSslTrustAll() ? OkHttpClientHelper.sslTrustAll(builder) : builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public ConnectionPool okHttp3ConnectionPool(SlardarOkHttpProp config) {
        int maxIdleConnections = config.getMaxIdle();
        return new ConnectionPool(maxIdleConnections, config.getKeepAlive(), TimeUnit.SECONDS);
    }

    @Bean
    @ConditionalOnMissingBean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public RestTemplate restTemplate(RestTemplateBuilder builder, OkHttpClient client) {
        return RestTemplateHelper.sslTrustAll(builder, client);
    }

}
