package pro.fessional.wings.warlock.service.perm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author trydofor
 * @since 2021-06-20
 */
class WarlockPermNormalizerTest {

    @Test
    void normalize() {
        WarlockPermNormalizer normalizer = new WarlockPermNormalizer();
        Assertions.assertEquals("ROLE_root", normalizer.role("root"));
        Assertions.assertEquals("ROLE_root", normalizer.role("ROLE_root"));
        Assertions.assertEquals("-ROLE_root", normalizer.role("-root"));
        Assertions.assertEquals("-ROLE_root", normalizer.role("-ROLE_root"));

        Assertions.assertTrue(normalizer.indexRolePrefix("ROLE_root") >= 0);
        Assertions.assertTrue(normalizer.indexRolePrefix("-ROLE_root") >= 0);
        Assertions.assertFalse(normalizer.indexRolePrefix("root") >= 0);
        Assertions.assertFalse(normalizer.indexRolePrefix("-root") >= 0);
    }
}
