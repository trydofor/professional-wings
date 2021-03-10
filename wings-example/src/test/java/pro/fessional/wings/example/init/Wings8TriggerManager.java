package pro.fessional.wings.example.init;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.example.WingsExampleApplication;
import pro.fessional.wings.faceless.flywave.SchemaJournalManager;

import java.util.Arrays;
import java.util.List;

/**
 * ⑥ 使用wings的flywave，生成trigger和跟踪表
 *
 * @author trydofor
 * @since 2019-12-26
 */

@SpringBootTest(classes = WingsExampleApplication.class)
@Slf4j
@Disabled("手动执行，要先分表在跟踪，若已跟踪，先摘trigger再分表，再跟踪")
public class Wings8TriggerManager {

    @Setter(onMethod_ = {@Autowired})
    private SchemaJournalManager schemaJournalManager;

    // 如果有分表，需要先分表，后trigger，否则个分表只是复制本表
    @Test
    public void journal() {
        long cid = -1;
        List<String> tables = Arrays.asList(
                "win_auth_role",
                "win_user",
                "win_user_login"
        );

        for (String table : tables) {
            log.info("====== init table={}", table);
            schemaJournalManager.checkAndInitDdl(table, cid);
        }

        for (String table : tables) {
            log.info("====== init delete,update={}", table);
            schemaJournalManager.publishDelete(table, false, cid);
            schemaJournalManager.publishUpdate(table, false, cid);
        }
    }
}
