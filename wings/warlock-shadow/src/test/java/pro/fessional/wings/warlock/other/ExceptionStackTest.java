package pro.fessional.wings.warlock.other;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.pain.CodeException;
import pro.fessional.mirana.pain.MessageException;
import pro.fessional.wings.slardar.context.TerminalContextException;

/**
 * @author trydofor
 * @since 2024-05-27
 */
@SpringBootTest(properties = "wings.silencer.tweak.code-stack=true")
public class ExceptionStackTest {

    @TmsLink("C14085")
    @Test
    void testStack() {
        TerminalContextException tce = new TerminalContextException("code");
        Assertions.assertEquals(0, tce.getStackTrace().length);

        MessageException me = new MessageException("code");
        Assertions.assertEquals(0, me.getStackTrace().length);

        CodeException ce = new CodeException("code");
        Assertions.assertNotEquals(0, ce.getStackTrace().length);
    }
}
