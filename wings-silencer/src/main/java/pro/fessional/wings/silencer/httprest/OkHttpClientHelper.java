package pro.fessional.wings.silencer.httprest;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpMethod;
import pro.fessional.mirana.data.Nulls;
import pro.fessional.mirana.io.InputStreams;
import pro.fessional.mirana.netx.SslTrustAll;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author trydofor
 * @since 2020-06-02
 */
public class OkHttpClientHelper {

    public static final MediaType ALL_VALUE = MediaType.parse("*/*");
    public static final MediaType APPLICATION_ATOM_XML_VALUE = MediaType.parse("application/atom+xml");
    public static final MediaType APPLICATION_CBOR_VALUE = MediaType.parse("application/cbor");
    public static final MediaType APPLICATION_FORM_URLENCODED_VALUE = MediaType.parse("application/x-www-form-urlencoded");
    public static final MediaType APPLICATION_JSON_VALUE = MediaType.parse("application/json");
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

    public static final RequestBody EMPTY = RequestBody.create(ALL_VALUE, "");

    @NotNull
    public static MultipartBody.Builder postFile(String key, File file) {
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(key, file.getName(), RequestBody.create(MULTIPART_FORM_DATA_VALUE, file));
    }

    @NotNull
    public static MultipartBody.Builder postFile(String key, byte[] file, String fileName) {
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(key, fileName, RequestBody.create(MULTIPART_FORM_DATA_VALUE, file));
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
        try {
            Response response = client.newCall(request).execute();
            return extractString(response);
        } catch (Exception e) {
            throw new IllegalStateException("failed to post file, url=" + url, e);
        }
    }

    @NotNull
    public static String postJson(OkHttpClient client, String url, CharSequence json) {
        okhttp3.RequestBody body = RequestBody.create(APPLICATION_JSON_VALUE, json.toString());
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return extractString(response);
        } catch (Exception e) {
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
        if (body == null) return Nulls.Str;
        return body.string();
    }

    @NotNull
    public static byte[] download(OkHttpClient client, String url) {
        return download(client, url, HttpMethod.GET);
    }

    @NotNull
    public static byte[] download(OkHttpClient client, String url, HttpMethod method) {
        Request.Builder builder = new Request.Builder().url(url);
        switch (method) {
            case GET:
                builder.get();
                break;
            case POST:
                builder.post(EMPTY);
                break;
            case PUT:
                builder.put(EMPTY);
                break;
            case HEAD:
                builder.head();
                break;
            case PATCH:
                builder.patch(EMPTY);
                break;
            case DELETE:
                builder.delete();
                break;
            default:
        }

        try {
            Response response = client.newCall(builder.build()).execute();
            ResponseBody body = extract(response);
            return body == null ? Nulls.Bytes : body.bytes();
        } catch (Exception e) {
            throw new IllegalStateException("failed to download, url=" + url, e);
        }
    }

    public static OkHttpClient sslTrustAll(OkHttpClient.Builder builder) {
        return builder.sslSocketFactory(SslTrustAll.SSL_SOCKET_FACTORY, SslTrustAll.X509_TRUST_MANAGER)
                      .hostnameVerifier(SslTrustAll.HOSTNAME_VERIFIER)
                      .build();
    }
}
