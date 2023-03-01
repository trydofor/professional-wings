package com.moilioncircle.wings.devops.project;

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
        "spring.datasource.url=" + Devops0ProjectConstant.JDBC_URL,
        "spring.datasource.username=" + Devops0ProjectConstant.JDBC_USER,
        "spring.datasource.password=" + Devops0ProjectConstant.JDBC_PASS,
        "debug = true"
})
@Disabled("分表分库")
public class Devops6ShardingManagerTest {

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
