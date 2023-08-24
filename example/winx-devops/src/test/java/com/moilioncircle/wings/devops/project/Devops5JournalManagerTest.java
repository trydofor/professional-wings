package com.moilioncircle.wings.devops.project;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.flywave.SchemaJournalManager;
import pro.fessional.wings.faceless.util.FlywaveInteractiveGui;

import java.util.Arrays;
import java.util.List;

/**
 * Generate trigger and trace table by wings flywave
 *
 * @author trydofor
 * @since 2019-12-26
 */

@SpringBootTest(properties = {
        "spring.datasource.url=" + Devops0ProjectConstant.JDBC_URL,
        "spring.datasource.username=" + Devops0ProjectConstant.JDBC_USER,
        "spring.datasource.password=" + Devops0ProjectConstant.JDBC_PASS,
})
@Disabled("Journal Manage")
@Slf4j
public class Devops5JournalManagerTest {

    @Setter(onMethod_ = {@Autowired})
    private SchemaJournalManager schemaJournalManager;

    @Test
    public void journal() {
        schemaJournalManager.askWay(FlywaveInteractiveGui.askGui());
        schemaJournalManager.logWay(FlywaveInteractiveGui.logGui());

        long commitId = 9999_9999_9999L;
        boolean enable = true;
        boolean manage = true;
        List<String> tables = Arrays.asList(
                "win_user_basis",
                "win_user_login"
        );

        for (String table : tables) {
            log.info("====== init manager={}", table);
            schemaJournalManager.manageTriggers(table, manage);
        }

        for (String table : tables) {
            log.info("====== init table={}", table);
            schemaJournalManager.checkAndInitDdl(table, commitId);
        }

        for (String table : tables) {
            log.info("====== init delete,update={}", table);
            schemaJournalManager.publishInsert(table, enable, commitId);
            schemaJournalManager.publishUpdate(table, enable, commitId);
            schemaJournalManager.publishDelete(table, enable, commitId);
        }
    }
}
