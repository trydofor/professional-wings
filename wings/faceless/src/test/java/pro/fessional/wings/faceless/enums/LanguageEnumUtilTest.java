package pro.fessional.wings.faceless.enums;

import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.fessional.wings.faceless.enums.autogen.StandardLanguage;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2020-06-13
 */
@Slf4j
public class LanguageEnumUtilTest {

    @Test
    @TmsLink("C12017")
    public void localeLanguageTag() {
        Locale locale = StandardLanguage.ZH_CN.toLocale();
        log.info("locale={}", locale.toLanguageTag());
        StandardLanguage al = LanguageEnumUtil.localeOrNull(locale, StandardLanguage.values());
        Assertions.assertEquals(StandardLanguage.ZH_CN, al);
    }
}
