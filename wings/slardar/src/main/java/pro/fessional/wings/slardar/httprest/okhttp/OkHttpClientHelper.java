package pro.fessional.wings.slardar.httprest.okhttp;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
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
        return postFile(client, url, postFile(key, file).build());
    }

    @NotNull
    public static String postFile(OkHttpClient client, String url, String key, byte[] file, String fileName) {
        return postFile(client, url, postFile(key, file, fileName).build());
    }

    @NotNull
    public static String postFile(OkHttpClient client, String url, String key, InputStream file, String fileName) {
        return postFile(client, url, postFile(key, file, fileName).build());
    }

    @NotNull
    public static String postFile(OkHttpClient client, String url, MultipartBody body) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);
        try (Response response = execute(client, builder)) {
            return extractString(response);
        }
        catch (Exception e) {
            throw new IllegalStateException("failed to post file, url=" + url, e);
        }
    }

    @NotNull
    public static String postJson(OkHttpClient client, String url, CharSequence json) {
        okhttp3.RequestBody body = RequestBody.create(json.toString(), OkHttpMediaType.APPLICATION_JSON_VALUE);
        Request.Builder builder = new okhttp3.Request.Builder()
                .url(url)
                .post(body);
        try (Response response = execute(client, builder)) {
            return extractString(response);
        }
        catch (Exception e) {
            throw new IllegalStateException("failed to post file, url=" + url, e);
        }
    }

    @Nullable
    public static ResponseBody extract(Response response) {
        if (response.isSuccessful()) {
            return response.body();
        }
        return null;
    }

    @NotNull
    public static String extractString(Response response) throws IOException {
        ResponseBody body = extract(response);
        if (body == null) return Null.Str;
        return body.string();
    }

    @Nullable
    @Contract("_,false->!null")
    public static String extractString(Response response, boolean nullWhenThrow) {
        try {
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


    public static byte @NotNull [] download(OkHttpClient client, String url) {
        return download(client, url, "GET");
    }

    public static byte @NotNull [] download(OkHttpClient client, String url, String method) {
        Request.Builder builder = new Request.Builder().url(url);
        if ("GET".equalsIgnoreCase(method)) {
            builder.get();
        }
        else if ("POST".equalsIgnoreCase(method)) {
            builder.post(EMPTY);
        }
        else if ("PUT".equalsIgnoreCase(method)) {
            builder.put(EMPTY);
        }
        else if ("HEAD".equalsIgnoreCase(method)) {
            builder.head();
        }
        else if ("PATCH".equalsIgnoreCase(method)) {
            builder.patch(EMPTY);
        }
        else if ("DELETE".equalsIgnoreCase(method)) {
            builder.delete();
        }

        try (Response response = execute(client, builder)) {
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
        Request.Builder builder = new okhttp3.Request.Builder()
                .url(url)
                .get();
        try (Response response = execute(client, builder)) {
            return extractString(response);
        }
        catch (Exception e) {
            throw new IllegalStateException("failed to post file, url=" + url, e);
        }
    }

    @Contract("_,_,false->!null")
    public static String executeString(OkHttpClient client, Request request, boolean nullWhenThrow) {

        try (Response response = execute(client, request)) {
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

    @NotNull
    public static Response execute(OkHttpClient client, Request request) throws IOException {
        return client.newCall(request).execute();
    }


    @Contract("_,_,false->!null")
    public static Response execute(OkHttpClient client, Request request, boolean nullWhenThrow) {
        try {
            return execute(client, request);
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
        try (final Response res = execute(client, request)) {
            return fun.apply(res, null);
        }
        catch (IOException e) {
            return fun.apply(null, e);
        }
    }

    @NotNull
    public static Response execute(OkHttpClient client, Request.Builder builder) throws IOException {
        if (client instanceof OkHttpBuildableClient) {
            return ((OkHttpBuildableClient) client).newCall(builder).execute();
        }
        else {
            return client.newCall(builder.build()).execute();
        }
    }

    @Contract("_,_,false->!null")
    public static Response execute(OkHttpClient client, Request.Builder builder, boolean nullWhenThrow) {
        try {
            return execute(client, builder);
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
        try (final Response res = execute(client, builder)) {
            return fun.apply(res, null);
        }
        catch (IOException e) {
            return fun.apply(null, e);
        }
    }

}
