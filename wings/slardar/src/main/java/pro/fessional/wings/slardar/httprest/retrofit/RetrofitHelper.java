package pro.fessional.wings.slardar.httprest.retrofit;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import pro.fessional.wings.slardar.jackson.JacksonHelper;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * @author trydofor
 * @since 2023-03-06
 */
public class RetrofitHelper {

    public static <T> T jacksonPlain(Class<T> caller, String url, OkHttpClient client) {
        return jackson(caller, url, (Call.Factory) client, JacksonHelper.JsonPlain);
    }

    /**
     * use JacksonHelper.JsonDefault as JacksonConverterFactory
     */
    public static <T> T jacksonPlain(Class<T> caller, String url, Call.Factory factory) {
        return jackson(caller, url, factory, JacksonHelper.JsonPlain);
    }

    public static <T> T jacksonWings(Class<T> caller, String url, OkHttpClient client) {
        return jackson(caller, url, (Call.Factory) client, JacksonHelper.JsonWings());
    }

    /**
     * use JacksonHelper.JsonDefault as JacksonConverterFactory
     */
    public static <T> T jacksonWings(Class<T> caller, String url, Call.Factory factory) {
        return jackson(caller, url, factory, JacksonHelper.JsonWings());
    }

    public static <T> T jackson(Class<T> caller, String url, OkHttpClient client, ObjectMapper mapper) {
        return jackson(caller, url, (Call.Factory) client, mapper);
    }

    public static <T> T jackson(Class<T> caller, String url, Call.Factory factory, ObjectMapper mapper) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .callFactory(factory)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .validateEagerly(true)
                .build();
        return retrofit.create(caller);
    }
}
