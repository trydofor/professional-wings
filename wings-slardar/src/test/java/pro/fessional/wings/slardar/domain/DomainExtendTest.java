package pro.fessional.wings.slardar.domain;

import lombok.Setter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

/**
 * @author trydofor
 * @since 2020-06-03
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.wings.slardar.extend-controller.enabled=true",
                "spring.wings.slardar.extend-resource.enabled=true"
        })
@RunWith(SpringRunner.class)
public class DomainExtendTest {

    @Setter(onMethod = @__({@Value("http://127.0.0.1:${local.server.port}")}))
    private String domainA;
    @Setter(onMethod = @__({@Value("http://localhost:${local.server.port}")}))
    private String domainB;

    @Setter(onMethod = @__({@Autowired}))
    private RestTemplate restTemplate;

    @Test
    public void testDomainA() {
        String json = restTemplate.getForObject(domainA + "/user-list.json", String.class);
        assertEquals("a.com/user-list.json", json);

        String css = restTemplate.getForObject(domainA + "/css/main.css", String.class);
        assertEquals("/*a.com/main.css*/", css);

        String htm = restTemplate.getForObject(domainA + "/user.html", String.class);
        assertEquals("a.com/user.html", htm);
    }

    @Test
    public void testDomainBA() {
        String json = restTemplate.getForObject(domainA + "/domain/b/user-list.json", String.class);
        assertEquals("b.com/user-list.json", json);

        String css = restTemplate.getForObject(domainA + "/domain/b/css/main.css", String.class);
        assertEquals("/*b.com/main.css*/", css);

        String htm = restTemplate.getForObject(domainA + "/domain/b/user.html", String.class);
        assertEquals("b.com/user.html", htm);
    }

    @Test
    public void testDomainBB() {
        String json = restTemplate.getForObject(domainB + "/domain/b/user-list.json", String.class);
        assertEquals("b.com/user-list.json", json);

        String css = restTemplate.getForObject(domainB + "/domain/b/css/main.css", String.class);
        assertEquals("/*b.com/main.css*/", css);

        String htm = restTemplate.getForObject(domainB + "/domain/b/user.html", String.class);
        assertEquals("b.com/user.html", htm);
    }

    @Test
    public void testDomainBE() {
        String json = restTemplate.getForObject(domainB + "/user-list.json", String.class);
        assertEquals("b.com/user-list.json", json);

        String css = restTemplate.getForObject(domainB + "/css/main.css", String.class);
        assertEquals("/*b.com/main.css*/", css);

        String htm = restTemplate.getForObject(domainB + "/user.html", String.class);
        assertEquals("b.com/user.html", htm);
    }
}