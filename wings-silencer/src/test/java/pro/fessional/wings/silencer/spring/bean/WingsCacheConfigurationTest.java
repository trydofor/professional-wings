package pro.fessional.wings.silencer.spring.bean;

import lombok.Setter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pro.fessional.wings.silencer.cache.MyCacheService;

import static org.junit.Assert.assertEquals;

/**
 * @author trydofor
 * @since 2020-08-10
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"debug = true"})
public class WingsCacheConfigurationTest {

    @Setter(onMethod = @__({@Autowired}))
    private MyCacheService cacheService;

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
