package pro.fessional.wings.slardar.httprest.retrofit;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.wings.slardar.fastjson.FastJsonHelper;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static pro.fessional.wings.slardar.httprest.okhttp.OkHttpMediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * @author trydofor
 * @see JacksonConverterFactory
 * @since 2022-09-11
 */
public class FastJsonConverterFactory extends Converter.Factory {

    private static final FastJsonConverterFactory INSTANCE = new FastJsonConverterFactory();

    public static Converter.Factory create() {
        return INSTANCE;
    }

    @Nullable @Override
    public Converter<ResponseBody, ?> responseBodyConverter(@NotNull Type type, Annotation @NotNull [] annotations, @NotNull Retrofit retrofit) {
        return body -> FastJsonHelper.object(body.bytes(), type);
    }

    @Nullable @Override
    public Converter<?, RequestBody> requestBodyConverter(@NotNull Type type, Annotation @NotNull [] parameterAnnotations, Annotation @NotNull [] methodAnnotations, @NotNull Retrofit retrofit) {
        return body -> RequestBody.create(FastJsonHelper.bytes(body), APPLICATION_JSON_UTF8_VALUE);
    }
}
