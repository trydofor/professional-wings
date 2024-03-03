package pro.fessional.wings.silencer.tweak;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.silencer.tweak.test2.TestFlagA;
import pro.fessional.wings.silencer.tweak.test2.TestFlagB;

/**
 * @author trydofor
 * @since 2024-02-17
 */
@SpringBootTest(properties = {
        "wings.feature.enable[pro.fessional.wings.silencer.tweak.test2.*]=true",
        "wings.enabled.pro.fessional.wings.silencer.tweak.test2.TestFlagB=false",
})
class FeatureFlag2Test {

    @Test
    void has() {
        Assertions.assertTrue(FeatureFlag.has(TestFlagA.class));
        Assertions.assertTrue(FeatureFlag.not(TestFlagB.class));

        Assertions.assertTrue(FeatureFlag.any(TestFlagA.class, TestFlagB.class));
        Assertions.assertFalse(FeatureFlag.all(TestFlagA.class, TestFlagB.class));
    }
}