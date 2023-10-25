package pro.fessional.wings.silencer.i18n;

import io.qameta.allure.TmsLink;
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
@SpringBootTest(properties = "wings.silencer.i18n.locale=en_US")
public class MessageModuleTest {

    @Setter(onMethod_ = {@Autowired})
    private MessageSource messageSource;

    @Setter(onMethod_ = {@Value("${wings.test.module}")})
    private String module;

    @Test
    @TmsLink("C11001")
    void testModule() {
        assertEquals("Silencer", module);
        assertEquals(Locale.forLanguageTag("en-US"), Locale.getDefault());
    }

    @Test
    @TmsLink("C11002")
    void testMessageStandard() {
        // use lang
        String cn = messageSource.getMessage("base.not-empty", Arr.of("姓名"), Locale.CHINA);
        // use default
        String en = messageSource.getMessage("base.not-empty", Arr.of("name"), Locale.US);
        // use lang and region
        String tw = messageSource.getMessage("base.not-empty", Arr.of("姓名"), Locale.TAIWAN);
        // use default
        String jp = messageSource.getMessage("base.not-empty", Arr.of("name"), Locale.JAPAN);

        assertEquals("姓名 不能为空", cn);
        assertEquals("name can not be empty", en);
        assertEquals("姓名 不能為空", tw);
        assertEquals("name can not be empty", jp);
    }

    @Test
    @TmsLink("C11003")
    void testMessagePartial() {
        // use lang
        String zh = messageSource.getMessage("base.not-empty", Arr.of("姓名"), Locale.forLanguageTag("zh"));
        // use default
        String en = messageSource.getMessage("base.not-empty", Arr.of("name"), Locale.forLanguageTag("en"));
        // use lang and region
        String tw = messageSource.getMessage("base.not-empty", Arr.of("姓名"), Locale.forLanguageTag("zh-TW"));
        // use default
        String jp = messageSource.getMessage("base.not-empty", Arr.of("name"), Locale.forLanguageTag("jp"));

        assertEquals("姓名 不能为空", zh);
        assertEquals("name can not be empty", en);
        assertEquals("姓名 不能為空", tw);
        assertEquals("name can not be empty", jp);
    }
}
