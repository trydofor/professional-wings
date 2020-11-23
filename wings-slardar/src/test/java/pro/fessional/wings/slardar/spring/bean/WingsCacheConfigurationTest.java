package pro.fessional.wings.slardar.spring.bean;

import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.slardar.service.TestMyCacheService;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2020-08-10
 */

@SpringBootTest(properties = {"debug = true"})
public class WingsCacheConfigurationTest {

    @Setter(onMethod = @__({@Autowired}))
    private TestMyCacheService cacheService;

    @Test
    public void cacheCall() {
        int c = cacheService.cacheMethod("foo");
        assertEquals(1, c);

        c = cacheService.cacheMethod("foo");
        assertEquals(1, c);
    }

    @Test
    public void directCall() {
        int c = cacheService.directMethod("bar");
        assertEquals(1, c);

        c = cacheService.directMethod("bar");
        assertEquals(2, c);
    }
}
