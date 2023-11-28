package pro.fessional.wings.slardar.spring.bean;

import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.CookieJar;
import okhttp3.Dns;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.runner.CommandLineRunnerOrdered;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpClientBuilder;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpClientHelper;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpHostCookie;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpInterceptor;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpRedirectNopInterceptor;
import pro.fessional.wings.slardar.spring.prop.SlardarOkhttpProp;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author trydofor
 * @link <a href="https://docs.spring.io/spring-boot/docs/3.0.3/reference/htmlsingle/#io.rest-client.resttemplate.customization">RestTemplate Customization</a>
 * @see RestTemplateAutoConfiguration#RestTemplateAutoConfiguration()
 * @since 2020-05-22
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(OkHttpClient.class)
@ConditionalWingsEnabled
public class SlardarOkhttpConfiguration {

    private static final Log log = LogFactory.getLog(SlardarOkhttpConfiguration.class);

    @Bean
    @ConditionalWingsEnabled
    public ConnectionPool okhttpConnectionPool(SlardarOkhttpProp config) {
        log.info("Slardar spring-bean okhttp3ConnectionPool");
        int maxIdleConnections = config.getMaxIdle();
        return new ConnectionPool(maxIdleConnections, config.getKeepAlive(), TimeUnit.SECONDS);
    }

    @Bean
    @ConditionalWingsEnabled
    public CookieJar okhttpHostCookieJar() {
        log.info("Slardar spring-bean okhttpHostCookieJar");
        return new OkHttpHostCookie();
    }

    @Bean
    @ConditionalWingsEnabled
    @ConditionalOnExpression("${" + SlardarOkhttpProp.Key$followRedirect + ":false} || ${" + SlardarOkhttpProp.Key$followRedirectSsl + ":false}")
    public OkHttpRedirectNopInterceptor okhttpRedirectNopInterceptor() {
        log.info("Slardar spring-bean okhttpRedirectNopInterceptor");
        return new OkHttpRedirectNopInterceptor();
    }

    @Bean
    @ConditionalWingsEnabled
    public Builder okhttpClientBuilder(
            ObjectProvider<Cache> cacheProvider,
            ObjectProvider<CookieJar> cookieProvider,
            ObjectProvider<Dns> dnsProvider,
            ConnectionPool connectionPool,
            ObjectProvider<Interceptor> interceptors,
            SlardarOkhttpProp properties
    ) {
        log.info("Slardar spring-bean okhttpClientBuilder");
        // check builder return new ...
        final Builder builder = new Builder()
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
                log.info("Slardar conf okhttpClient addNetworkInterceptor:" + it.getClass());
                builder.addNetworkInterceptor(it);
            }
            else {
                log.info("Slardar conf okhttpClient addInterceptor:" + it.getClass());
                builder.addInterceptor(it);
            }
        });

        // cache
        Cache cacheBean = cacheProvider.getIfAvailable();
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
                    log.warn("failed to create okhttp cache on dir=" + cacheDir, e);
                }
                log.info("Slardar conf okhttpClient cache-dir=" + properties.getCacheDirectory());
            }
            else {
                log.info("Slardar conf okhttpClient no-cache");
            }
        }
        else {
            builder.cache(cacheBean);
            log.info("Slardar conf okhttpClient cache=" + cacheBean.getClass().getName());
        }

        builder.connectionPool(connectionPool);

        final CookieJar ck = cookieProvider.getIfAvailable();
        if (ck != null) {
            log.info("Slardar conf okhttpClient CookieJar=" + ck.getClass().getName());
            builder.cookieJar(ck);
        }
        final Dns dns = dnsProvider.getIfAvailable();
        if (dns != null) {
            log.info("Slardar conf okhttpClient dns=" + dns.getClass().getName());
            builder.dns(dns);
        }

        if (properties.isSslTrustAll()) {
            log.info("Slardar conf okhttpClient sslTrustAll");
            OkHttpClientBuilder.sslTrustAll(builder);
        }
        return builder;
    }

    @Bean
    @ConditionalWingsEnabled
    public OkHttpClient okhttpClient(Builder builder) {
        log.info("Slardar spring-bean okhttpClient");
        return builder.build();
    }

    @Bean
    @ConditionalWingsEnabled
    public CommandLineRunnerOrdered okhttpHelperRunner(ObjectProvider<Builder> opb, ObjectProvider<OkHttpClient> ohc) {
        log.info("Slardar spring-runs runnerOkHttpHelper");
        return new CommandLineRunnerOrdered(WingsOrdered.Lv3Service, ignored -> {
            final Builder ob = opb.getIfAvailable();
            if (ob != null) {
                log.info("Slardar spring-conf OkHttpClientBuilder");
                new OkHttpClientBuilder() {{
                    SpringBuilder = ob;
                }};
            }

            final OkHttpClient oc = ohc.getIfAvailable();
            if (oc != null) {
                log.info("Slardar spring-conf OkHttpClientHelper");
                new OkHttpClientHelper() {{
                    SpringClient = oc;
                }};
            }
        });
    }
}
