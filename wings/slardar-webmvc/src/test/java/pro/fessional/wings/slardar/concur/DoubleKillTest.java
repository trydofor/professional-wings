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
import pro.fessional.wings.slardar.app.service.TestDoubleKillService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author trydofor
 * @since 2021-03-09
 */

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "wings.slardar.double-kill.http-status=202",
                "wings.slardar.double-kill.content-type=text/plain",
                "wings.slardar.double-kill.response-body=double-killed ttl=${ttl}, key=${key}",
        })
@Slf4j
class DoubleKillTest {

    @Setter(onMethod_ = {@Value("http://localhost:${local.server.port}")})
    private String doubleKillHost;

    @Setter(onMethod_ = {@Autowired})
    private TestDoubleKillService testDoubleKillService;

    @Setter(onMethod_ = {@Autowired})
    private RestTemplate restTemplate;

    @Test
    @TmsLink("C13035")
    void doubleKillUrl() throws InterruptedException {
        final String url = this.doubleKillHost + "/test/double-kill.json";
        new Thread(() -> {
            final ResponseEntity<String> r1 = restTemplate.getForEntity(url, String.class);
            assertEquals(HttpStatus.OK, r1.getStatusCode());
        }).start();

        Thread.sleep(1000);
        final ResponseEntity<String> r2 = restTemplate.getForEntity(url, String.class);
        assertEquals(HttpStatus.ACCEPTED, r2.getStatusCode());
        final String ct = r2.getHeaders().getFirst("Content-Type");
        assertNotNull(ct);
        assertTrue(ct.contains("text/plain"));
        final String body = r2.getBody();
        assertNotNull(body);
        assertTrue(body.contains("double-killed"));
        log.info("body={}", body);
        String key = body.substring(body.lastIndexOf("=") + 1);
        final ProgressContext.Bar bar = ProgressContext.get(key);
        log.info("bar={}", bar);
    }

    @Test
    @TmsLink("C13036")
    void doubleKillAsync() throws InterruptedException {
        final String url = this.doubleKillHost + "/test/double-kill-async.json";
        new Thread(() -> {
            final ResponseEntity<String> r1 = restTemplate.getForEntity(url, String.class);
            assertEquals(HttpStatus.OK, r1.getStatusCode());
        }).start();

        Thread.sleep(1000);
        final ResponseEntity<String> r2 = restTemplate.getForEntity(url, String.class);
        assertEquals(HttpStatus.ACCEPTED, r2.getStatusCode());
        final String ct = r2.getHeaders().getFirst("Content-Type");
        assertNotNull(ct);
        assertTrue(ct.contains("text/plain"));
        final String body = r2.getBody();
        assertNotNull(body);
        assertTrue(body.contains("double-killed"));
        log.info("body={}", body);
        String key = body.substring(body.lastIndexOf("=") + 1);
        final ProgressContext.Bar bar = ProgressContext.get(key);
        log.info("bar={}", bar);
    }

    @Test
    @TmsLink("C13037")
    void doubleKillArg() throws InterruptedException {
        new Thread(() -> {
            log.info("before thread call");
            testDoubleKillService.sleepSecond("sleep", 10);
            log.info("after  thread call");
        }).start();

        Thread.sleep(1000);
        try {
            log.info("before main call");
            testDoubleKillService.sleepSecond("sleep", 10);
            log.info("after  main call");
            fail();
        } catch (DoubleKillException e) {
            assertTrue(true);
        }
    }

    @Test
    @TmsLink("C13038")
    void doubleKillStr() throws InterruptedException {
        new Thread(() -> {
            log.info("before thread call");
            testDoubleKillService.sleepSecondStr("sleep", 10);
            log.info("after  thread call");
        }).start();

        Thread.sleep(1000);
        try {
            log.info("before main call");
            testDoubleKillService.sleepSecondStr("sleep", 10);
            log.info("after  main call");
            fail();
        } catch (DoubleKillException e) {
            assertTrue(true);
        }
    }

    @Test
    @TmsLink("C13039")
    void doubleKillExp() throws InterruptedException {
        new Thread(() -> {
            log.info("before thread call");
            testDoubleKillService.sleepSecondExp("sleep", 10);
            log.info("after  thread call");
        }).start();

        Thread.sleep(1000);
        try {
            log.info("before main call");
            testDoubleKillService.sleepSecondExp("sleep", 10);
            log.info("after  main call");
            fail();
        } catch (DoubleKillException e) {
            assertTrue(true);
        }
    }
}
