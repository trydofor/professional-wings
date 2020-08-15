package pro.fessional.wings.faceless.sample;

import lombok.Setter;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import pro.fessional.wings.faceless.WingsTestHelper;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;

/**
 * ① 使用wings的flywave管理数据库版本
 *
 * @author trydofor
 * @since 2019-06-22
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("init")
@SpringBootTest(properties =
        {"debug = true",
         "spring.wings.flywave.enabled=true",
//         "spring.wings.enumi18n.enabled=true",
//         "spring.shardingsphere.datasource.names=writer",
//         "spring.shardingsphere.datasource.writer.jdbc-url=jdbc:mysql://127.0.0.1:3306/wings?autoReconnect=true&useSSL=false",
//         "spring.shardingsphere.datasource.writer.username=trydofor",
//         "spring.shardingsphere.datasource.writer.password=moilioncircle",
        })
@Ignore("手动执行一次，初始化步骤，危险操作")
public class WingsSchemaGenerator {

    @Setter(onMethod = @__({@Autowired}))
    private WingsTestHelper wingsTestHelper;

    @Test
    public void init() {
//        wingsTestHelper.cleanAndInit(2019_0521_01L, FlywaveRevisionScanner.REVISION_PATH_MASTER, FlywaveRevisionScanner.REVISION_PATH_BRANCH_3RD_ENU18N);
        wingsTestHelper.cleanAndInit(2019_0601_02L, FlywaveRevisionScanner.REVISION_PATH_MASTER, FlywaveRevisionScanner.REVISION_PATH_BRANCH_3RD_ENU18N);
    }
}