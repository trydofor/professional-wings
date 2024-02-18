package pro.fessional.wings.silencer.tweak;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.silencer.app.conf.TestApplicationEventLogger;
import pro.fessional.wings.silencer.app.conf.TestSpringOrderConfiguration;

/**
 * @author trydofor
 * @since 2024-02-17
 */
@SpringBootTest(properties = {
        "wings.feature.enable[pro.fessional.wings.silencer.app.conf.*]=false",
        "wings.enabled.pro.fessional.wings.silencer.app.conf.TestSpringOrderConfiguration=true",
})
class FeatureFlag3Test {

    @Test
    void has() {
        Assertions.assertTrue(FeatureFlag.not(TestApplicationEventLogger.class));
        Assertions.assertTrue(FeatureFlag.has(TestSpringOrderConfiguration.class));

        Assertions.assertTrue(FeatureFlag.any(TestApplicationEventLogger.class, TestSpringOrderConfiguration.class));
        Assertions.assertFalse(FeatureFlag.all(TestApplicationEventLogger.class, TestSpringOrderConfiguration.class));
    }
}