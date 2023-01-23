package pro.fessional.wings.faceless.sample;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 可以自己设置配置文件
 *
 * @author trydofor
 * @since 2020-06-10
 */

@ActiveProfiles("init")
@SpringBootTest(properties =
        {"debug = true",
         "spring.wings.faceless.enabled.enumi18n=true",
//         "spring.wings.faceless.flywave.enabled.module=true",
        })
@Disabled("手动生成Java类，依赖分支feature/enum-i18n的2019052101, ConstantEnumI18nTest")
@Tag("init")
public class ConstantEnumGenSample {

//    @Setter(onMethod_ = {@Autowired})
//    SysConstantEnumDao sysConstantEnumDao;
//
//    @Test
//    public void test2GenEnum() {
//        List<SysConstantEnum> all = sysConstantEnumDao.findAll();
//        ConstantEnumGenerator.builder()
//                             .targetDirectory("./src/main/java/")
//                             .targetPackage("pro.fessional.wings.faceless.enums.autogen")
//                             .excludeType("standard_boolean")
//                             .generate(SysConstantEnum.class, all);
//    }
}
