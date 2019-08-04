package pro.fessional.wings.example.load;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertTrue;

/**
 * @author trydofor
 * @since 2019-07-31
 */

@RunWith(SpringRunner.class)
//@SpringBootTest(properties = {"debug = true"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestJsonControllerTest {

    @Autowired
    private TestRestTemplate tmpl;

    @Test
    public void jsonIt() throws InterruptedException {
        ResponseEntity<String> entity = tmpl.getForEntity("/test.json", String.class);
        boolean ok = entity.getStatusCode().is2xxSuccessful();
        assertTrue(ok);
    }

    @Test
    public void stressTestJson() {
        stress("/test.json", 2000, 20);
    }

    @Test
    public void stressSleep() {
        stress("/sleep.html?ms=6000", 2000, 50);
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