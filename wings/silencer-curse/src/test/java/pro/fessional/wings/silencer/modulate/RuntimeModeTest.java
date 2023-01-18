package pro.fessional.wings.silencer.modulate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author trydofor
 * @since 2023-01-11
 */

class RuntimeModeTest {

    @Test
    void isRunMode() {
        final RunMode rm = RuntimeMode.getRunMode();
        Assertions.assertEquals(RunMode.Nothing, rm);
        Assertions.assertFalse(RuntimeMode.isRunMode(""));
        Assertions.assertFalse(RuntimeMode.isRunMode("nothinx"));
        Assertions.assertTrue(RuntimeMode.isRunMode("nothing"));
        Assertions.assertTrue(RuntimeMode.isRunMode("nothing "));
        Assertions.assertTrue(RuntimeMode.isRunMode(" nothing "));
        Assertions.assertTrue(RuntimeMode.isRunMode(" nothing, "));
        Assertions.assertTrue(RuntimeMode.isRunMode(" nothing, "));
    }

    @Test
    void isApiMode() {
        final ApiMode am = RuntimeMode.getApiMode();
        Assertions.assertEquals(ApiMode.Nothing, am);
        Assertions.assertTrue(RuntimeMode.isApiMode("nothing"));
        Assertions.assertTrue(RuntimeMode.isApiMode("nothing "));
        Assertions.assertTrue(RuntimeMode.isApiMode(" nothing "));
        Assertions.assertTrue(RuntimeMode.isApiMode(" nothing, "));
        Assertions.assertTrue(RuntimeMode.isApiMode(" nothing, "));
    }
}
