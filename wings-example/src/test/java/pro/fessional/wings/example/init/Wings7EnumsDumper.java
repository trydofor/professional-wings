package pro.fessional.wings.example.init;

import lombok.Setter;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;
import pro.fessional.mirana.data.CodeEnum;
import pro.fessional.wings.example.WingsExampleApplication;
import pro.fessional.wings.silencer.spring.help.SubclassSpringLoader;

import java.util.Map;

/**
 * ⑥ 使用wings的flywave，生成trigger和跟踪表
 *
 * @author trydofor
 * @since 2019-12-26
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WingsExampleApplication.class)
@Ignore("手动执行，版本更新时处理")
public class Wings7EnumsDumper {

    @Setter(onMethod = @__({@Autowired}))
    private ResourceLoader resourceLoader;

    @Test
    public void dumpCodeEnum() {
        SubclassSpringLoader loader = new SubclassSpringLoader(resourceLoader);
        Class<CodeEnum> superEnum = CodeEnum.class;

        Map<Class<?>, Enum<?>[]> enums = loader.loadSubEnums("pro.fessional", superEnum);
        for (Map.Entry<Class<?>, Enum<?>[]> e : enums.entrySet()) {
            String grp = e.getKey().getName();
            for (Enum<?> enu : e.getValue()) {
                CodeEnum en = (CodeEnum) enu;
                System.out.printf("%s\t%s\t%s\t%s\n", enu.name(), en.getCode(), en.getHint(), grp);
            }
        }
    }
}
