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
        "wings.feature.enable[pro.fessional.wings.silencer.app.conf.*]=true",
        "wings.enabled.pro.fessional.wings.silencer.app.conf.TestSpringOrderConfiguration=false",
})
class FeatureFlag2Test {

    @Test
    void has() {
        Assertions.assertTrue(FeatureFlag.has(TestApplicationEventLogger.class));
        Assertions.assertTrue(FeatureFlag.not(TestSpringOrderConfiguration.class));

        Assertions.assertTrue(FeatureFlag.any(TestApplicationEventLogger.class, TestSpringOrderConfiguration.class));
        Assertions.assertFalse(FeatureFlag.all(TestApplicationEventLogger.class, TestSpringOrderConfiguration.class));
    }
}