package pro.fessional.wings.warlock.webmvc;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;
import pro.fessional.mirana.best.DummyBlock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;


/**
 * @author trydofor
 * @since 2022-12-03
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "test.plain-async=true")
@AutoConfigureMockMvc
public class JournalControllerTest {

    @Setter(onMethod_ = { @Value("http://localhost:${local.server.port}") })
    private String host;

    @Setter(onMethod_ = { @Autowired })
    private RestTemplate restTemplate;

    @Test
    @TmsLink("C14086")
    public void testTtlContext() throws InterruptedException {

        final ExecutorService executorService = Executors.newFixedThreadPool(50);

        final Pattern number = Pattern.compile("\\d+");
        final int total = 1000;
        final AtomicInteger error = new AtomicInteger(0);

        final ConcurrentHashMap<String, Integer> normal = new ConcurrentHashMap<>();

        for (int i = 0; i < total; i++) {
            final String ix = String.valueOf(i);
            final boolean ttl = i % 2 == 0;
            executorService.submit(() -> {
                final String id = restTemplate.getForObject(
                    host
                    + "/test/ttl-journal.json?t="
                    + ttl + "&i=" + ix, String.class);
                if (number.matcher(id).matches()) {
                    normal.compute(id, (k, v) -> v == null ? 1 : v + 1);
                }
                else {
                    error.incrementAndGet();
                }
            });
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
            DummyBlock.empty();
        }

        for (Map.Entry<String, Integer> en : normal.entrySet()) {
            Assertions.assertEquals(1, en.getValue(), en.getKey());
        }
        log.info("normal={}, error={}, total={}", normal.size(), error.get(), total);
        Assertions.assertEquals(total, error.get() + normal.size());
    }
}
