package pro.fessional.wings.faceless.converter;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Test;
import pro.fessional.wings.faceless.enums.autogen.StandardLanguage;
import pro.fessional.wings.faceless.enums.autogen.StandardTimezone;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * @author trydofor
 * @since 2024-01-14
 */
class WingsEnumConvertersTest {

    @Test
    @TmsLink("C12146")
    public void convertIdAndCode() {
        assertEquals(StandardLanguage.AR_AE, WingsEnumConverters.Id2Language.convert(1020101));
        assertEquals(StandardLanguage.AR_AE, WingsEnumConverters.Code2Language.convert("ar_AE"));
        assertEquals(StandardLanguage.AR_AE, WingsEnumConverters.Code2Language.convert("AR_AE"));
        assertEquals(StandardLanguage.AR_AE, WingsEnumConverters.Code2Language.convert("ar_ae"));

        assertEquals(StandardTimezone.AMERICA_NEW_YORK, WingsEnumConverters.Id2Timezone.convert(1010303));
        assertEquals(StandardTimezone.AMERICA_NEW_YORK, WingsEnumConverters.Code2Timezone.convert("America/New_York"));
    }
}