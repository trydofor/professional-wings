package pro.fessional.wings.faceless.enums;

import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.faceless.WingsTestHelper;
import pro.fessional.wings.faceless.codegen.ConstantEnumGenerator;
import pro.fessional.wings.faceless.database.autogen.tables.daos.SysConstantEnumDao;
import pro.fessional.wings.faceless.database.autogen.tables.pojos.SysConstantEnum;
import pro.fessional.wings.faceless.enums.autogen.StandardLanguage;
import pro.fessional.wings.faceless.enums.autogen.StandardTimezone;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.flywave.WingsRevision;
import pro.fessional.wings.faceless.service.wini18n.StandardI18nService;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;

import java.util.List;
import java.util.SortedMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_PATH_MASTER;

/**
 * @author trydofor
 * @since 2020-06-10
 */

@ActiveProfiles("init")
@SpringBootTest(properties = {"debug = true", "spring.wings.faceless.enabled.enumi18n=true"})
@TestMethodOrder(MethodOrderer.MethodName.class)
@Tag("init")
public class ConstantEnumI18nTest {

    @Setter(onMethod_ = {@Autowired})
    SysConstantEnumDao sysConstantEnumDao;

    @Setter(onMethod_ = {@Autowired})
    StandardI18nService standardI18nService;

    @Setter(onMethod_ = {@Autowired})
    WingsTestHelper wingsTestHelper;

    @Setter(onMethod_ = {@Autowired})
    SchemaRevisionManager schemaRevisionManager;

    @Setter(onMethod_ = {@Autowired})
    ApplicationContext applicationContext;

    @Setter(onMethod_ = {@Autowired})
    MessageSource messageSource;

    @Test
    public void test1Init() {
        wingsTestHelper.cleanTable();
        final SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls = FlywaveRevisionScanner
                .scan(REVISION_PATH_MASTER, WingsRevision.V01_19_0521_01_EnumI18n.classpath());
        schemaRevisionManager.checkAndInitSql(sqls, 0, true);
        schemaRevisionManager.publishRevision(WingsRevision.V01_19_0521_01_EnumI18n.revision(), -1);
    }

    @Test
    @Disabled("手动执行，避免污染java类")
    public void test2GenEnum() {
        List<SysConstantEnum> all = sysConstantEnumDao.findAll();
        ConstantEnumGenerator.builder()
                             .targetDirectory("./src/test/java/")
                             .targetPackage("pro.fessional.wings.faceless.enums.test")
                             .generate(SysConstantEnum.class, all);
    }

    @Test
    public void test3Code() {
        StandardLanguage zhCN = StandardLanguage.ZH_CN;
        StandardTimezone tzUs = StandardTimezone.AMERICA_CHICAGO;
        assertEquals(zhCN.getBase() + "." + zhCN.getKind() + "." + zhCN.getCode(), zhCN.getI18nCode());
        assertEquals(tzUs.getBase() + "." + zhCN.getKind() + ".id" + tzUs.getId(), tzUs.getI18nCode());
    }

    @Test
    public void test4I18n() {
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
