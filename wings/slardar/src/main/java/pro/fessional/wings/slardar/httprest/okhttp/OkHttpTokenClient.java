package pro.fessional.wings.slardar.httprest.okhttp;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;

/**
 * 自动完成基于Header Token验证功能的Client，比如Oauth2
 * <p>
 * 采用继承的方式实现，因为Interceptor及Authenticator方式不够轻便
 * <a href="https://medium.com/@sumon.v0.0/okhttp-how-to-refresh-access-token-efficiently-6dece4d271c0">okhttp-how-to-refresh-access-token-efficiently</a>
 *
 * @author trydofor
 * @since 2022-11-25
 */
public class OkHttpTokenClient implements Call.Factory, OkHttpBuildableClient {

    private final Tokenize tokenize;
    private final Call.Factory tkClient;

    public OkHttpTokenClient(@NotNull final OkHttpClient client, @NotNull final Tokenize tokenize) {
        this.tokenize = tokenize;
        this.tkClient = (Call.Factory) client
                .newBuilder()
                .authenticator((ignored, response) -> {
                    if (syncInitToken(tokenize, (Call.Factory) client)) {
                        final Request.Builder bd = response.request().newBuilder();
                        if (tokenize.fillToken(bd)) {
                            return bd.build();
                        }
                    }
                    return null;
                })
                .build();
    }

    @NotNull
    @Override
    public Call newCall(@NotNull Request request) {
        if (tokenize != null && tokenize.needToken(request)) {
            return newCall(request.newBuilder());
        }

        return tkClient.newCall(request);
    }

    @NotNull
    @Override
    public Call newCall(@NotNull Request.Builder builder) {
        if (!tokenize.fillToken(builder)) {
            if (syncInitToken(tokenize, tkClient)) {
                if (!tokenize.fillToken(builder)) {
                    throw new IllegalStateException("failed to fill token");
                }
            }
            else {
                throw new IllegalStateException("failed to init token");
            }
        }
        //
        return tkClient.newCall(builder.build());
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public static boolean syncInitToken(@NotNull final Tokenize tokenize, @NotNull Call.Factory callFactory) {
        final Request.Builder builder = new Request.Builder();
        synchronized (tokenize) {
            // re check
            if (tokenize.fillToken(builder)) {
                return true;
            }
            else {
                return tokenize.initToken(callFactory);
            }
        }
    }

    public interface Tokenize {
        /**
         * 是否需要设置token
         */
        boolean needToken(@NotNull Request request);

        /**
         * 设置有效的token，一般在header。
         * false表示token无效，应该尝试init
         */
        boolean fillToken(Request.Builder builder);

        /**
         * 尝试获取有效token
         */
        boolean initToken(@NotNull Call.Factory callFactory);
    }
}
