package pro.fessional.wings.faceless.enums;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import pro.fessional.wings.faceless.enums.autogen.StandardLanguage;

import java.util.Locale;

/**
 * @author trydofor
 * @since 2020-06-13
 */
@Slf4j
public class LanguageEnumUtilTest {

    @Test
    public void test() {
        Locale locale = StandardLanguage.ZH_CN.toLocale();
        log.info("locale={}", locale.toLanguageTag());
    }
}
