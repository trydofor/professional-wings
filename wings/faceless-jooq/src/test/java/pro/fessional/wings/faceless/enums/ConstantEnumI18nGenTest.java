package pro.fessional.wings.faceless.enums;

import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pro.fessional.wings.faceless.codegen.ConstantEnumGenerator;
import pro.fessional.wings.faceless.database.autogen.tables.daos.SysConstantEnumDao;
import pro.fessional.wings.faceless.database.autogen.tables.pojos.SysConstantEnum;

import java.util.List;

/**
 * @author trydofor
 * @since 2020-06-10
 */

@ActiveProfiles("init")
@SpringBootTest(properties = {"debug = true", "spring.wings.faceless.enabled.enumi18n=true"})
@TestMethodOrder(MethodOrderer.MethodName.class)
@Tag("init")
public class ConstantEnumI18nGenTest {

    @Setter(onMethod_ = {@Autowired})
    SysConstantEnumDao sysConstantEnumDao;

    @Test
    @Disabled("手动执行，避免污染java类")
    public void test2GenEnum() {
        List<SysConstantEnum> all = sysConstantEnumDao.findAll();
        ConstantEnumGenerator.builder()
                             .targetDirectory("./src/test/java/")
                             .targetPackage("pro.fessional.wings.faceless.enums.test")
                             .generate(SysConstantEnum.class, all);
    }
}
