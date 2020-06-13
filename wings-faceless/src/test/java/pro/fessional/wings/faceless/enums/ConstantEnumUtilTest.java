package pro.fessional.wings.faceless.enums;

import org.junit.Assert;
import org.junit.Test;
import pro.fessional.wings.faceless.enums.auto.StandardLanguage;
import pro.fessional.wings.faceless.enums.auto.StandardTimezone;

import java.util.Arrays;
import java.util.List;

/**
 * @author trydofor
 * @since 2020-06-10
 */
public class ConstantEnumUtilTest {

    @Test
    public void idOrElse() {
        StandardLanguage zhCn = StandardLanguage.ZH_CN;
        Assert.assertEquals(zhCn, ConstantEnumUtil.idOrNull(zhCn.getId(), StandardLanguage.values()));
        Assert.assertEquals(zhCn, ConstantEnumUtil.idOrElse(1, zhCn, StandardLanguage.values()));
    }

    @Test
    public void nameOrElse() {
        StandardLanguage zhCn = StandardLanguage.ZH_CN;
        Assert.assertEquals(zhCn, ConstantEnumUtil.nameOrNull(zhCn.name(), StandardLanguage.values()));
        Assert.assertEquals(zhCn, ConstantEnumUtil.nameOrElse("", zhCn, StandardLanguage.values()));
    }

    @Test
    public void codeOrElse() {
        StandardLanguage zhCn = StandardLanguage.ZH_CN;
        Assert.assertEquals(zhCn, ConstantEnumUtil.codeOrNull(zhCn.getCode(), StandardLanguage.values()));
        Assert.assertEquals(zhCn, ConstantEnumUtil.codeOrElse("", zhCn, StandardLanguage.values()));
    }

    @Test
    public void codeIn() {
        StandardLanguage zhCn = StandardLanguage.ZH_CN;
        Assert.assertTrue(ConstantEnumUtil.codeIn(zhCn.getCode(), StandardLanguage.values()));
    }

    @Test
    public void nameIn() {
        StandardLanguage zhCn = StandardLanguage.ZH_CN;
        Assert.assertTrue(ConstantEnumUtil.nameIn(zhCn.name(), StandardLanguage.values()));
    }

    @Test
    public void idIn() {
        StandardLanguage zhCn = StandardLanguage.ZH_CN;
        Assert.assertTrue(ConstantEnumUtil.idIn(zhCn.getId(), StandardLanguage.values()));
    }

    @Test
    public void groupInfo() {
        List<StandardTimezone> usTimezone = ConstantEnumUtil.groupInfo(StandardTimezone.AMERICA𓃬CHICAGO, StandardTimezone.values());
        List<StandardTimezone> objects = Arrays.asList(
                StandardTimezone.AMERICA𓃬CHICAGO,
                StandardTimezone.AMERICA𓃬LOS_ANGELES,
                StandardTimezone.AMERICA𓃬NEW_YORK,
                StandardTimezone.AMERICA𓃬PHOENIX,
                StandardTimezone.US𓃬ALASKA,
                StandardTimezone.US𓃬HAWAII);
        Assert.assertEquals(objects, usTimezone);
    }
}