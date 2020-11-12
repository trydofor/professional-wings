package pro.fessional.wings.faceless.sample;

import lombok.Setter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import pro.fessional.wings.faceless.database.autogen.tables.daos.SysConstantEnumDao;
import pro.fessional.wings.faceless.database.autogen.tables.pojos.SysConstantEnum;
import pro.fessional.wings.faceless.util.ConstantEnumGenerator;

import java.util.List;

/**
 * 可以自己设置配置文件
 * @author trydofor
 * @since 2020-06-10
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("init")
@SpringBootTest(properties =
        {"debug = true",
         "spring.wings.enumi18n.enabled=true",
//         "spring.wings.flywave.enabled=true",
//         "spring.shardingsphere.datasource.names=master",
//         "spring.shardingsphere.datasource.master.jdbc-url=jdbc:mysql://127.0.0.1:3306/wings?autoReconnect=true&useSSL=false",
//         "spring.shardingsphere.datasource.master.username=trydofor",
//         "spring.shardingsphere.datasource.master.password=moilioncircle",
        })
//@Ignore("手动生成Java类，依赖分支feature/enum-i18n的2019052101, ConstantEnumI18nTest")
public class ConstantEnumGenSample {

    @Setter(onMethod = @__({@Autowired}))
    SysConstantEnumDao sysConstantEnumDao;

    @Test
    public void test2GenEnum() throws Exception {
        List<SysConstantEnum> all = sysConstantEnumDao.findAll();
        ConstantEnumGenerator.builder()
                             .setJavaSource("./src/main/java/")
                             .setJavaPackage("pro.fessional.wings.faceless.enums.auto")
                             .generate(SysConstantEnum.class, all);
    }
}