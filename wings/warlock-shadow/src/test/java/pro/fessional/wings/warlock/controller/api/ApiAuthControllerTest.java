package pro.fessional.wings.warlock.controller.api;

import com.alibaba.fastjson2.JSON;
import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ContentDisposition;
import pro.fessional.mirana.bits.HmacHelp;
import pro.fessional.mirana.bits.MdHelp;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.text.FormatUtil;
import pro.fessional.wings.slardar.context.Now;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpClientHelper;
import pro.fessional.wings.warlock.app.service.TestWatchingService;
import pro.fessional.wings.warlock.service.auth.WarlockOauthService;
import pro.fessional.wings.warlock.spring.prop.WarlockApiAuthProp;
import pro.fessional.wings.warlock.spring.prop.WarlockUrlmapProp;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static pro.fessional.wings.slardar.httprest.okhttp.OkHttpMediaType.APPLICATION_JSON_VALUE;
import static pro.fessional.wings.slardar.httprest.okhttp.OkHttpMediaType.MULTIPART_FORM_DATA_VALUE;
import static pro.fessional.wings.warlock.app.controller.TestToyApiController.ApiSimple;
import static pro.fessional.wings.warlock.app.controller.TestToyApiController.ModFileFile;
import static pro.fessional.wings.warlock.app.controller.TestToyApiController.ModJsonFile;
import static pro.fessional.wings.warlock.app.controller.TestToyApiController.ModJsonJson;
import static pro.fessional.wings.warlock.app.controller.TestToyApiController.ReqFileBody;
import static pro.fessional.wings.warlock.app.controller.TestToyApiController.ReqFileKey;
import static pro.fessional.wings.warlock.app.controller.TestToyApiController.ReqFileName;
import static pro.fessional.wings.warlock.app.controller.TestToyApiController.ReqJsonBody;
import static pro.fessional.wings.warlock.app.controller.TestToyApiController.ReqMethod;
import static pro.fessional.wings.warlock.app.controller.TestToyApiController.ResFileBody;
import static pro.fessional.wings.warlock.app.controller.TestToyApiController.ResFileName;
import static pro.fessional.wings.warlock.app.controller.TestToyApiController.ResJsonBody;
import static pro.fessional.wings.warlock.app.controller.TestToyApiController.TerUserId;

/**
 * @author trydofor
 * @since 2022-11-16
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "wings.warlock.apiauth.must-signature=false",
                "wings.enabled.slardar.restream=false",
        })
@Slf4j
class ApiAuthControllerTest {

    @Setter(onMethod_ = {@Autowired})
    private WarlockApiAuthProp apiAuthProp;

    @Setter(onMethod_ = {@Autowired})
    private WarlockUrlmapProp urlmapProp;

    @Setter(onMethod_ = {@Value("${local.server.port}")})
    private int apiPort;

    @Setter(onMethod_ = {@Autowired})
    private OkHttpClient okHttpClient;

    private final String client = "wings-trydofor";
    private final String secret = "wings-trydofor-secret";

    // after encode %7B%22try%22%3A%20%22dofor%22%7D
    private final String jsonBody = "{\"try\": \"dofor\"}";
    private final String fileKey = "file1";
    private final String fileSum = fileKey + ".sum";
    private final String fileName = "豆腐.exe";
    private final String fileBody = "try and do this for that";
    private final String userId = "79";

    private final Function<String, String> h256 = HmacHelp.sha256(secret.getBytes(UTF_8))::sum;
    private final Function<String, String> md5 = MdHelp.md5::sum;
    private final Function<String, String> sha1 = MdHelp.sha1::sum;

    private HttpUrl.Builder urlBuilder() {
        return new HttpUrl.Builder()
                .scheme("http")
                .host("localhost")
                .port(apiPort)
                .encodedPath(ApiSimple);
    }

    private String accessToken = null;

    private String getToken() {
        if (accessToken != null) return accessToken;
        final HttpUrl.Builder u1 = new HttpUrl.Builder()
                .scheme("http")
                .host("localhost")
                .port(apiPort)
                .encodedPath(urlmapProp.getOauthAuthorize())
                .addQueryParameter(WarlockOauthService.ClientId, client);
        final String t1 = OkHttpClientHelper.getText(okHttpClient, u1.toString());
        final String code = JSON.parseObject(t1).getString(WarlockOauthService.Code);

        final HttpUrl.Builder u2 = new HttpUrl.Builder()
                .scheme("http")
                .host("localhost")
                .port(apiPort)
                .encodedPath(urlmapProp.getOauthAccessToken())
                .addQueryParameter(WarlockOauthService.ClientId, client)
                .addQueryParameter(WarlockOauthService.ClientSecret, secret)
                .addQueryParameter(WarlockOauthService.Code, code);

        final String t2 = OkHttpClientHelper.postJson(okHttpClient, u2.toString(), "");
        accessToken = JSON.parseObject(t2).getString(WarlockOauthService.AccessToken);
        return accessToken;
    }

    @Test
    @TmsLink("C14024")
    public void testJsonJson() throws IOException {
        String[] clients = {client, getToken()};
        String[] timestamps = {String.valueOf(Now.millis()), Null.Str};
        for (String c : clients) {
            for (String t : timestamps) {
                jsonJson(c, t, md5);
                jsonJson(c, t, sha1);
                jsonJson(c, t, h256);
                jsonJson(c, t, null);
            }
        }
        final long uid = TestWatchingService.AsyncContext.getUserId();
        Assertions.assertEquals(userId, Long.toString(uid));
    }

    private void jsonJson(String client, String timestamp, Function<String, String> sumFun) throws IOException {
        TreeMap<String, String> param = new TreeMap<>();
        param.put(ReqMethod, ModJsonJson);
        param.put(ResJsonBody, jsonBody);

        HttpUrl.Builder ubd = urlBuilder();
        for (Map.Entry<String, String> en : param.entrySet()) {
            final String v = en.getValue();
            if (v != null) {
                ubd.addQueryParameter(en.getKey(), v);
            }
        }

        final String para = FormatUtil.sortParam(param);
        final String signature = sumFun == null
                                 ? Null.Str
                                 : sumFun.apply(para + jsonBody + secret + timestamp);

        RequestBody body = RequestBody.create(jsonBody, APPLICATION_JSON_VALUE);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(ubd.build())
                .header(apiAuthProp.getClientHeader(), client)
                .header(apiAuthProp.getTimestampHeader(), timestamp)
                .header(apiAuthProp.getSignatureHeader(), signature)
                .post(body)
                .build();

        final Response r2 = OkHttpClientHelper.execute(okHttpClient, request, false);
        Assertions.assertNotNull(r2);
        Assertions.assertEquals(userId, r2.header(TerUserId));
        Assertions.assertEquals(jsonBody, r2.header(ReqJsonBody));
        final ResponseBody resBody = r2.body();
        Assertions.assertNotNull(resBody);
        final String text = resBody.string();
        Assertions.assertEquals(jsonBody, text);

        final String stmp = r2.header(apiAuthProp.getTimestampHeader());
        if (!timestamp.isEmpty()) {
            Assertions.assertEquals(timestamp, stmp);
        }

        if (sumFun != null) {
            final String sign = r2.header(apiAuthProp.getSignatureHeader());
            Assertions.assertNotNull(sign);
            final String data1 = text + secret + stmp;

            final String sum = sumFun.apply(data1);
            Assertions.assertEquals(sign, sum);
        }
        else {
            final String sign = r2.header(apiAuthProp.getSignatureHeader());
            Assertions.assertNull(sign);
        }
    }

    @Test
    @TmsLink("C14025")
    public void testFileJson() throws IOException {
        String[] clients = {client, getToken()};
        String[] timestamps = {String.valueOf(Now.millis()), Null.Str};
        for (String c : clients) {
            for (String t : timestamps) {
                fileJson(c, t, md5, true);
                fileJson(c, t, sha1, true);
                fileJson(c, t, h256, false);
                fileJson(c, t, null, false);
            }
        }
    }

    private void fileJson(String client, String timestamp, Function<String, String> sumFun, boolean digest) throws IOException {
        TreeMap<String, String> param = new TreeMap<>();
        param.put(ReqMethod, ModJsonJson);
        param.put(ResJsonBody, jsonBody);
        if (digest) {
            param.put(fileSum, sumFun.apply(fileBody));
        }

        HttpUrl.Builder ubd = urlBuilder();
        for (Map.Entry<String, String> en : param.entrySet()) {
            final String v = en.getValue();
            if (v != null) {
                ubd.addQueryParameter(en.getKey(), v);
            }
        }

        final String para = FormatUtil.sortParam(param);
        final String signature = sumFun == null
                                 ? Null.Str
                                 : sumFun.apply(para + secret + timestamp);

        MultipartBody.Builder body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(fileKey, fileName, RequestBody.create(fileBody.getBytes(UTF_8), MULTIPART_FORM_DATA_VALUE));

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(ubd.build())
                .header(apiAuthProp.getClientHeader(), client)
                .header(apiAuthProp.getTimestampHeader(), timestamp)
                .header(apiAuthProp.getSignatureHeader(), signature)
                .post(body.build())
                .build();

        final Response r2 = OkHttpClientHelper.execute(okHttpClient, request, false);
        Assertions.assertNotNull(r2);
        Assertions.assertEquals(userId, r2.header(TerUserId));
        Assertions.assertEquals("", r2.header(ReqJsonBody));
        Assertions.assertEquals(fileKey, r2.header(ReqFileKey));
        final String rfn = r2.header(ReqFileName);
        Assertions.assertNotNull(rfn);
        Assertions.assertEquals(fileName, URLDecoder.decode(rfn, UTF_8));
        Assertions.assertEquals(fileBody, r2.header(ReqFileBody));
        final ResponseBody resBody = r2.body();
        Assertions.assertNotNull(resBody);
        final String text = resBody.string();
        Assertions.assertEquals(jsonBody, text);

        final String stmp = r2.header(apiAuthProp.getTimestampHeader());
        if (!timestamp.isEmpty()) {
            Assertions.assertEquals(timestamp, stmp);
        }

        if (sumFun != null) {
            final String sign = r2.header(apiAuthProp.getSignatureHeader());
            Assertions.assertNotNull(sign);
            final String data1 = text + secret + stmp;

            final String sum = sumFun.apply(data1);
            Assertions.assertEquals(sign, sum);
        }
        else {
            final String sign = r2.header(apiAuthProp.getSignatureHeader());
            Assertions.assertNull(sign);
        }
    }


    @Test
    @TmsLink("C14026")
    public void testJsonFile() throws IOException {
        String[] clients = {client, getToken()};
        String[] timestamps = {String.valueOf(Now.millis()), Null.Str};
        for (String c : clients) {
            for (String t : timestamps) {
                jsonFile(c, t, md5, true);
                jsonFile(c, t, sha1, true);
                jsonFile(c, t, h256, false);
                jsonFile(c, t, null, false);
            }
        }
    }

    private void jsonFile(String client, String timestamp, Function<String, String> sumFun, boolean digest) throws IOException {
        TreeMap<String, String> param = new TreeMap<>();
        param.put(ReqMethod, ModJsonFile);
        param.put(ResFileName, fileName);
        param.put(ResFileBody, fileBody);

        HttpUrl.Builder ubd = urlBuilder();
        for (Map.Entry<String, String> en : param.entrySet()) {
            final String v = en.getValue();
            if (v != null) {
                ubd.addQueryParameter(en.getKey(), v);
            }
        }

        final String para = FormatUtil.sortParam(param);
        final String signature = sumFun == null
                                 ? Null.Str
                                 : sumFun.apply(para + jsonBody + secret + timestamp);
        final String dgst = digest && sumFun != null ? sumFun.apply(jsonBody) : Null.Str;
        RequestBody body = RequestBody.create(jsonBody, APPLICATION_JSON_VALUE);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(ubd.build())
                .header(apiAuthProp.getClientHeader(), client)
                .header(apiAuthProp.getTimestampHeader(), timestamp)
                .header(apiAuthProp.getSignatureHeader(), signature)
                .header(apiAuthProp.getDigestHeader(), dgst)
                .post(body)
                .build();

        final Response r2 = OkHttpClientHelper.execute(okHttpClient, request, false);
        Assertions.assertNotNull(r2);
        Assertions.assertEquals(userId, r2.header(TerUserId));
        Assertions.assertEquals(jsonBody, r2.header(ReqJsonBody));
        final ResponseBody resBody = r2.body();
        Assertions.assertNotNull(resBody);
        final String text = resBody.string();
        Assertions.assertEquals(fileBody, text);

        final String stmp = r2.header(apiAuthProp.getTimestampHeader());
        if (!timestamp.isEmpty()) {
            Assertions.assertEquals(timestamp, stmp);
        }

        final String cd = r2.header(CONTENT_DISPOSITION);
        Assertions.assertNotNull(cd);
        ContentDisposition disposition = ContentDisposition.parse(cd);
        Assertions.assertEquals(fileName, disposition.getFilename());

        final String data1;
        if (digest && sumFun != null) {
            final String sum = sumFun.apply(fileBody);
            Assertions.assertEquals(sum, r2.header(apiAuthProp.getDigestHeader()));
            data1 = sum + secret + stmp;
        }
        else {
            data1 = secret + stmp;
        }

        if (sumFun != null) {
            final String sign = r2.header(apiAuthProp.getSignatureHeader());
            Assertions.assertNotNull(sign);

            final String sum = sumFun.apply(data1);
            Assertions.assertEquals(sign, sum);
        }
        else {
            final String sign = r2.header(apiAuthProp.getSignatureHeader());
            Assertions.assertNull(sign);
        }
    }

    @Test
    @TmsLink("C14027")
    public void testFileFile() throws IOException {
        String[] clients = {client, getToken()};
        String[] timestamps = {String.valueOf(Now.millis()), Null.Str};
        for (String c : clients) {
            for (String t : timestamps) {
                fileFile(c, t, md5, true);
                fileFile(c, t, sha1, true);
                fileFile(c, t, h256, false);
                fileFile(c, t, null, false);
            }
        }
    }

    private void fileFile(String client, String timestamp, Function<String, String> sumFun, boolean digest) throws IOException {
        TreeMap<String, String> param = new TreeMap<>();
        param.put(ReqMethod, ModFileFile);
        param.put(ResFileName, fileName);
        param.put(ResFileBody, fileBody);
        final String dgst;
        if (digest && sumFun != null) {
            dgst = sumFun.apply(fileBody);
            param.put(fileSum, dgst);
        }
        else {
            dgst = Null.Str;
        }

        final String para = FormatUtil.sortParam(param);
        param.remove(fileSum);

        HttpUrl.Builder ubd = urlBuilder();
        for (Map.Entry<String, String> en : param.entrySet()) {
            final String v = en.getValue();
            if (v != null) {
                ubd.addQueryParameter(en.getKey(), v);
            }
        }

        final String signature = sumFun == null
                                 ? Null.Str
                                 : sumFun.apply(para + jsonBody + secret + timestamp);

        MultipartBody.Builder body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(fileKey, fileName, RequestBody.create(fileBody.getBytes(UTF_8), MULTIPART_FORM_DATA_VALUE))
                .addFormDataPart(fileSum, dgst);

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(ubd.build())
                .header(apiAuthProp.getClientHeader(), client)
                .header(apiAuthProp.getTimestampHeader(), timestamp)
                .header(apiAuthProp.getSignatureHeader(), signature)
                .post(body.build())
                .build();

        final Response r2 = OkHttpClientHelper.execute(okHttpClient, request, false);
        Assertions.assertNotNull(r2);
        Assertions.assertEquals(userId, r2.header(TerUserId));
        Assertions.assertEquals(fileBody, r2.header(ReqFileBody));
        final ResponseBody resBody = r2.body();
        Assertions.assertNotNull(resBody);
        final String text = resBody.string();
        Assertions.assertEquals(fileBody, text);

        final String stmp = r2.header(apiAuthProp.getTimestampHeader());
        if (!timestamp.isEmpty()) {
            Assertions.assertEquals(timestamp, stmp);
        }

        final String cd = r2.header(CONTENT_DISPOSITION);
        Assertions.assertNotNull(cd);
        ContentDisposition disposition = ContentDisposition.parse(cd);
        Assertions.assertEquals(fileName, disposition.getFilename());

        final String data1;
        if (digest && sumFun != null) {
            final String sum = sumFun.apply(fileBody);
            Assertions.assertEquals(sum, r2.header(apiAuthProp.getDigestHeader()));
            data1 = sum + secret + stmp;
        }
        else {
            data1 = secret + stmp;
        }

        if (sumFun != null) {
            final String sign = r2.header(apiAuthProp.getSignatureHeader());
            Assertions.assertNotNull(sign);

            final String sum = sumFun.apply(data1);
            Assertions.assertEquals(sign, sum);
        }
        else {
            final String sign = r2.header(apiAuthProp.getSignatureHeader());
            Assertions.assertNull(sign);
        }
    }
}
