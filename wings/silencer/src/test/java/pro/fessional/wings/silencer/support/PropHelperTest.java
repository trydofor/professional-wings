package pro.fessional.wings.silencer.support;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author trydofor
 * @since 2024-06-28
 */
class PropHelperTest {

    @Test
    @TmsLink("C11036")
    void testCommaList() {
        Assertions.assertTrue(PropHelper.commaList("").isEmpty());
        Assertions.assertTrue(PropHelper.commaList(",").isEmpty());
        Assertions.assertTrue(PropHelper.commaList(",,").isEmpty());
        Assertions.assertEquals(List.of("1", "2"), PropHelper.commaList("1,,2"));
        Assertions.assertEquals(List.of("1", "2"), PropHelper.commaList("1 , - , , 2 "));

        Assertions.assertEquals(List.of(""), PropHelper.commaList("", false, false));
        Assertions.assertEquals(List.of("", ""), PropHelper.commaList(",", false, false));
        Assertions.assertEquals(List.of("", "", ""), PropHelper.commaList(",,", false, false));
        Assertions.assertEquals(List.of("1", "", "2"), PropHelper.commaList("1,,2", false, false));
        Assertions.assertEquals(List.of("1 ", " ", " 2 "), PropHelper.commaList("1 , , 2 ", false, false));

        Assertions.assertNull(PropHelper.commaString((String[]) null));
        Assertions.assertEquals("", PropHelper.commaString(List.of("")));
        Assertions.assertEquals(",", PropHelper.commaString(List.of("","")));
        Assertions.assertEquals(",,1", PropHelper.commaString(List.of("", "", "1")));
        Assertions.assertEquals(",,1 ", PropHelper.commaString(List.of("", "", "1 ")));
        Assertions.assertEquals(",,1 ,-", PropHelper.commaString(List.of("", "", "1 ", "-")));

        Assertions.assertEquals("", PropHelper.commaString(List.of(""), true, true));
        Assertions.assertEquals("", PropHelper.commaString(List.of("", ""), true, true));
        Assertions.assertEquals("1", PropHelper.commaString(List.of("", "", "1"), true, true));
        Assertions.assertEquals("1", PropHelper.commaString(List.of("", "", "1 "), true, true));
        Assertions.assertEquals("1", PropHelper.commaString(List.of("", "", "1 ", "- "), true, true));
        Assertions.assertEquals("1 ,- ", PropHelper.commaString(List.of("", "", "1 ", "- "), false, true));
    }
}