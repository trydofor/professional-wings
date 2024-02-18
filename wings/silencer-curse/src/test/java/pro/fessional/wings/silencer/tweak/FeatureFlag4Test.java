package pro.fessional.wings.silencer.tweak;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author trydofor
 * @since 2024-02-17
 */
@SpringBootTest
class FeatureFlag4Test {
    @Test
    void tweak() {
        TweakFeature.tweakThread(FeatureFlag4Test.class, true);
        TweakFeature.tweakGlobal(FeatureFlag4Test.class, false);
        Assertions.assertTrue(FeatureFlag.has(FeatureFlag4Test.class));

        TweakFeature.resetThread(FeatureFlag4Test.class);
        TweakFeature.tweakGlobal(FeatureFlag4Test.class, true);
        Assertions.assertTrue(FeatureFlag.has(FeatureFlag4Test.class));

        TweakFeature.resetThread();
        Assertions.assertTrue(FeatureFlag.has(FeatureFlag4Test.class));

        TweakFeature.resetGlobal();
        Assertions.assertTrue(FeatureFlag.not(FeatureFlag4Test.class));
    }
}