package pro.fessional.wings.slardar.domain;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2020-06-03
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "wings.enabled.slardar.domainx=true",
        })
public class DomainExtendTest {

    @Setter(onMethod_ = {@Value("http://127.0.0.1:${local.server.port}")})
    private String domainA;
    @Setter(onMethod_ = {@Value("http://localhost:${local.server.port}")})
    private String domainB;

    @Setter(onMethod_ = {@Autowired})
    private RestTemplate restTemplate;

    @Test
    @TmsLink("C13043")
    public void testDomainA() {
        String json = restTemplate.getForObject(domainA + "/user-list.json", String.class);
        assertEquals("a.com/user-list.json", json);

        String css = restTemplate.getForObject(domainA + "/css/main.css", String.class);
        assertEquals("/*a.com/main.css*/", css);

        String htm = restTemplate.getForObject(domainA + "/user.html", String.class);
        assertEquals("a.com/user.html", htm);
    }

    @Test
    @TmsLink("C13044")
    public void testDomainBA() {
        String json = restTemplate.getForObject(domainA + "/domain/b/user-list.json", String.class);
        assertEquals("b.com/user-list.json", json);

        String css = restTemplate.getForObject(domainA + "/domain/b/css/main.css", String.class);
        assertEquals("/*b.com/main.css*/", css);

        String htm = restTemplate.getForObject(domainA + "/domain/b/user.html", String.class);
        assertEquals("b.com/user.html", htm);
    }

    @Test
    @TmsLink("C13045")
    public void testDomainBB() {
        String json = restTemplate.getForObject(domainB + "/domain/b/user-list.json", String.class);
        assertEquals("b.com/user-list.json", json);

        String css = restTemplate.getForObject(domainB + "/domain/b/css/main.css", String.class);
        assertEquals("/*b.com/main.css*/", css);

        String htm = restTemplate.getForObject(domainB + "/domain/b/user.html", String.class);
        assertEquals("b.com/user.html", htm);
    }

    @Test
    @TmsLink("C13046")
    public void testDomainBE() {
        String json = restTemplate.getForObject(domainB + "/user-list.json", String.class);
        assertEquals("b.com/user-list.json", json);

        String css = restTemplate.getForObject(domainB + "/css/main.css", String.class);
        assertEquals("/*b.com/main.css*/", css);

        String htm = restTemplate.getForObject(domainB + "/user.html", String.class);
        assertEquals("b.com/user.html", htm);
    }
}
