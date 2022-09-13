package pro.fessional.wings.slardar.httprest;

import lombok.Data;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
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
import pro.fessional.mirana.netx.SslTrustAll;
import pro.fessional.mirana.pain.IORuntimeException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * 保持，谁用response谁关闭，采用 try-close模式
 *
 * @author trydofor
 * @since 2020-06-02
 */
public class OkHttpClientHelper {

    public static final MediaType ALL_VALUE = MediaType.parse("*/*");
    public static final MediaType APPLICATION_ATOM_XML_VALUE = MediaType.parse("application/atom+xml");
    public static final MediaType APPLICATION_CBOR_VALUE = MediaType.parse("application/cbor");
    public static final MediaType APPLICATION_FORM_URLENCODED_VALUE = MediaType.parse("application/x-www-form-urlencoded");
    public static final MediaType APPLICATION_JSON_VALUE = MediaType.parse("application/json");
    public static final MediaType APPLICATION_JSON_UTF8_VALUE = MediaType.parse("application/json;charset=UTF-8");
    public static final MediaType APPLICATION_OCTET_STREAM_VALUE = MediaType.parse("application/octet-stream");
    public static final MediaType APPLICATION_PDF_VALUE = MediaType.parse("application/pdf");
    public static final MediaType APPLICATION_PROBLEM_JSON_VALUE = MediaType.parse("application/problem+json");
    public static final MediaType APPLICATION_PROBLEM_JSON_UTF8_VALUE = MediaType.parse("application/problem+json;charset=UTF-8");
    public static final MediaType APPLICATION_PROBLEM_XML_VALUE = MediaType.parse("application/problem+xml");
    public static final MediaType APPLICATION_RSS_XML_VALUE = MediaType.parse("application/rss+xml");
    public static final MediaType APPLICATION_STREAM_JSON_VALUE = MediaType.parse("application/stream+json");
    public static final MediaType APPLICATION_XHTML_XML_VALUE = MediaType.parse("application/xhtml+xml");
    public static final MediaType APPLICATION_XML_VALUE = MediaType.parse("application/xml");
    public static final MediaType IMAGE_GIF_VALUE = MediaType.parse("image/gif");
    public static final MediaType IMAGE_JPEG_VALUE = MediaType.parse("image/jpeg");
    public static final MediaType IMAGE_PNG_VALUE = MediaType.parse("image/png");
    public static final MediaType MULTIPART_FORM_DATA_VALUE = MediaType.parse("multipart/form-data");
    public static final MediaType MULTIPART_MIXED_VALUE = MediaType.parse("multipart/mixed");
    public static final MediaType MULTIPART_RELATED_VALUE = MediaType.parse("multipart/related");
    public static final MediaType TEXT_EVENT_STREAM_VALUE = MediaType.parse("text/event-stream");
    public static final MediaType TEXT_HTML_VALUE = MediaType.parse("text/html");
    public static final MediaType TEXT_MARKDOWN_VALUE = MediaType.parse("text/markdown");
    public static final MediaType TEXT_PLAIN_VALUE = MediaType.parse("text/plain");
    public static final MediaType TEXT_XML_VALUE = MediaType.parse("text/xml");

    public static final RequestBody EMPTY = RequestBody.create("", ALL_VALUE);

    @NotNull
    public static MultipartBody.Builder postFile(String key, File file) {
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(key, file.getName(), RequestBody.create(file, MULTIPART_FORM_DATA_VALUE));
    }

    @NotNull
    public static MultipartBody.Builder postFile(String key, byte[] file, String fileName) {
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(key, fileName, RequestBody.create(file, MULTIPART_FORM_DATA_VALUE));
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
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return extractString(response);
        }
        catch (Exception e) {
            throw new IllegalStateException("failed to post file, url=" + url, e);
        }
    }

    @NotNull
    public static String postJson(OkHttpClient client, String url, CharSequence json) {
        okhttp3.RequestBody body = RequestBody.create(json.toString(), APPLICATION_JSON_VALUE);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
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

        try (Response response = client.newCall(builder.build()).execute()) {
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
    public static Response execute(OkHttpClient client, Request.Builder builder) throws IOException {
        return client.newCall(builder.build()).execute();
    }

    @Nullable
    @Contract("_,_,false->!null")
    public static Response execute(OkHttpClient client, Request.Builder builder, boolean nullWhenThrow) {
        try {
            return client.newCall(builder.build()).execute();
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
        try (final Response res = client.newCall(builder.build()).execute()) {
            return fun.apply(res, null);
        }
        catch (IOException e) {
            return fun.apply(null, e);
        }
    }

    public static void sslTrustAll(OkHttpClient.Builder builder) {
        builder.sslSocketFactory(SslTrustAll.SSL_SOCKET_FACTORY, SslTrustAll.X509_TRUST_MANAGER)
               .hostnameVerifier(SslTrustAll.HOSTNAME_VERIFIER);
    }

    public static void hostCookieJar(OkHttpClient.Builder builder) {
        builder.cookieJar(new HostCookieJar());
    }

    public static class HostCookieJar implements CookieJar {

        private final Map<String, Map<Ckk, Cookie>> cookies = new ConcurrentHashMap<>();

        @Override
        public void saveFromResponse(HttpUrl url, @NotNull List<Cookie> cks) {
            Map<Ckk, Cookie> cookies = this.cookies.computeIfAbsent(url.host(), s -> new LinkedHashMap<>());
            for (Cookie ck : cks) {
                Ckk k = new Ckk();
                k.setHost(ck.domain());
                k.setPath(ck.path());
                k.setName(ck.name());
                k.setSecure(ck.secure());
                //
                cookies.remove(k);
                cookies.put(k, ck);
            }
        }

        @Override
        @NotNull
        public List<Cookie> loadForRequest(HttpUrl url) {
            Map<Ckk, Cookie> cookies = this.cookies.get(url.host());
            if (cookies == null) return Collections.emptyList();
            return cookies.values().stream()
                          .filter(it -> it.matches(url))
                          .collect(Collectors.toList());
        }
    }

    @Data
    private static class Ckk {
        private String host;
        private String path;
        private String name;
        private boolean secure;
    }
}
