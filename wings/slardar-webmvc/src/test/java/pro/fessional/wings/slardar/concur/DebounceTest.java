package pro.fessional.wings.slardar.concur;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import pro.fessional.mirana.data.Q;
import pro.fessional.mirana.time.Sleep;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * @author trydofor
 * @since 2021-03-09
 */

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "wings.slardar.debounce.http-status=202",
                "wings.slardar.debounce.content-type=text/plain",
                "wings.slardar.debounce.response-body=debounced",
        })
@Slf4j
class DebounceTest {

    @Setter(onMethod_ = {@Value("http://localhost:${local.server.port}")})
    private String doubleKillHost;

    @Setter(onMethod_ = {@Autowired})
    private RestTemplate restTemplate;

    @Test
    @TmsLink("C13031")
    void debounceNore() throws InterruptedException {
        debounce(false, "/test/debounce-nore.json?p=p1");
        debounce(false, "/test/debounce-nore.json?p=p2");
    }

    @Test
    @TmsLink("C13032")
    void debounceView() throws InterruptedException {
        debounce(true, "/test/debounce-view.json?p=p1");
        debounce(true, "/test/debounce-view.json?p=p2");
    }

    @Test
    @TmsLink("C13033")
    void debounceError() throws InterruptedException {
        debounce(true, "/test/debounce-error.json?p=seq");
    }

    @Test
    @TmsLink("C13034")
    void debounceBody() throws InterruptedException {
        debounce(true, "/test/debounce-body.json?p=p1");
        debounce(true, "/test/debounce-body.json?p=p2");
    }

    private void debounce(boolean reuse, String url) {

        final Q<String> q = new Q<>("123");

        final ResponseEntity<String> r1 = restTemplate.postForEntity(this.doubleKillHost + url, q, String.class);
        assertEquals(HttpStatus.OK, r1.getStatusCode());
        log.info(">>r1>>" + r1.getBody());

        final ResponseEntity<String> r2 = restTemplate.postForEntity(this.doubleKillHost + url, q, String.class);
        log.info(">>r2>>" + r2.getBody());
        if (reuse) {
            assertEquals(HttpStatus.OK, r2.getStatusCode());
            assertEquals(r1.getBody(), r2.getBody(),"may be waiting more than 600ms");
        }
        else {
            assertEquals(202, r2.getStatusCode().value());
            assertEquals("debounced", r2.getBody());
        }

        Sleep.ignoreInterrupt(1000);
        final ResponseEntity<String> r3 = restTemplate.postForEntity(this.doubleKillHost + url, q, String.class);
        log.info(">>r3>>" + r3.getBody());
        assertEquals(HttpStatus.OK, r3.getStatusCode());
        assertNotEquals(r2.getBody(), r3.getBody());
    }
}
