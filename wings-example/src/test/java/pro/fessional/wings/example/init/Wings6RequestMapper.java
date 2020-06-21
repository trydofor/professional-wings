package pro.fessional.wings.example.init;

import lombok.Setter;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import pro.fessional.wings.example.WingsExampleApplication;
import pro.fessional.wings.slardar.webmvc.RequestMappingHelper;

import java.util.List;

/**
 * ⑥ 使用wings的flywave，生成trigger和跟踪表
 *
 * @author trydofor
 * @since 2019-12-26
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WingsExampleApplication.class)
@Ignore("手动执行，版本更新时处理")
public class Wings6RequestMapper {

    @Setter(onMethod = @__({@Autowired}))
    private ApplicationContext context;

    @Test
    public void infoAllMapping() {
        List<RequestMappingHelper.Info> infos = RequestMappingHelper.infoAllMapping(context);
        for (RequestMappingHelper.Info info : infos) {
            System.out.println(info.toJson());
        }
    }
}
