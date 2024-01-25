package pro.fessional.wings.slardar.httprest;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.io.InputStreams;
import pro.fessional.wings.slardar.fastjson.FastJsonHelper;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpClientHelper;
import pro.fessional.wings.slardar.httprest.retrofit.RetrofitHelper;
import retrofit2.Call;

import java.io.FileInputStream;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pro.fessional.wings.slardar.httprest.TestRetrofitCaller.Bad;
import static pro.fessional.wings.slardar.httprest.TestRetrofitCaller.Ins;
import static pro.fessional.wings.slardar.httprest.okhttp.OkHttpMediaType.APPLICATION_OCTET_STREAM_VALUE;
import static pro.fessional.wings.slardar.httprest.okhttp.OkHttpMediaType.MULTIPART_FORM_DATA_VALUE;


/**
 * @author trydofor
 * @since 2020-06-03
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class RetrofitTest {

    @Setter(onMethod_ = {@Value("http://localhost:${local.server.port}")})
    private String host;

    @Setter(onMethod_ = {@Autowired})
    private OkHttpClient okHttpClient;

    @Setter(onMethod_ = {@Autowired})
    protected ObjectMapper objectMapper;


    @Test
    @TmsLink("C13056")
    public void testJacksonBuilder() {
        final OkHttpClient.Builder bd = okHttpClient.newBuilder();
        bd.addInterceptor(chain -> {
            Request request = chain.request();
            return chain.proceed(request);
        });

        TestRetrofitCaller caller = RetrofitHelper.jacksonPlain(TestRetrofitCaller.class, host, bd.build());
        testAll(caller);
    }

    @Test
    @TmsLink("C13057")
    public void testJacksonAutowired() {
        TestRetrofitCaller caller = RetrofitHelper.jacksonPlain(TestRetrofitCaller.class, host, okHttpClient);
        testAll(caller);
    }

    @SneakyThrows
    private void testAll(TestRetrofitCaller caller) {
        Bad bad = new Bad();
        bad.setSsStr("ssStr");
        bad.setSStr("sStr");
        Ins ins = new Ins();
        ins.setBad(Collections.singletonList(bad));

        final Call<Ins> c1 = caller.jsonIns(ins);
        final Ins b1 = c1.execute().body();
        assertEquals(ins, b1);

        final Call<ResponseBody> c2 = caller.download();
        final String body = OkHttpClientHelper.extractString(c2.execute().body());
        String pom = InputStreams.readText(new FileInputStream("./pom.xml"));
        assertEquals(pom, body);

        String txt = "123456\nasdfgh";

        final MultipartBody.Part bd = MultipartBody.Part.createFormData(
                "up", "test.txt", RequestBody.create(
                        txt.getBytes(), APPLICATION_OCTET_STREAM_VALUE

                )
        );
        final Call<ResponseBody> c3 = caller.upload(bd);
        String j3 = OkHttpClientHelper.extractString(c3.execute().body());
        assertEquals(txt, j3);

        final RequestBody rb = RequestBody.create(txt.getBytes(), MULTIPART_FORM_DATA_VALUE);
        final Call<ResponseBody> c4 = caller.upload(rb);
        String j4 = OkHttpClientHelper.extractString(c4.execute().body());
        assertEquals(txt, j4);
    }

    @SneakyThrows
    @Test
    @TmsLink("C13058")
    public void infoFastjsonAndJackson() {
        Bad bad = new Bad();
        bad.setSsStr("ssStr");
        bad.setSStr("sStr");
        final String j1 = JSON.toJSONString(bad, FastJsonHelper.DefaultWriter());
        log.info("fastjson:{}", j1);

        final String j2 = objectMapper.writeValueAsString(bad);
        log.info("jackson:{}", j2);

        //
        final Bad o1 = JSON.parseObject(j1, Bad.class, FastJsonHelper.DefaultReader());
        log.info("fastjson-fastjson:{}", o1);
        final Bad o2 = objectMapper.readValue(j1, Bad.class);
        log.info("fastjson-jackson:{}", o2);

        final Bad o3 = JSON.parseObject(j2, Bad.class, FastJsonHelper.DefaultReader());
        log.info("jackson-fastjson:{}", o3);
        final Bad o4 = objectMapper.readValue(j2, Bad.class);
        log.info("jackson-jackson:{}", o4);

    }
}
