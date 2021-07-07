package com.moilioncircle.roshan.devops.project;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.flywave.SchemaJournalManager;

import java.util.Arrays;
import java.util.List;

/**
 * ⑤ 使用wings的flywave，生成trigger和跟踪表
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
@Disabled("手动执行，版本更新时处理")
@Slf4j
public class Devops5JournalManagerTest {

    @Setter(onMethod_ = {@Autowired})
    private SchemaJournalManager schemaJournalManager;

    @Test
    public void journal() {
        long commitId = 9999_9999_9999L;
        boolean enable = true;
        List<String> tables = Arrays.asList(
                "win_user",
                "win_user_login"
        );

        for (String table : tables) {
            log.info("====== init table={}", table);
            schemaJournalManager.checkAndInitDdl(table, commitId);
        }

        for (String table : tables) {
            log.info("====== init delete,update={}", table);
            schemaJournalManager.publishDelete(table, enable, commitId);
            schemaJournalManager.publishUpdate(table, enable, commitId);
        }
    }
}
