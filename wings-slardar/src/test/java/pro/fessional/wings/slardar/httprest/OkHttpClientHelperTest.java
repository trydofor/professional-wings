package pro.fessional.wings.slardar.httprest;

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


/**
 * @author trydofor
 * @since 2020-06-03
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class OkHttpClientHelperTest {

    @Setter(onMethod = @__({@Value("http://localhost:${local.server.port}")}))
    private String host;

    @Setter(onMethod = @__({@Autowired}))
    private OkHttpClient okHttpClient;


    @Test
    public void postJson() {
        String j1 = "{}";
        String j2 = OkHttpClientHelper.postJson(okHttpClient, host + "/test/rest-template-helper-body.htm", j1);
        assertEquals(j1, j2);
    }

    @Test
    public void postFile() {
        String txt = "123456\nasdfgh";
        String j2 = OkHttpClientHelper.postFile(okHttpClient, host + "/test/rest-template-helper-file.htm", "up", txt.getBytes(), "test.txt");
        assertEquals(txt, j2);
    }

    @Test
    public void download() throws IOException {
        byte[] bytes = OkHttpClientHelper.download(okHttpClient, host + "/test/rest-template-helper-down.htm");
        String pom = InputStreams.readText(new FileInputStream("./pom.xml"));
        assertEquals(pom, new String(bytes));
    }
}