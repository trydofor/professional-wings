package pro.fessional.wings.faceless.enums;

import lombok.Setter;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.faceless.enums.autogen.StandardLanguage;
import pro.fessional.wings.faceless.enums.autogen.StandardTimezone;
import pro.fessional.wings.faceless.service.wini18n.StandardI18nService;
import pro.fessional.wings.faceless.util.ExecSql;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2020-06-10
 */

@SpringBootTest(properties = {"debug = true", "spring.wings.faceless.enabled.enumi18n=true"})
@TestMethodOrder(MethodName.class)
public class ConstantEnumI18nTest {

    @Setter(onMethod_ = {@Autowired})
    private StandardI18nService standardI18nService;

    @Setter(onMethod_ = {@Autowired})
    private ApplicationContext applicationContext;

    @Setter(onMethod_ = {@Autowired})
    private MessageSource messageSource;

    @Setter(onMethod_ = {@Autowired})
    private JdbcTemplate jdbcTemplate;

    @Test
    public void test3Code() {
        StandardLanguage zhCN = StandardLanguage.ZH_CN;
        StandardTimezone tzUs = StandardTimezone.AMERICA_CHICAGO;
        assertEquals(zhCN.getBase() + "." + zhCN.getKind() + "." + zhCN.getCode(), zhCN.getI18nCode());
        assertEquals(tzUs.getBase() + "." + zhCN.getKind() + ".id" + tzUs.getId(), tzUs.getI18nCode());
    }

    @Test
    public void test4I18n() {
        ExecSql.execWingsSql(jdbcTemplate, "master/01-light/2019-05-20u01-light-commit.sql");
        ExecSql.execWingsSql(jdbcTemplate, "master/01-light/2019-05-20v01-light-commit.sql");
        ExecSql.execWingsSql(jdbcTemplate, "branch/feature/01-enum-i18n/2019-05-21u01-enum-i18n.sql");
        ExecSql.execWingsSql(jdbcTemplate, "branch/feature/01-enum-i18n/2019-05-21v01-enum-i18n.sql");
        StandardLanguage zhCn = StandardLanguage.ZH_CN;
        StandardLanguage enUs = StandardLanguage.EN_US;
        assertEquals("简体中文", standardI18nService.load(zhCn, zhCn));
        assertEquals("Simplified Chinese", standardI18nService.load(zhCn, enUs));
        String mcn = messageSource.getMessage(zhCn.getI18nCode(), Null.StrArr, zhCn.toLocale());
        String men = messageSource.getMessage(zhCn.getI18nCode(), Null.StrArr, enUs.toLocale());
        assertEquals("简体中文", mcn);
        assertEquals("Simplified Chinese", men);
    }

    @Test
    public void printAllBean() {
        int i = 1;
        for (String bean : applicationContext.getBeanDefinitionNames()) {
            System.out.printf("[%d] %s\n", i++, bean);
        }
    }
}
