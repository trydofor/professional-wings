package pro.fessional.wings.slardar.httprest.okhttp;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * 当followRedirects时，根据request的header设置，把Location头改为Nop-Location，
 * 从而阻断此请求的Redirect
 * <p>
 * 参考 RetryAndFollowUpInterceptor#followUpRequest
 *
 * @author trydofor
 * @since 2022-11-01
 */
@Slf4j
public class OkHttpRedirectNopInterceptor implements OkHttpInterceptor {

    public static final String NopLocation = "Nop-Location";
    public static final String Location = "Location";

    @NotNull
    @Override
    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
        final Request request = chain.request();
        Response response = chain.proceed(request);
        if (response.isRedirect() && request.header(NopLocation) != null) {
            final String loc = response.header(Location);
            if (loc != null) {
                response = response.newBuilder()
                                   .removeHeader(Location)
                                   .addHeader(NopLocation, loc)
                                   .build();
            }
        }
        return response;
    }

    @Override
    public boolean isNetwork() {
        return true;
    }
}
