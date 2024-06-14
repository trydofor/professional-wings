package pro.fessional.wings.slardar.httprest.okhttp;

import okhttp3.OkHttpClient.Builder;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.function.SingletonSupplier;
import pro.fessional.mirana.netx.SslTrustAll;
import pro.fessional.wings.silencer.spring.help.ApplicationContextHelper;

/**
 * @author trydofor
 * @since 2022-11-24
 */
public class OkHttpClientBuilder {

    // lazy initialization holder class idiom
    private static final class DefaultBuilderHolder {
        private static final Builder DefaultBuilder = new Builder();
    }

    /**
     * Static global default builder
     */
    @NotNull
    public static Builder staticBuilder() {
        return DefaultBuilderHolder.DefaultBuilder;
    }

    private static final SingletonSupplier<Builder> SpringBuilder = ApplicationContextHelper
        .getSingletonSupplier(Builder.class, OkHttpClientBuilder::staticBuilder);

    /**
     * Spring injected Bean
     */
    @NotNull
    public static Builder springBuilder() {
        return SpringBuilder.obtain();
    }

    public static void sslTrustAll(Builder builder) {
        builder.sslSocketFactory(SslTrustAll.SSL_SOCKET_FACTORY, SslTrustAll.X509_TRUST_MANAGER)
               .hostnameVerifier(SslTrustAll.HOSTNAME_VERIFIER);
    }

    public static void cookieHost(Builder builder) {
        builder.cookieJar(new OkHttpHostCookie());
    }
}
