package pro.fessional.wings.faceless.druid;

import com.alibaba.druid.stat.DruidStatManagerFacade;
import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

/**
 * @author trydofor
 * @since 2021-01-08
 */
@SpringBootTest(properties = {"debug = true", "spring.wings.faceless.enumi18n.enabled=true"})
public class DruidStatTest {
    @Test
    public void druidStat() {
        final List<Map<String, Object>> stat = DruidStatManagerFacade.getInstance().getDataSourceStatDataList();
        System.out.println(JSON.toJSONString(stat, true));
    }
}