package pro.fessional.wings.faceless.enums;

import org.junit.Test;
import pro.fessional.wings.faceless.enums.auto.StandardLanguage;

import java.util.Locale;

/**
 * @author trydofor
 * @since 2020-06-13
 */
public class LanguageEnumUtilTest {

    @Test
    public void test() {
        Locale locale = StandardLanguage.ZH_CN.toLocale();
        System.out.println(locale.toLanguageTag());
    }
}