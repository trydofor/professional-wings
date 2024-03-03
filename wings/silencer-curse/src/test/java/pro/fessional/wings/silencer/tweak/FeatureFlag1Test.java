package pro.fessional.wings.silencer.tweak;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.silencer.tweak.test1.TestFlagA;
import pro.fessional.wings.silencer.tweak.test1.TestFlagB;

/**
 * @author trydofor
 * @since 2024-02-17
 */
@SpringBootTest(properties = {
        "wings.enabled.pro.fessional.wings.silencer.tweak.test1.TestFlagB=true",
})
class FeatureFlag1Test {

    @Test
    void has() {
        Assertions.assertTrue(FeatureFlag.not(TestFlagA.class));
        Assertions.assertTrue(FeatureFlag.has(TestFlagB.class));

        Assertions.assertTrue(FeatureFlag.any(TestFlagA.class, TestFlagB.class));
        Assertions.assertFalse(FeatureFlag.all(TestFlagA.class, TestFlagB.class));
    }
}