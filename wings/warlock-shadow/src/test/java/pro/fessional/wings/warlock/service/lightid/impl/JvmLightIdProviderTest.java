package pro.fessional.wings.warlock.service.lightid.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.id.LightIdBufferedProvider;
import pro.fessional.mirana.id.LightIdProvider;

/**
 * @author trydofor
 * @since 2023-07-18
 */
@SpringBootTest(properties = "wings.faceless.lightid.provider.monotonic=jvm")
@Slf4j
public class JvmLightIdProviderTest {

    @Setter(onMethod_ = {@Autowired})
    protected LightIdProvider lightIdProvider;

    @Test
    public void test() {
        Assertions.assertInstanceOf(LightIdBufferedProvider.class, lightIdProvider);
    }
}
