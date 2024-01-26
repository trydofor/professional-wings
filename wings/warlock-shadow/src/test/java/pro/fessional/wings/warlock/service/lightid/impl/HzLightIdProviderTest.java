package pro.fessional.wings.warlock.service.lightid.impl;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.id.LightIdProvider;
import pro.fessional.wings.slardar.service.lightid.HazelcastLightIdProvider;

/**
 * @author trydofor
 * @since 2023-07-18
 */
@SpringBootTest(properties = "wings.faceless.lightid.provider.monotonic=hz")
@Slf4j
public class HzLightIdProviderTest {
    @Setter(onMethod_ = {@Autowired})
    protected LightIdProvider lightIdProvider;

    @Test
    @TmsLink("C14062")
    public void hzLightIdProvider() {
        Assertions.assertInstanceOf(HazelcastLightIdProvider.class, lightIdProvider);
    }
}
