package pro.fessional.wings.slardar.context;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * <a href="https://docs.spring.io/spring-boot/docs/3.0.3/reference/htmlsingle/#features.external-config">Externalized Configuration</a>
 *
 * @author trydofor
 * @since 2021-07-05
 */
@SpringBootTest
@Slf4j
public class ExternalConfigTest {

    @Value("${random.value}")
    private String randomValue;
    @Value("${random.uuid}")
    private String randomUuid;

    @Test
    public void testRandom() {
        log.info("random-value=" + randomValue);
        log.info("random-uuid=" + randomUuid);
    }
}
