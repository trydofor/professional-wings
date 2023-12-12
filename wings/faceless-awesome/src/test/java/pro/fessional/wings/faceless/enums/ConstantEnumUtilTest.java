package pro.fessional.wings.faceless.enums;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Test;
import pro.fessional.wings.faceless.enums.autogen.StandardLanguage;
import pro.fessional.wings.faceless.enums.autogen.StandardTimezone;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author trydofor
 * @since 2020-06-10
 */
public class ConstantEnumUtilTest {

    @Test
    @TmsLink("C12009")
    public void idOrElse() {
        StandardLanguage zhCn = StandardLanguage.ZH_CN;
        assertEquals(zhCn, ConstantEnumUtil.idOrNull(zhCn.getId(), StandardLanguage.values()));
        assertEquals(zhCn, ConstantEnumUtil.idOrElse(1, zhCn, StandardLanguage.values()));
    }

    @Test
    @TmsLink("C12010")
    public void nameOrElse() {
        StandardLanguage zhCn = StandardLanguage.ZH_CN;
        assertEquals(zhCn, ConstantEnumUtil.nameOrNull(zhCn.name(), StandardLanguage.values()));
        assertEquals(zhCn, ConstantEnumUtil.nameOrElse("", zhCn, StandardLanguage.values()));
    }

    @Test
    @TmsLink("C12011")
    public void codeOrElse() {
        StandardLanguage zhCn = StandardLanguage.ZH_CN;
        assertEquals(zhCn, ConstantEnumUtil.codeOrNull(zhCn.getCode(), StandardLanguage.values()));
        assertEquals(zhCn, ConstantEnumUtil.codeOrElse("", zhCn, StandardLanguage.values()));
    }

    @Test
    @TmsLink("C12012")
    public void codeIn() {
        StandardLanguage zhCn = StandardLanguage.ZH_CN;
        assertTrue(ConstantEnumUtil.codeIn(zhCn.getCode(), StandardLanguage.values()));
    }

    @Test
    @TmsLink("C12013")
    public void nameIn() {
        StandardLanguage zhCn = StandardLanguage.ZH_CN;
        assertTrue(ConstantEnumUtil.nameIn(zhCn.name(), StandardLanguage.values()));
    }

    @Test
    @TmsLink("C12014")
    public void idIn() {
        StandardLanguage zhCn = StandardLanguage.ZH_CN;
        assertTrue(ConstantEnumUtil.idIn(zhCn.getId(), StandardLanguage.values()));
    }

    @Test
    @TmsLink("C12015")
    public void groupInfo() {
        List<StandardTimezone> usTimezone = ConstantEnumUtil.groupInfo(StandardTimezone.AMERICA_CHICAGO, StandardTimezone.values());
        List<StandardTimezone> objects = Arrays.asList(
                StandardTimezone.AMERICA_CHICAGO,
                StandardTimezone.AMERICA_LOS_ANGELES,
                StandardTimezone.AMERICA_NEW_YORK,
                StandardTimezone.AMERICA_PHOENIX,
                StandardTimezone.US_ALASKA,
                StandardTimezone.US_HAWAII);
        assertEquals(objects, usTimezone);
    }

    @Test
    @TmsLink("C12016")
    public void namesAuto() {
        List<StandardTimezone> usTimezone = ConstantEnumUtil.namesAuto(StandardTimezone.values(),
                "AMERICA_CHICAGO, "
                + "AMERICA_LOS_ANGELES "
                + "AMERICA_NEW_YORK; "
                + "AMERICA_PHOENIX,");
        List<StandardTimezone> objects = Arrays.asList(
                StandardTimezone.AMERICA_CHICAGO,
                StandardTimezone.AMERICA_LOS_ANGELES,
                StandardTimezone.AMERICA_NEW_YORK,
                StandardTimezone.AMERICA_PHOENIX);
        assertEquals(objects, usTimezone);
    }
}
