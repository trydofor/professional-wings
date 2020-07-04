package pro.fessional.wings.example.init;

import lombok.Setter;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pro.fessional.wings.example.WingsExampleApplication;
import pro.fessional.wings.faceless.database.autogen.tables.daos.SysConstantEnumDao;
import pro.fessional.wings.faceless.database.autogen.tables.pojos.SysConstantEnum;
import pro.fessional.wings.faceless.util.ConstantEnumGenerator;

import java.util.List;

/**
 * ③ 根据数据库记录，自动生成enum类
 *
 * @author trydofor
 * @since 2020-06-10
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WingsExampleApplication.class, properties =
        {"debug = true",
         "spring.wings.enumi18n.enabled=true",
//         "spring.wings.flywave.enabled=true",
//         "spring.shardingsphere.datasource.names=writer",
//         "spring.shardingsphere.datasource.writer.jdbc-url=jdbc:mysql://127.0.0.1:3306/wings?autoReconnect=true&useSSL=false",
//         "spring.shardingsphere.datasource.writer.username=trydofor",
//         "spring.shardingsphere.datasource.writer.password=moilioncircle",
        })
@Ignore("手动生成Java类，依赖分支feature/enum-i18n的2019052101")
public class Wings3ConstEnumGen {

    @Setter(onMethod = @__({@Autowired}))
    SysConstantEnumDao sysConstantEnumDao;

    @Test
    public void gen() throws Exception {
        List<SysConstantEnum> all = sysConstantEnumDao.findAll();
        ConstantEnumGenerator.builder()
                             .setJavaSource("./src/main/java/")
                             .setJavaPackage("pro.fessional.wings.example.enums.auto")
                             // 如果够用，可以直接用，否则用自己生成的
                             .addExcludeType("standard_timezone", "standard_language")
                             .generate(SysConstantEnum.class, all);
    }
}