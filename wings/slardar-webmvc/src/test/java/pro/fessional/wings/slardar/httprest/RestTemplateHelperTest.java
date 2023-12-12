package pro.fessional.wings.slardar.httprest;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import pro.fessional.mirana.io.InputStreams;
import pro.fessional.wings.slardar.app.controller.TestRestTmplController;

import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pro.fessional.wings.slardar.app.controller.TestRestTmplController.json;

/**
 * @author trydofor
 * @since 2020-06-03
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestTemplateHelperTest {

    @Setter(onMethod_ = {@Value("http://localhost:${local.server.port}")})
    private String host;

    @Setter(onMethod_ = {@Autowired})
    private RestTemplate restTemplate;


    @Test
    @TmsLink("C13052")
    public void jsonEntity() {
        TestRestTmplController.Json j1 = json();
        HttpEntity<TestRestTmplController.Json> entity = RestTemplateHelper.jsonEntity(j1);
        ResponseEntity<TestRestTmplController.Json> j2 = restTemplate.postForEntity(host + "/test/rest-template-helper-body.htm", entity, TestRestTmplController.Json.class);
        assertEquals(j1, j2.getBody());
    }

    @Test
    @TmsLink("C13053")
    public void formEntity() {
        HttpEntity<MultiValueMap<String, String>> form = RestTemplateHelper.formEntity();
        RestTemplateHelper.body(form).add("k1", "v1");
        ResponseEntity<String> rt = restTemplate.postForEntity(host + "/test/rest-template-helper-body.htm", form, String.class);
        assertEquals("k1=v1", rt.getBody());
    }

    @Test
    @TmsLink("C13054")
    public void fileEntity() {
        HttpEntity<MultiValueMap<String, Object>> form = RestTemplateHelper.fileEntity();
        String txt = "123456\nasdfgh";
        RestTemplateHelper.addFile(form, "up", new ByteArrayResource(txt.getBytes()), "test.txt");
        ResponseEntity<String> rt = restTemplate.postForEntity(host + "/test/rest-template-helper-file.htm", form, String.class);
        assertEquals(txt, rt.getBody());
    }

    @Test
    @TmsLink("C13055")
    public void download() throws IOException {
        byte[] bytes = RestTemplateHelper.download(restTemplate, host + "/test/rest-template-helper-down.htm");
        String pom = InputStreams.readText(new FileInputStream("./pom.xml"));
        assertEquals(pom, new String(bytes));
    }
}
