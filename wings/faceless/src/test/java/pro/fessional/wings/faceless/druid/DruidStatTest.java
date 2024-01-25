package pro.fessional.wings.faceless.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidStatManagerFacade;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.fessional.wings.testing.spring.conf.TestingDatabaseAutoConfiguration;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * @author trydofor
 * @since 2021-01-08
 */
@SpringBootTest(properties = {
        "spring.datasource.type=com.alibaba.druid.pool.DruidDataSource",
        "spring.datasource.druid.filter.stat.enabled=true",
})
@EnableAutoConfiguration(exclude = TestingDatabaseAutoConfiguration.class)
@Slf4j
public class DruidStatTest {

    @Setter(onMethod_ = {@Autowired})
    protected DataSource dataSource;

    @Test
    @TmsLink("C12005")
    public void infoDruidStat() {
        Assertions.assertInstanceOf(DruidDataSource.class, dataSource);
        new JdbcTemplate(dataSource).execute("SHOW TABLES;");

        final List<Map<String, Object>> stat = DruidStatManagerFacade.getInstance().getDataSourceStatDataList();
        Assertions.assertFalse(stat.isEmpty());
        log.info("druidStat={}", JSON.toJSONString(stat, JSONWriter.Feature.PrettyFormat));
    }
}
