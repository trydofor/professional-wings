package pro.fessional.wings.silencer.modulate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author trydofor
 * @since 2023-01-11
 */
@SpringBootTest
public class RuntimeModeTest {

    @Test
    void isRunMode() {
        final RunMode rm = RuntimeMode.getRunMode();
        Assertions.assertEquals(RunMode.Local, rm);
        Assertions.assertFalse(RuntimeMode.isRunMode(""));
        Assertions.assertFalse(RuntimeMode.isRunMode("localx"));
        Assertions.assertTrue(RuntimeMode.isRunMode("local"));
        Assertions.assertTrue(RuntimeMode.isRunMode("local "));
        Assertions.assertTrue(RuntimeMode.isRunMode(" local "));
        Assertions.assertTrue(RuntimeMode.isRunMode(" local, "));
        Assertions.assertTrue(RuntimeMode.isRunMode(" local, "));
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
