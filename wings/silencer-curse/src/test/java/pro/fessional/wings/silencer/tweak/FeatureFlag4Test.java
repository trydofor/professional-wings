package pro.fessional.wings.silencer.tweak;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.silencer.app.conf.TestSpringOrderConfiguration;

/**
 * @author trydofor
 * @since 2024-02-17
 */
@SpringBootTest
class FeatureFlag4Test {
    @Test
    void tweak() {
        TweakFeature.tweakThread(TestSpringOrderConfiguration.class, true);
        TweakFeature.tweakGlobal(TestSpringOrderConfiguration.class, false);
        Assertions.assertTrue(FeatureFlag.has(TestSpringOrderConfiguration.class));

        TweakFeature.resetThread(TestSpringOrderConfiguration.class);
        TweakFeature.tweakGlobal(TestSpringOrderConfiguration.class, true);
        Assertions.assertTrue(FeatureFlag.has(TestSpringOrderConfiguration.class));

        TweakFeature.resetThread();
        Assertions.assertTrue(FeatureFlag.has(TestSpringOrderConfiguration.class));

        TweakFeature.resetGlobal();
        Assertions.assertTrue(FeatureFlag.not(TestSpringOrderConfiguration.class));
    }
}