package pro.fessional.wings.silencer.modulate;

import io.qameta.allure.TmsLink;
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
    @TmsLink("C11011")
    void isRunMode() {
        Assertions.assertEquals(RunMode.Local, RuntimeMode.getRunMode());
        Assertions.assertFalse(RuntimeMode.isRunMode(""));
        Assertions.assertFalse(RuntimeMode.isRunMode("localx"));
        Assertions.assertTrue(RuntimeMode.isRunMode("local"));
        Assertions.assertTrue(RuntimeMode.isRunMode("local "));
        Assertions.assertTrue(RuntimeMode.isRunMode(" local "));
        Assertions.assertTrue(RuntimeMode.isRunMode(" local, "));
        Assertions.assertTrue(RuntimeMode.isRunMode(" local, "));

        Assertions.assertTrue(RuntimeMode.voteRunMode(" local, "));
        Assertions.assertTrue(RuntimeMode.voteRunMode(" !develop, "));
        Assertions.assertTrue(RuntimeMode.voteRunMode(" !develop,!test, "));
        Assertions.assertFalse(RuntimeMode.voteRunMode(" !local, "));

        new RuntimeMode(RunMode.Test, null) {};
        Assertions.assertEquals(RunMode.Test, RuntimeMode.getRunMode());
        Assertions.assertFalse(RuntimeMode.isRunMode(""));
        Assertions.assertFalse(RuntimeMode.isRunMode("testx"));
        Assertions.assertTrue(RuntimeMode.isRunMode("test"));
        Assertions.assertTrue(RuntimeMode.isRunMode("test "));
        Assertions.assertTrue(RuntimeMode.isRunMode(" test "));
        Assertions.assertTrue(RuntimeMode.isRunMode(" test, "));
        Assertions.assertTrue(RuntimeMode.isRunMode(" test, "));

        Assertions.assertFalse(RuntimeMode.voteRunMode(""));
        Assertions.assertTrue(RuntimeMode.voteRunMode(" test, "));
        Assertions.assertTrue(RuntimeMode.voteRunMode(" !local, "));
        Assertions.assertFalse(RuntimeMode.voteRunMode(" !develop, !test, "));
        Assertions.assertFalse(RuntimeMode.voteRunMode(" !test, "));
    }

    @Test
    @TmsLink("C11012")
    void isApiMode() {
        Assertions.assertEquals(ApiMode.Nothing, RuntimeMode.getApiMode());
        Assertions.assertTrue(RuntimeMode.isApiMode("nothing"));
        Assertions.assertTrue(RuntimeMode.isApiMode("nothing "));
        Assertions.assertTrue(RuntimeMode.isApiMode(" nothing "));
        Assertions.assertTrue(RuntimeMode.isApiMode(" nothing, "));
        Assertions.assertTrue(RuntimeMode.isApiMode(" nothing, "));

        Assertions.assertTrue(RuntimeMode.voteApiMode(" nothing, "));
        Assertions.assertTrue(RuntimeMode.voteApiMode(" !online, "));
        Assertions.assertTrue(RuntimeMode.voteApiMode(" !online,!sandbox "));
        Assertions.assertFalse(RuntimeMode.voteApiMode(" !nothing "));
    }
}
