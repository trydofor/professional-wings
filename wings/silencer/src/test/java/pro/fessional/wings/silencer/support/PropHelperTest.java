package pro.fessional.wings.silencer.support;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * @author trydofor
 * @since 2024-06-28
 */
@SpringBootTest
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

    @Test
    @TmsLink("C11037")
    void testResourceString() {
        ClassPathResource app = new ClassPathResource("application.properties");
        Assertions.assertTrue(app.exists());
        String res1 = PropHelper.stringResource(app);
        Assertions.assertEquals("classpath:application.properties", res1);

        Resource res2 = PropHelper.resourceString("classpath:application.properties");
        Assertions.assertTrue(res2.exists());

        Resource res3 = PropHelper.resourceString("optional:classpath:application.properties");
        Assertions.assertTrue(res3.exists());

        Resource res4 = PropHelper.resourceString("optional:classpath:application.properties-404");
        Assertions.assertFalse(res4.exists());

        Resource res5 = PropHelper.resourceString("optional:file:./application.properties-404");
        Assertions.assertFalse(res5.exists());
    }
}