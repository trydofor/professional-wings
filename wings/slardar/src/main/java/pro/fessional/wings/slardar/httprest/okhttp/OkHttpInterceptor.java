package pro.fessional.wings.slardar.httprest.okhttp;

import okhttp3.Interceptor;

/**
 * @author trydofor
 * @since 2022-06-16
 */
public interface OkHttpInterceptor extends Interceptor {

    default boolean isNetwork() {
        return false;
    }
}
