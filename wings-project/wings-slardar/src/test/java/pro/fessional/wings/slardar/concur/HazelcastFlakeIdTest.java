package pro.fessional.wings.slardar.concur;

import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author trydofor
 * @since 2022-02-24
 */
@SpringBootTest
class HazelcastFlakeIdTest {

    @Setter(onMethod_ = {@Autowired})
    private HazelcastFlakeId hazelcastFlakeId;

    @Test
    void nextId() {
        for (int i = 0; i < 100; i++) {
            System.out.println(hazelcastFlakeId.nextId());
        }
    }
}
