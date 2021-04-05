package com.moilioncircle.roshan.common.load;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author trydofor
 * @since 2019-07-31
 */


//@SpringBootTest(properties = {"debug = true"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestJsonControllerTest {

    private TestRestTemplate tmpl;

    @Autowired
    public void setTmpl(TestRestTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Test
    public void jsonIt() {
        ResponseEntity<String> entity = tmpl.getForEntity("/test/test.json", String.class);
        boolean ok = entity.getStatusCode().is2xxSuccessful();
        assertTrue(ok);
    }

    @Test
    @Disabled("手动执行，负载测试")
    public void stressTestJson() {
        stress("/test/test.json", 2000, 20);
    }

    @Test
    @Disabled("手动执行，负载测试")
    public void stressSleep() {
        stress("/test/sleep.html?ms=6000", 2000, 50);
    }

    private void stress(final String uri, final int threads, final int loops) {
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch latch = new CountDownLatch(threads);
        final AtomicInteger oks = new AtomicInteger(0);
        final AtomicInteger nos = new AtomicInteger(0);
        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                try {
                    start.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ResponseEntity<String> entity = tmpl.getForEntity(uri, String.class);
                for (int j = 0; j < loops; j++) {
                    boolean ok = entity.getStatusCode().is2xxSuccessful();
                    if (ok) {
                        oks.incrementAndGet();
                    } else {
                        nos.incrementAndGet();
                    }
                }
            }).start();
        }
        start.countDown();


        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("ok=" + oks.get());
        System.out.println("no=" + nos.get());
    }
}
