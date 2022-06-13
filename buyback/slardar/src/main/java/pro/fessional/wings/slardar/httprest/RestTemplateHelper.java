package pro.fessional.wings.slardar.httprest;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import pro.fessional.mirana.data.Null;

import java.io.File;
import java.io.InputStream;

/**
 * 你搜到的大部分 post file的资料，都不标准，或不对。
 *
 * @author trydofor
 * @since 2020-06-02
 */
public class RestTemplateHelper {

    public static final ResponseErrorHandler NopErrorHandler = new ResponseErrorHandler() {
        @Override
        public boolean hasError(@NotNull ClientHttpResponse response) {
            return false;
        }

        @Override
        public void handleError(@NotNull ClientHttpResponse response) {
        }
    };

    @NotNull
    public static HttpHeaders header(MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setConnection("Keep-Alive");
        headers.setCacheControl("no-cache");
        return headers;
    }

    @NotNull
    public static <T> MultiValueMap<String, T> body() {
        return new LinkedMultiValueMap<>();
    }

    @NotNull
    public static <T> MultiValueMap<String, T> body(@Nullable HttpEntity<MultiValueMap<String, T>> entity) {
        MultiValueMap<String, T> body = entity == null ? null : entity.getBody();
        return body == null ? new LinkedMultiValueMap<>() : body;
    }

    @NotNull
    public static HttpHeaders jsonHeader() {
        return header(MediaType.APPLICATION_JSON);
    }

    @NotNull
    public static HttpEntity<String> jsonEntity(String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(json, headers);
    }

    @NotNull
    public static <T> HttpEntity<T> jsonEntity(T obj) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(obj, headers);
    }

    @NotNull
    public static HttpHeaders formHeader() {
        return header(MediaType.APPLICATION_FORM_URLENCODED);
    }

    @NotNull
    public static HttpEntity<MultiValueMap<String, String>> formEntity() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        return new HttpEntity<>(map, formHeader());
    }

    @NotNull
    public static HttpHeaders fileHeader() {
        return header(MediaType.MULTIPART_FORM_DATA);
    }

    @NotNull
    public static HttpEntity<MultiValueMap<String, Object>> fileEntity() {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        return new HttpEntity<>(map, fileHeader());
    }

    @NotNull
    public static MultiValueMap<String, Object> addFile(HttpEntity<MultiValueMap<String, Object>> entity, String key, File file) {
        return addFile(entity, key, new FileSystemResource(file), file.getName());
    }

    @NotNull
    public static MultiValueMap<String, Object> addFile(HttpEntity<MultiValueMap<String, Object>> entity, String key, byte[] file, String fileName) {
        return addFile(entity, key, new ByteArrayResource(file), fileName);
    }

    @NotNull
    public static MultiValueMap<String, Object> addFile(HttpEntity<MultiValueMap<String, Object>> entity, String key, InputStream file, String fileName) {
        return addFile(entity, key, new InputStreamResource(file), fileName);
    }

    /**
     * @link https://medium.com/red6-es/uploading-a-file-with-a-filename-with-spring-resttemplate-8ec5e7dc52ca
     */
    @NotNull
    public static MultiValueMap<String, Object> addFile(HttpEntity<MultiValueMap<String, Object>> entity, String key, Resource res, String fileName) {
        MultiValueMap<String, Object> body = body(entity);

        // This nested HttpEntiy is important to create the correct
        // Content-Disposition entry with metadata "name" and "filename"
        MultiValueMap<String, String> file = new LinkedMultiValueMap<>();
        ContentDisposition contentDisposition = ContentDisposition
                .builder("form-data")
                .name(key)
                .filename(fileName)
                .build();
        file.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());

        body.add(key, new HttpEntity<>(res, file));
        return body;
    }


    public static byte @NotNull [] download(RestTemplate tmpl, String url) {
        return download(tmpl, url, HttpMethod.GET);
    }

    public static byte @NotNull [] download(RestTemplate tmpl, String url, HttpMethod method) {
        HttpEntity<String> entity = new HttpEntity<>(header(MediaType.APPLICATION_OCTET_STREAM));
        if (method == null) method = HttpMethod.GET;
        ResponseEntity<byte[]> res = tmpl.exchange(url, method, entity, byte[].class);
        byte[] arr = extract(res);
        return arr == null ? Null.Bytes : arr;
    }

    @Nullable
    public static <T> T extract(ResponseEntity<T> res) {
        if (res.getStatusCode().is2xxSuccessful()) {
            return res.getBody();
        }
        return null;
    }
}
