package pro.fessional.wings.example.init;

import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.example.WingsExampleApplication;
import pro.fessional.wings.faceless.flywave.SchemaShardingManager;

/**
 * ⑥ 使用wings的flywave，生成trigger和跟踪表
 *
 * @author trydofor
 * @since 2019-12-26
 */

@SpringBootTest(classes = WingsExampleApplication.class)
@Disabled("手动执行，版本更新时处理")
public class Wings9ShardingManager {

    @Setter(onMethod_ = {@Autowired})
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
