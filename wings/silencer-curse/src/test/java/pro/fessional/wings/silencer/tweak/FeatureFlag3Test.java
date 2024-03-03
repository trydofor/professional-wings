package pro.fessional.wings.silencer.tweak;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.silencer.tweak.test3.TestFlagA;
import pro.fessional.wings.silencer.tweak.test3.TestFlagB;

/**
 * @author trydofor
 * @since 2024-02-17
 */
@SpringBootTest(properties = {
        "wings.feature.enable[pro.fessional.wings.silencer.tweak.test3.*]=false",
        "wings.enabled.pro.fessional.wings.silencer.tweak.test3.TestFlagB=true",
})
class FeatureFlag3Test {

    @Test
    void has() {
        Assertions.assertTrue(FeatureFlag.not(TestFlagA.class));
        Assertions.assertTrue(FeatureFlag.has(TestFlagB.class));

        Assertions.assertTrue(FeatureFlag.any(TestFlagA.class, TestFlagB.class));
        Assertions.assertFalse(FeatureFlag.all(TestFlagA.class, TestFlagB.class));
    }
}