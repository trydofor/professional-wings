package pro.fessional.wings.example.init;

import lombok.Setter;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pro.fessional.wings.example.WingsExampleApplication;
import pro.fessional.wings.faceless.flywave.SchemaShardingManager;

/**
 * ⑥ 使用wings的flywave，生成trigger和跟踪表
 *
 * @author trydofor
 * @since 2019-12-26
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WingsExampleApplication.class)
@Ignore("手动执行，版本更新时处理")
public class Wings9ShardingManager {

    @Setter(onMethod = @__({@Autowired}))
    private SchemaShardingManager schemaShardingManager;

    @Test
    public void test1SplitTable() {
        schemaShardingManager.publishShard("win_user", 2);
    }

    // 需要 sharding config
//    @Test
//    public void test2MoveDate() {
//        schemaShardingManager.shardingData("win_user", true);
//    }
}
