package com.moilioncircle.roshan.common.project;

import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.flywave.SchemaShardingManager;

/**
 * ⑥ 使用wings的flywave，生成trigger和跟踪表
 *
 * @author trydofor
 * @since 2019-12-26
 */

@SpringBootTest(properties = {
        "spring.datasource.url=" + Demo0ProjectConstant.JDBC_URL,
        "spring.datasource.username=" + Demo0ProjectConstant.JDBC_USER,
        "spring.datasource.password=" + Demo0ProjectConstant.JDBC_PASS,
        "debug = true"
})
@Disabled("手动执行，版本更新时处理")
public class Demo6ShardingManagerTest {

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
