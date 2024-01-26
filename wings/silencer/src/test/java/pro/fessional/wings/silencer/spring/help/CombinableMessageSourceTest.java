package pro.fessional.wings.silencer.spring.help;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.support.StaticMessageSource;
import pro.fessional.wings.silencer.message.MessageSourceHelper;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author trydofor
 * @since 2019-09-16
 */
@SpringBootTest
public class CombinableMessageSourceTest {

    @Setter(onMethod_ = {@Autowired})
    private MessageSource messageSource;

    @Test
    @TmsLink("C11010")
    public void dynamicCombineMessage() {

        assertTrue(MessageSourceHelper.hasPrimary);
        assertTrue(MessageSourceHelper.hasCombine);

        Object[] args = {};
        String m1 = messageSource.getMessage("test.MyTest", args, Locale.CHINA);
        MessageSourceHelper.Combine.addMessage("test.MyTest", Locale.CHINA, "啥都好用");
        String m2 = messageSource.getMessage("test.MyTest", args, Locale.CHINA);

        StaticMessageSource sms = new StaticMessageSource();
        sms.addMessage("test.my-test", Locale.CHINA, "又一个测试");
        MessageSourceHelper.Combine.addMessage(sms, 1);
        String m3 = messageSource.getMessage("test.my-test", args, Locale.CHINA);

        assertEquals("test.MyTest", m1);// code
        assertEquals("啥都好用", m2);
        assertEquals("又一个测试", m3);
    }
}
