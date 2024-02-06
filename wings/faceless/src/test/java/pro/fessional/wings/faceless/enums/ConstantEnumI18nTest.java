package pro.fessional.wings.faceless.enums;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.io.InputStreams;
import pro.fessional.wings.faceless.enums.autogen.StandardLanguage;
import pro.fessional.wings.faceless.enums.autogen.StandardTimezone;
import pro.fessional.wings.faceless.service.wini18n.StandardI18nService;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2020-06-10
 */

@SpringBootTest
@TestMethodOrder(MethodName.class)
@Slf4j
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
    @TmsLink("C12006")
    public void enumI18nCode() {
        StandardLanguage zhCN = StandardLanguage.ZH_CN;
        StandardTimezone tzUs = StandardTimezone.AMERICA_CHICAGO;
        Assertions.assertEquals(zhCN.getBase() + "." + zhCN.getKind() + "." + zhCN.getType() + "." + zhCN.getCode(), zhCN.getI18nCode());
        Assertions.assertEquals(tzUs.getBase() + "." + tzUs.getKind() + "." + tzUs.getType() + ".id." + tzUs.getId(), tzUs.getI18nCode());
    }

    @Test
    @TmsLink("C12007")
    public void dynamicI18nService() {
        execWingsSql(jdbcTemplate, "master/01-light/2019-05-20u01-light-commit.sql");
        execWingsSql(jdbcTemplate, "master/01-light/2019-05-20v01-light-commit.sql");
        execWingsSql(jdbcTemplate, "branch/feature/01-enum-i18n/2019-05-21u01-enum-i18n.sql");
        execWingsSql(jdbcTemplate, "branch/feature/01-enum-i18n/2019-05-21v01-enum-i18n.sql");
        StandardLanguage zhCn = StandardLanguage.ZH_CN;
        StandardLanguage enUs = StandardLanguage.EN_US;
        assertEquals("简体中文", standardI18nService.load(zhCn, zhCn));
        assertEquals("Simplified Chinese", standardI18nService.load(zhCn, enUs));
        String mcn = messageSource.getMessage(zhCn.getI18nCode(), Null.StrArr, zhCn.toLocale());
        String men = messageSource.getMessage(zhCn.getI18nCode(), Null.StrArr, enUs.toLocale());
        assertEquals("简体中文", mcn);
        assertEquals("Simplified Chinese", men);
    }

    void execWingsSql(JdbcTemplate jdbcTemplate, String path) {
        String sqls = InputStreams.readText(ConstantEnumI18nTest.class.getResourceAsStream("/wings-flywave/" + path));
        for (String sql : sqls.split(
                ";+[ \\t]*[\\r\\n]+"
                + "|"
                + ";+[ \\t]*--[^\\r\\n]+[\\r\\n]+"
                + "|"
                + ";+[ \\t]*/\\*[^\\r\\n]+\\*/[ \\t]*[\\r\\n]+"
        )) {
            String s = sql.trim();
            if (!s.isEmpty()) {
                jdbcTemplate.execute(s);
            }
        }
    }

    @Test
    @TmsLink("C12008")
    public void infoAllBeanName() {
        int i = 1;
        for (String bean : applicationContext.getBeanDefinitionNames()) {
            log.info("[{}] {}", i++, bean);
        }
    }
}
