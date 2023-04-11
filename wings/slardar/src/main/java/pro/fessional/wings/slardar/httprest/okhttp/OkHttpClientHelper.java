package pro.fessional.wings.slardar.httprest.okhttp;

import okhttp3.Call;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpMethod;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.io.InputStreams;
import pro.fessional.mirana.pain.IORuntimeException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiFunction;

/**
 * 保持，谁用response谁关闭，采用 try-close模式
 *
 * @author trydofor
 * @since 2020-06-02
 */
public class OkHttpClientHelper {

    // lazy initialization holder class idiom
    private static final class DefaultClientHolder {
        private static final OkHttpClient DefaultClient = OkHttpClientBuilder.staticBuilder().build();
    }

    /**
     * 静态全局的默认初始化的
     */
    @NotNull
    public static OkHttpClient staticClient() {
        return DefaultClientHolder.DefaultClient;
    }

    protected static OkHttpClient SpringClient;

    /**
     * 注入的Spring Bean
     */
    @NotNull
    public static OkHttpClient springClient() {
        return SpringClient != null ? SpringClient : staticClient();
    }

    //
    public static final RequestBody EMPTY = RequestBody.create("", OkHttpMediaType.ALL_VALUE);

    @NotNull
    public static MultipartBody.Builder postFile(String key, File file) {
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(key, file.getName(), RequestBody.create(file, OkHttpMediaType.MULTIPART_FORM_DATA_VALUE));
    }

    @NotNull
    public static MultipartBody.Builder postFile(String key, byte[] file, String fileName) {
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(key, fileName, RequestBody.create(file, OkHttpMediaType.MULTIPART_FORM_DATA_VALUE));
    }

    @NotNull
    public static MultipartBody.Builder postFile(String key, InputStream file, String fileName) {
        return postFile(key, InputStreams.readBytes(file), fileName);
    }

    @NotNull
    public static String postFile(OkHttpClient client, String url, String key, File file) {
        return postFile((Call.Factory) client, url, postFile(key, file).build());
    }

    @NotNull
    public static String postFile(Call.Factory callFactory, String url, String key, File file) {
        return postFile(callFactory, url, postFile(key, file).build());
    }

    @NotNull
    public static String postFile(OkHttpClient client, String url, String key, byte[] file, String fileName) {
        return postFile((Call.Factory) client, url, postFile(key, file, fileName).build());
    }

    @NotNull
    public static String postFile(Call.Factory callFactory, String url, String key, byte[] file, String fileName) {
        return postFile(callFactory, url, postFile(key, file, fileName).build());
    }

    @NotNull
    public static String postFile(OkHttpClient client, String url, String key, InputStream file, String fileName) {
        return postFile((Call.Factory) client, url, postFile(key, file, fileName).build());
    }

    @NotNull
    public static String postFile(Call.Factory callFactory, String url, String key, InputStream file, String fileName) {
        return postFile(callFactory, url, postFile(key, file, fileName).build());
    }

    @NotNull
    public static String postFile(OkHttpClient client, String url, MultipartBody body) {
        return postFile((Call.Factory) client, url, body);
    }

    @NotNull
    public static String postFile(Call.Factory callFactory, String url, MultipartBody body) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);
        try (Response response = execute(callFactory, builder)) {
            return extractString(response);
        }
        catch (Exception e) {
            throw new IllegalStateException("failed to post file, url=" + url, e);
        }
    }

    @NotNull
    public static String postJson(OkHttpClient client, String url, CharSequence json) {
        return executeJson(client, url, json, "POST");
    }

    @NotNull
    public static String postJson(Call.Factory callFactory, String url, CharSequence json) {
        return executeJson(callFactory, url, json, "POST");
    }

    @Nullable
    public static ResponseBody extract(Response response) {
        if (response != null && response.isSuccessful()) {
            return response.body();
        }
        return null;
    }

    @NotNull
    public static String extractString(Response response) throws IOException {
        ResponseBody body = extract(response);
        return extractString(body);
    }

    @Nullable
    @Contract("_,false->!null")
    public static String extractString(Response response, boolean nullWhenThrow) {
        ResponseBody body = extract(response);
        return extractString(body, nullWhenThrow);
    }

    @NotNull
    public static String extractString(ResponseBody body) throws IOException {
        if (body == null) return Null.Str;
        return body.string();
    }

    @Nullable
    @Contract("_,false->!null")
    public static String extractString(ResponseBody body, boolean nullWhenThrow) {
        try {
            return extractString(body);
        }
        catch (Exception e) {
            if (nullWhenThrow) {
                return null;
            }
            else {
                throw new IORuntimeException(e);
            }
        }
    }

    public static byte @NotNull [] download(OkHttpClient client, String url) {
        return download((Call.Factory) client, url, "GET");
    }

    public static byte @NotNull [] download(Call.Factory callFactory, String url) {
        return download(callFactory, url, "GET");
    }

    public static byte @NotNull [] download(OkHttpClient client, String url, String method) {
        return download((Call.Factory) client, url, method);
    }

    /**
     * return empty body or null according to method
     *
     * @see HttpMethod#requiresRequestBody(String)
     */
    @Nullable
    public static RequestBody emptyBody(String method) {
        return HttpMethod.requiresRequestBody(method) ? EMPTY : null;
    }

    public static byte @NotNull [] download(Call.Factory callFactory, String url, String method) {
        Request.Builder builder = new Request.Builder().url(url);
        builder.method(method, emptyBody(method));

        try (Response response = execute(callFactory, builder)) {
            ResponseBody body = extract(response);
            if (body == null) return Null.Bytes;
            final byte[] bytes = body.bytes();
            return bytes == null ? Null.Bytes : bytes;
        }
        catch (Exception e) {
            throw new IllegalStateException("failed to download, url=" + url, e);
        }
    }

    @NotNull
    public static String getText(OkHttpClient client, String url) {
        return getText((Call.Factory) client, url);
    }

    @NotNull
    public static String getText(Call.Factory callFactory, String url) {
        Request.Builder builder = new okhttp3.Request.Builder()
                .url(url)
                .get();
        try (Response response = execute(callFactory, builder)) {
            return extractString(response);
        }
        catch (Exception e) {
            throw new IllegalStateException("failed to post file, url=" + url, e);
        }
    }

    @Contract("_,_,false->!null")
    public static String executeString(OkHttpClient client, Request request, boolean nullWhenThrow) {
        return executeString((Call.Factory) client, request, nullWhenThrow);
    }

    @Contract("_,_,false->!null")
    public static String executeString(Call.Factory callFactory, Request request, boolean nullWhenThrow) {

        try (Response response = execute(callFactory, request)) {
            return extractString(response);
        }
        catch (Exception e) {
            if (nullWhenThrow) {
                return null;
            }
            else {
                throw new IORuntimeException(e);
            }
        }
    }

    public static String executeJson(OkHttpClient client, String url, CharSequence json, String method) {
        return executeJson((Call.Factory) client, url, json, method);
    }

    public static String executeJson(Call.Factory callFactory, String url, CharSequence json, String method) {
        okhttp3.RequestBody body = json == null ? null : RequestBody.create(json.toString(), OkHttpMediaType.APPLICATION_JSON_VALUE);
        Request.Builder builder = new okhttp3.Request.Builder()
                .url(url)
                .method(method, body);
        try (Response response = execute(callFactory, builder)) {
            return extractString(response);
        }
        catch (Exception e) {
            throw new IllegalStateException("failed to post file, url=" + url, e);
        }
    }

    @NotNull
    public static Response execute(OkHttpClient client, Request request) throws IOException {
        return execute((Call.Factory) client, request);
    }

    @NotNull
    public static Response execute(Call.Factory callFactory, Request request) throws IOException {
        return callFactory.newCall(request).execute();
    }

    @Contract("_,_,false->!null")
    public static Response execute(OkHttpClient client, Request request, boolean nullWhenThrow) {
        return execute((Call.Factory) client, request, nullWhenThrow);
    }

    @Contract("_,_,false->!null")
    public static Response execute(Call.Factory callFactory, Request request, boolean nullWhenThrow) {
        try {
            return execute(callFactory, request);
        }
        catch (IOException e) {
            if (nullWhenThrow) {
                return null;
            }
            else {
                throw new IORuntimeException(e);
            }
        }
    }

    @Nullable
    public static <T> T execute(OkHttpClient client, Request request, BiFunction<Response, IOException, T> fun) {
        return execute((Call.Factory) client, request, fun);
    }

    @Nullable
    public static <T> T execute(Call.Factory callFactory, Request request, BiFunction<Response, IOException, T> fun) {
        try (final Response res = execute(callFactory, request)) {
            return fun.apply(res, null);
        }
        catch (IOException e) {
            return fun.apply(null, e);
        }
    }

    @NotNull
    public static Response execute(OkHttpClient client, Request.Builder builder) throws IOException {
        return execute((Call.Factory) client, builder);
    }

    @NotNull
    public static Response execute(Call.Factory callFactory, Request.Builder builder) throws IOException {
        if (callFactory instanceof OkHttpBuildableClient) {
            return ((OkHttpBuildableClient) callFactory).newCall(builder).execute();
        }
        else {
            return callFactory.newCall(builder.build()).execute();
        }
    }

    @Contract("_,_,false->!null")
    public static Response execute(OkHttpClient client, Request.Builder builder, boolean nullWhenThrow) {
        return execute((Call.Factory) client, builder, nullWhenThrow);
    }

    @Contract("_,_,false->!null")
    public static Response execute(Call.Factory callFactory, Request.Builder builder, boolean nullWhenThrow) {
        try {
            return execute(callFactory, builder);
        }
        catch (IOException e) {
            if (nullWhenThrow) {
                return null;
            }
            else {
                throw new IORuntimeException(e);
            }
        }
    }

    @Nullable
    public static <T> T execute(OkHttpClient client, Request.Builder builder, BiFunction<Response, IOException, T> fun) {
        return execute((Call.Factory) client, builder, fun);
    }

    public static <T> T execute(Call.Factory callFactory, Request.Builder builder, BiFunction<Response, IOException, T> fun) {
        try (final Response res = execute(callFactory, builder)) {
            return fun.apply(res, null);
        }
        catch (IOException e) {
            return fun.apply(null, e);
        }
    }

}
