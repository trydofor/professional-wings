package pro.fessional.wings.silencer.i18n;

import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import pro.fessional.mirana.data.Arr;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2019-06-10
 */
@SpringBootTest
public class MessagePrintTest {

    @Setter(onMethod_ = {@Autowired})
    private MessageSource messageSource;

    @Setter(onMethod_ = {@Value("${wings.test.module}")})
    private String module;

    @Test
    void print() {
        String cn = messageSource.getMessage("base.not-empty", Arr.of("姓名"), Locale.CHINA);
        String en = messageSource.getMessage("base.not-empty", Arr.of("name"), Locale.US);
        assertEquals("姓名 不能为空", cn);
        assertEquals("name can not be empty", en);
        assertEquals("沉默术士", module);
    }
}
