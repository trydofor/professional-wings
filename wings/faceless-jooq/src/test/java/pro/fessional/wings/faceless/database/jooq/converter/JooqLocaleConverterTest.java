package pro.fessional.wings.faceless.database.jooq.converter;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;

/**Ø
 * @author trydofor
 * @since 2021-07-27
 */
class JooqLocaleConverterTest {

    @Test
    @TmsLink("C12080")
    void jooqLocaleConvert() {
        JooqLocaleConverter jlc = new JooqLocaleConverter();
        final Locale lc = jlc.from("en-us");
        final String t1 = jlc.to(lc);
        Assertions.assertEquals("en_US",t1);
        final Locale lc2 = Locale.forLanguageTag("en-us");
        final String t2 = jlc.to(lc2);
        Assertions.assertEquals("en_US",t2);
    }
}
