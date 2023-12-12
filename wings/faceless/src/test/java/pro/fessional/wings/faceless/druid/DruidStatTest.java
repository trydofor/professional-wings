package pro.fessional.wings.faceless.druid;

import com.alibaba.druid.stat.DruidStatManagerFacade;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

/**
 * @author trydofor
 * @since 2021-01-08
 */
@SpringBootTest
@Slf4j
public class DruidStatTest {
    @Test
    @TmsLink("C12005")
    public void druidStat() {
        final List<Map<String, Object>> stat = DruidStatManagerFacade.getInstance().getDataSourceStatDataList();
        log.info("druidStat={}", stat);
    }
}
