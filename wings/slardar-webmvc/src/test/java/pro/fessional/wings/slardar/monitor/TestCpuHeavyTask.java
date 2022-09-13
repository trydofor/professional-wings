package pro.fessional.wings.slardar.monitor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2021-07-16
 */
// @Component
@Slf4j
public class TestCpuHeavyTask {

    @Scheduled(fixedRate = 10_000)
    public void calcPi() {
        final Map<String, String> map = new HashMap<>();
        for (double i = 1; i < 10_0000; i++) {
            map.put("" + i, "pi=" + (Math.PI * Math.pow(i, 3)));
        }
        log.info("map size={}", map.size());
    }
}
