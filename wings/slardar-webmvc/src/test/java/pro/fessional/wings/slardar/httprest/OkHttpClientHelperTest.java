package pro.fessional.wings.slardar.httprest;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.io.InputStreams;

import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pro.fessional.wings.slardar.httprest.okhttp.OkHttpClientHelper.download;
import static pro.fessional.wings.slardar.httprest.okhttp.OkHttpClientHelper.postFile;
import static pro.fessional.wings.slardar.httprest.okhttp.OkHttpClientHelper.postJson;


/**
 * @author trydofor
 * @since 2020-06-03
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OkHttpClientHelperTest {

    @Setter(onMethod_ = {@Value("http://localhost:${local.server.port}")})
    private String host;

    @Setter(onMethod_ = {@Autowired})
    private OkHttpClient okHttpClient;


    @Test
    @TmsLink("C13047")
    public void testPostJson() {
        String j1 = "{}";
        String j2 = postJson(okHttpClient, host + "/test/rest-template-helper-body.htm", j1);
        assertEquals(j1, j2);
    }

    @Test
    @TmsLink("C13048")
    public void testPostBad() {
        String j1 = "{\"bad\":[{\"ssStr\":\"ssStr\",\"sStr\":\"sStr\"}]}";
        String j2 = postJson(okHttpClient, host + "/test/rest-bad-json.htm", j1);
        assertEquals("{\"bad\":[{\"ssStr\":\"ssStr\"}]}", j2);
    }

    @Test
    @TmsLink("C13049")
    public void testPostFile() {
        String txt = "123456\nasdfgh";
        String j2 = postFile(okHttpClient, host + "/test/rest-template-helper-file.htm", "up", txt.getBytes(), "test.txt");
        assertEquals(txt, j2);
    }

    @Test
    @TmsLink("C13050")
    public void testDownload() throws IOException {
        byte[] bytes = download(okHttpClient, host + "/test/rest-template-helper-down.htm");
        String pom = InputStreams.readText(new FileInputStream("./pom.xml"));
        assertEquals(pom, new String(bytes));
    }
}
