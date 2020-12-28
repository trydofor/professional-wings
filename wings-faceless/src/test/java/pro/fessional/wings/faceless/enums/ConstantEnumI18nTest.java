package pro.fessional.wings.faceless.enums;

import lombok.Setter;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.faceless.enums.auto.StandardLanguage;
import pro.fessional.wings.faceless.enums.auto.StandardTimezone;
import pro.fessional.wings.faceless.service.wini18n.StandardI18nService;
import pro.fessional.wings.faceless.util.ExecSql;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2020-06-10
 */

@ActiveProfiles("init")
@SpringBootTest(properties = {"debug = true", "spring.wings.enumi18n.enabled=true"})
@TestMethodOrder(MethodName.class)
@Tag("init")
public class ConstantEnumI18nTest {

    @Setter(onMethod = @__({@Autowired}))
    private StandardI18nService standardI18nService;

    @Setter(onMethod = @__({@Autowired}))
    private ApplicationContext applicationContext;

    @Setter(onMethod = @__({@Autowired}))
    private MessageSource messageSource;

    @Setter(onMethod = @__({@Autowired}))
    private JdbcTemplate jdbcTemplate;

    @Test
    public void test3Code() {
        StandardLanguage zhCN = StandardLanguage.ZH_CN;
        StandardTimezone tzUs = StandardTimezone.AMERICA𓃬CHICAGO;
        assertEquals(zhCN.getBase() + "." + zhCN.getKind() + "." + zhCN.getCode(), zhCN.getI18nCode());
        assertEquals(tzUs.getBase() + "." + zhCN.getKind() + ".id" + tzUs.getId(), tzUs.getI18nCode());
    }

    @Test
    public void test4I18n() {
        ExecSql.execWingsSql(jdbcTemplate, "master/01-light/2019-05-20u01-light-commit.sql");
        ExecSql.execWingsSql(jdbcTemplate, "master/01-light/2019-05-20v01-light-commit.sql");
        ExecSql.execWingsSql(jdbcTemplate, "branch/feature/01-enum-i18n/2019-05-21u01-enum-i18n.sql");
        ExecSql.execWingsSql(jdbcTemplate, "branch/feature/01-enum-i18n/2019-05-21v01-enum-i18n.sql");
        StandardLanguage zhCN = StandardLanguage.ZH_CN;
        StandardLanguage enUs = StandardLanguage.EN_US;
        assertEquals("简体中文", standardI18nService.load(zhCN, zhCN));
        assertEquals("Simplified Chinese", standardI18nService.load(zhCN, enUs));
        String mcn = messageSource.getMessage(zhCN.getI18nCode(), Null.StrArr, zhCN.toLocale());
        String men = messageSource.getMessage(zhCN.getI18nCode(), Null.StrArr, enUs.toLocale());
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