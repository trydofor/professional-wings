package pro.fessional.wings.slardar.httprest;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static pro.fessional.wings.slardar.httprest.OkHttpClientHelper.APPLICATION_JSON_UTF8_VALUE;

/**
 * @author trydofor
 * @since 2022-09-11
 */
public class FastJsonConvertFactory extends Converter.Factory {

    private static final FastJsonConvertFactory INSTANCE = new FastJsonConvertFactory();

    public static Converter.Factory create() {
        return INSTANCE;
    }

    @Nullable @Override
    public Converter<ResponseBody, ?> responseBodyConverter(@NotNull Type type, Annotation @NotNull [] annotations, @NotNull Retrofit retrofit) {
        return body -> JSON.parseObject(body.bytes(), type, JSONReader.Feature.SupportSmartMatch);
    }

    @Nullable @Override
    public Converter<?, RequestBody> requestBodyConverter(@NotNull Type type, Annotation @NotNull [] parameterAnnotations, Annotation @NotNull [] methodAnnotations, @NotNull Retrofit retrofit) {
        return body -> RequestBody.create(JSON.toJSONBytes(body), APPLICATION_JSON_UTF8_VALUE);
    }
}
