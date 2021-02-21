package pro.fessional.wings.example.init;

import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.example.WingsExampleApplication;
import pro.fessional.wings.example.database.autogen.tables.daos.SysConstantEnumDao;
import pro.fessional.wings.example.database.autogen.tables.pojos.SysConstantEnum;
import pro.fessional.wings.faceless.codegen.ConstantEnumGenerator;

import java.util.List;

/**
 * ③ 根据数据库记录，自动生成enum类
 *
 * @author trydofor
 * @since 2020-06-10
 */

@SpringBootTest(classes = WingsExampleApplication.class, properties =
        {"debug = true",
         "spring.wings.faceless.enabled.enumi18n=true",
//         "spring.wings.faceless.flywave.enabled.module=true",
//         "spring.shardingsphere.datasource.names=writer",
//         "spring.shardingsphere.datasource.writer.jdbc-url=jdbc:mysql://127.0.0.1:3306/wings?autoReconnect=true&useSSL=false",
//         "spring.shardingsphere.datasource.writer.username=trydofor",
//         "spring.shardingsphere.datasource.writer.password=moilioncircle",
        })
@Disabled("手动生成Java类，依赖分支feature/enum-i18n的2019052101")
public class Wings3ConstEnumGen {

    @Setter(onMethod = @__({@Autowired}))
    private SysConstantEnumDao sysConstantEnumDao;

    @Test
    public void gen() throws Exception {
        List<SysConstantEnum> all = sysConstantEnumDao.findAll();
        ConstantEnumGenerator.builder()
                             .setJavaSource("./src/main/java/")
                             .setJavaPackage("pro.fessional.wings.example.enums.auto")
                             // 如果够用，可以直接用，否则用自己生成的
                             .addExcludeType("standard_boolean","standard_timezone", "standard_language")
                             .generate(SysConstantEnum.class, all);
    }
}
