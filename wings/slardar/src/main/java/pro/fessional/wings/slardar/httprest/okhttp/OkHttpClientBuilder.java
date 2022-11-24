package pro.fessional.wings.slardar.httprest.okhttp;

import okhttp3.OkHttpClient.Builder;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.netx.SslTrustAll;

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
     * 静态全局的默认初始化的
     */
    @NotNull
    public static Builder staticBuilder() {
        return DefaultBuilderHolder.DefaultBuilder;
    }

    protected static Builder SpringBuilder;

    /**
     * 注入的Spring Bean
     */
    @NotNull
    public static Builder springBuilder() {
        return SpringBuilder != null ? SpringBuilder : staticBuilder();
    }

    public static void sslTrustAll(Builder builder) {
        builder.sslSocketFactory(SslTrustAll.SSL_SOCKET_FACTORY, SslTrustAll.X509_TRUST_MANAGER)
               .hostnameVerifier(SslTrustAll.HOSTNAME_VERIFIER);
    }

    public static void cookieHost(Builder builder) {
        builder.cookieJar(new OkHttpHostCookie());
    }


}
