package pro.fessional.wings.slardar.httprest.okhttp;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
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
import java.util.List;
import java.util.function.BiFunction;

/**
 * Who uses it, who should close it, in a try-close pattern.
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
     * global static client
     */
    @NotNull
    public static OkHttpClient staticClient() {
        return DefaultClientHolder.DefaultClient;
    }

    protected static OkHttpClient SpringClient;

    /**
     * inject Spring Configured client
     */
    @NotNull
    public static OkHttpClient springClient() {
        return SpringClient != null ? SpringClient : staticClient();
    }

    //
    public static final RequestBody EMPTY = RequestBody.create("", OkHttpMediaType.ALL_VALUE);

    @NotNull
    public static MultipartBody.Builder postFile(@NotNull String key, @NotNull File file) {
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(key, file.getName(), RequestBody.create(file, OkHttpMediaType.MULTIPART_FORM_DATA_VALUE));
    }

    @NotNull
    public static MultipartBody.Builder postFile(@NotNull String key, byte @NotNull [] file, @NotNull String fileName) {
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(key, fileName, RequestBody.create(file, OkHttpMediaType.MULTIPART_FORM_DATA_VALUE));
    }

    @NotNull
    public static MultipartBody.Builder postFile(@NotNull String key, @NotNull InputStream file, @NotNull String fileName) {
        return postFile(key, InputStreams.readBytes(file), fileName);
    }

    @NotNull
    public static String postFile(@NotNull OkHttpClient client, @NotNull String url, @NotNull String key, @NotNull File file) {
        return postFile((Call.Factory) client, url, postFile(key, file).build());
    }

    @NotNull
    public static String postFile(@NotNull Call.Factory callFactory, @NotNull String url, @NotNull String key, @NotNull File file) {
        return postFile(callFactory, url, postFile(key, file).build());
    }

    @NotNull
    public static String postFile(@NotNull OkHttpClient client, @NotNull String url, @NotNull String key, byte @NotNull [] file, @NotNull String fileName) {
        return postFile((Call.Factory) client, url, postFile(key, file, fileName).build());
    }

    @NotNull
    public static String postFile(@NotNull Call.Factory callFactory, @NotNull String url, @NotNull String key, byte @NotNull [] file, @NotNull String fileName) {
        return postFile(callFactory, url, postFile(key, file, fileName).build());
    }

    @NotNull
    public static String postFile(@NotNull OkHttpClient client, @NotNull String url, @NotNull String key, @NotNull InputStream file, @NotNull String fileName) {
        return postFile((Call.Factory) client, url, postFile(key, file, fileName).build());
    }

    @NotNull
    public static String postFile(@NotNull Call.Factory callFactory, @NotNull String url, @NotNull String key, @NotNull InputStream file, @NotNull String fileName) {
        return postFile(callFactory, url, postFile(key, file, fileName).build());
    }

    @NotNull
    public static String postFile(@NotNull OkHttpClient client, @NotNull String url, @NotNull MultipartBody body) {
        return postFile((Call.Factory) client, url, body);
    }

    @NotNull
    public static String postFile(@NotNull Call.Factory callFactory, @NotNull String url, @NotNull MultipartBody body) {
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
    public static String postJson(@NotNull OkHttpClient client, @NotNull String url, @Nullable CharSequence json) {
        return executeJson(client, url, json, "POST");
    }

    @NotNull
    public static String postJson(@NotNull Call.Factory callFactory, @NotNull String url, @Nullable CharSequence json) {
        return executeJson(callFactory, url, json, "POST");
    }

    @Nullable
    public static ResponseBody extract(@Nullable Response response) {
        return response == null ? null : response.body();
    }

    @NotNull
    public static String extractString(@Nullable Response response) throws IOException {
        ResponseBody body = extract(response);
        return extractString(body);
    }

    @Nullable
    @Contract("_,false->!null")
    public static String extractString(@Nullable Response response, boolean nullWhenThrow) {
        ResponseBody body = extract(response);
        return extractString(body, nullWhenThrow);
    }

    @NotNull
    public static String extractString(@Nullable ResponseBody body) throws IOException {
        if (body == null) return Null.Str;
        return body.string();
    }

    @Nullable
    @Contract("_,false->!null")
    public static String extractString(@Nullable ResponseBody body, boolean nullWhenThrow) {
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

    public static byte @NotNull [] download(@NotNull OkHttpClient client, @NotNull String url) {
        return download((Call.Factory) client, url, "GET");
    }

    public static byte @NotNull [] download(@NotNull Call.Factory callFactory, @NotNull String url) {
        return download(callFactory, url, "GET");
    }

    public static byte @NotNull [] download(@NotNull OkHttpClient client, @NotNull String url, @NotNull String method) {
        return download((Call.Factory) client, url, method);
    }

    /**
     * return empty body or null according to method
     *
     * @see HttpMethod#requiresRequestBody(String)
     */
    @Nullable
    public static RequestBody emptyBody(@NotNull String method) {
        return HttpMethod.requiresRequestBody(method) ? EMPTY : null;
    }

    public static byte @NotNull [] download(@NotNull Call.Factory callFactory, @NotNull String url, @NotNull String method) {
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
    public static String getText(@NotNull OkHttpClient client, @NotNull String url) {
        return getText((Call.Factory) client, url);
    }

    @NotNull
    public static String getText(@NotNull Call.Factory callFactory, @NotNull String url) {
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
    public static String executeString(@NotNull OkHttpClient client, @NotNull Request request, boolean nullWhenThrow) {
        return executeString((Call.Factory) client, request, nullWhenThrow);
    }

    @Contract("_,_,false->!null")
    public static String executeString(@NotNull Call.Factory callFactory, @NotNull Request request, boolean nullWhenThrow) {

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

    public static String executeJson(@NotNull OkHttpClient client, @NotNull String url, @Nullable CharSequence json, @NotNull String method) {
        return executeJson((Call.Factory) client, url, json, method);
    }

    public static String executeJson(@NotNull Call.Factory callFactory, @NotNull String url, @Nullable CharSequence json, @NotNull String method) {
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
    public static Response execute(@NotNull OkHttpClient client, @NotNull Request request) throws IOException {
        return execute((Call.Factory) client, request);
    }

    @NotNull
    public static Response execute(@NotNull Call.Factory callFactory, @NotNull Request request) throws IOException {
        return callFactory.newCall(request).execute();
    }

    @Contract("_,_,false->!null")
    public static Response execute(@NotNull OkHttpClient client, @NotNull Request request, boolean nullWhenThrow) {
        return execute((Call.Factory) client, request, nullWhenThrow);
    }

    @Contract("_,_,false->!null")
    public static Response execute(@NotNull Call.Factory callFactory, @NotNull Request request, boolean nullWhenThrow) {
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
    public static <T> T execute(@NotNull OkHttpClient client, @NotNull Request request, @NotNull BiFunction<Response, IOException, T> fun) {
        return execute((Call.Factory) client, request, fun);
    }

    @Nullable
    public static <T> T execute(@NotNull Call.Factory callFactory, @NotNull Request request, @NotNull BiFunction<Response, IOException, T> fun) {
        try (final Response res = execute(callFactory, request)) {
            return fun.apply(res, null);
        }
        catch (IOException e) {
            return fun.apply(null, e);
        }
    }

    @NotNull
    public static Response execute(@NotNull OkHttpClient client, @NotNull Request.Builder builder) throws IOException {
        return execute((Call.Factory) client, builder);
    }

    @NotNull
    public static Response execute(@NotNull Call.Factory callFactory, @NotNull Request.Builder builder) throws IOException {
        if (callFactory instanceof OkHttpBuildableClient) {
            return ((OkHttpBuildableClient) callFactory).newCall(builder).execute();
        }
        else {
            return callFactory.newCall(builder.build()).execute();
        }
    }

    @Contract("_,_,false->!null")
    public static Response execute(@NotNull OkHttpClient client, @NotNull Request.Builder builder, boolean nullWhenThrow) {
        return execute((Call.Factory) client, builder, nullWhenThrow);
    }

    @Contract("_,_,false->!null")
    public static Response execute(@NotNull Call.Factory callFactory, @NotNull Request.Builder builder, boolean nullWhenThrow) {
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
    public static <T> T execute(@NotNull OkHttpClient client, @NotNull Request.Builder builder, @NotNull BiFunction<Response, IOException, T> fun) {
        return execute((Call.Factory) client, builder, fun);
    }

    public static <T> T execute(@NotNull Call.Factory callFactory, @NotNull Request.Builder builder, @NotNull BiFunction<Response, IOException, T> fun) {
        try (final Response res = execute(callFactory, builder)) {
            return fun.apply(res, null);
        }
        catch (IOException e) {
            return fun.apply(null, e);
        }
    }

    public static void clearCookie(@NotNull OkHttpClient client, @NotNull HttpUrl url) {
        final CookieJar cookieJar = client.cookieJar();
        final List<Cookie> list = cookieJar.loadForRequest(url).stream().map(it -> {
            final Cookie.Builder builder = new Cookie.Builder()
                    .name(it.name())
                    .path(it.path())
                    .domain(it.domain())
                    .value(it.value())
                    .expiresAt(0);
            if (it.secure()) builder.secure();
            if (it.httpOnly()) builder.httpOnly();
            return builder.build();
        }).toList();
        cookieJar.saveFromResponse(url, list);
    }
}
