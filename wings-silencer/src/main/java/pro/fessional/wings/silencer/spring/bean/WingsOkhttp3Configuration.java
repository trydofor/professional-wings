package pro.fessional.wings.silencer.spring.bean;

import lombok.Data;
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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author trydofor
 * @link https://docs.spring.io/spring-boot/docs/2.2.7.RELEASE/reference/htmlsingle/#boot-features-resttemplate-customization
 * @see RestTemplateAutoConfiguration#RestTemplateAutoConfiguration()
 * @since 2020-05-22
 */
@Configuration
@ConditionalOnClass(OkHttpClient.class)
@ConditionalOnProperty(prefix = "spring.wings.okhttp", name = "enabled", havingValue = "true")
public class WingsOkhttp3Configuration {

    private static final Log logger = LogFactory.getLog(WingsOkhttp3Configuration.class);

    @Bean
    @ConditionalOnMissingBean
    public OkHttpClient okHttp3Client(
            ObjectProvider<Cache> cache,
            ObjectProvider<CookieJar> cookieJar,
            ObjectProvider<Dns> dns,
            ConnectionPool connectionPool,
            OkHttpProperties okHttpProperties
    ) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();


        builder.connectTimeout(Duration.ofSeconds(okHttpProperties.getTimeoutConn()));
        builder.readTimeout(Duration.ofSeconds(okHttpProperties.getTimeoutRead()));
        builder.writeTimeout(Duration.ofSeconds(okHttpProperties.getTimeoutWrite()));
        builder.pingInterval(Duration.ofSeconds(okHttpProperties.getPingInterval()));


        builder.followRedirects(okHttpProperties.isFollowRedirects());
        builder.followSslRedirects(okHttpProperties.isFollowSslRedirects());
        builder.retryOnConnectionFailure(okHttpProperties.isRetryFailure());

        // cache
        Cache cacheBean = cache.getIfAvailable();
        if (cacheBean == null) {
            int mbs = okHttpProperties.getCacheMegabyte();
            if (mbs > 0) {
                File cacheDir = okHttpProperties.getCacheDirectory();
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

        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public ConnectionPool okHttp3ConnectionPool(OkHttpProperties okHttpProperties) {
        int maxIdleConnections = okHttpProperties.getMaxIdle();
        return new ConnectionPool(maxIdleConnections, okHttpProperties.getKeepAlive(), TimeUnit.SECONDS);
    }

    @Bean
    @ConfigurationProperties(prefix = "wings.okhttp")
    public OkHttpProperties okHttpProperties() {
        return new OkHttpProperties();
    }

    @Data
    public static class OkHttpProperties {

        private int timeoutConn = 10; // "okhttp.write-timeout=21s"
        private int timeoutRead = 60;
        private int timeoutWrite = 60;
        // The default value of 0 disables client-initiated pings
        private int pingInterval = 0;

        private int cacheMegabyte = 0;
        private File cacheDirectory = null;

        private boolean followSslRedirects = true;
        private boolean followRedirects = true;
        private boolean retryFailure = true;
        private int maxIdle = 5;
        private int keepAlive = 300;
    }
}
