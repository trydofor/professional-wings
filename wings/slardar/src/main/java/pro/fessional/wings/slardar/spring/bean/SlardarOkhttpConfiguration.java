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
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.runner.CommandLineRunnerOrdered;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpClientBuilder;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpClientHelper;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpHostCookie;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpInterceptor;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpRedirectNopInterceptor;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.slardar.spring.prop.SlardarOkHttpProp;
import pro.fessional.wings.spring.consts.OrderedSlardarConst;

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
@ConditionalOnProperty(name = SlardarEnabledProp.Key$okhttp, havingValue = "true")
@AutoConfigureOrder(OrderedSlardarConst.OkhttpConfiguration)
public class SlardarOkhttpConfiguration {

    private static final Log log = LogFactory.getLog(SlardarOkhttpConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(ConnectionPool.class)
    public ConnectionPool okHttp3ConnectionPool(SlardarOkHttpProp config) {
        log.info("Slardar spring-bean okHttp3ConnectionPool");
        int maxIdleConnections = config.getMaxIdle();
        return new ConnectionPool(maxIdleConnections, config.getKeepAlive(), TimeUnit.SECONDS);
    }

    @Bean
    @ConditionalOnProperty(value = SlardarOkHttpProp.Key$hostCookie, havingValue = "true")
    public CookieJar hostCookieJar() {
        log.info("Slardar spring-bean hostCookieJar");
        return new OkHttpHostCookie();
    }

    @Bean
    @ConditionalOnExpression("${" + SlardarOkHttpProp.Key$redirectNop + ":false}"
                             + "&& (${" + SlardarOkHttpProp.Key$followRedirect + ":false} || ${" + SlardarOkHttpProp.Key$followRedirect + ":false})")
    public OkHttpRedirectNopInterceptor okHttpRedirectNopInterceptor() {
        log.info("Slardar spring-bean okHttpRedirectNopInterceptor");
        return new OkHttpRedirectNopInterceptor();
    }

    @Bean
    public Builder okHttpClientBuilder(
            ObjectProvider<Cache> cacheProvider,
            ObjectProvider<CookieJar> cookieProvider,
            ObjectProvider<Dns> dnsProvider,
            ConnectionPool connectionPool,
            ObjectProvider<Interceptor> interceptors,
            SlardarOkHttpProp properties
    ) {
        log.info("Slardar spring-bean okHttpClientBuilder");
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
                log.info("Slardar conf okHttpClient addNetworkInterceptor:" + it.getClass());
                builder.addNetworkInterceptor(it);
            }
            else {
                log.info("Slardar conf okHttpClient addInterceptor:" + it.getClass());
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
                log.info("Slardar conf okHttpClient cache-dir=" + properties.getCacheDirectory());
            }
            else {
                log.info("Slardar conf okHttpClient no-cache");
            }
        }
        else {
            builder.cache(cacheBean);
            log.info("Slardar conf okHttpClient cache=" + cacheBean.getClass().getName());
        }

        builder.connectionPool(connectionPool);

        final CookieJar ck = cookieProvider.getIfAvailable();
        if (ck != null) {
            log.info("Slardar conf okHttpClient CookieJar=" + ck.getClass().getName());
            builder.cookieJar(ck);
        }
        final Dns dns = dnsProvider.getIfAvailable();
        if (dns != null) {
            log.info("Slardar conf okHttpClient dns=" + dns.getClass().getName());
            builder.dns(dns);
        }

        if (properties.isSslTrustAll()) {
            log.info("Slardar conf okHttpClient sslTrustAll");
            OkHttpClientBuilder.sslTrustAll(builder);
        }
        return builder;
    }

    @Bean
    @ConditionalOnMissingBean(OkHttpClient.class)
    public OkHttpClient okHttpClient(Builder builder) {
        log.info("Slardar spring-bean okHttpClient");
        return builder.build();
    }

    @Bean
    public CommandLineRunnerOrdered runnerOkHttpHelper(ObjectProvider<Builder> opb, ObjectProvider<OkHttpClient> ohc) {
        log.info("Slardar spring-runs runnerOkHttpHelper");
        return new CommandLineRunnerOrdered(OrderedSlardarConst.RunnerOkHttpHelper, ignored -> {
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
