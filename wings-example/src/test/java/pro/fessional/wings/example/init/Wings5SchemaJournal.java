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
 * ⑤ 使用wings的flywave，生成trigger和跟踪表
 *
 * @author trydofor
 * @since 2019-12-26
 */

@SpringBootTest(classes = WingsExampleApplication.class, properties =
        {"debug = true",
         "spring.wings.flywave.enabled=true",
//         "spring.wings.enumi18n.enabled=true",
//         "spring.shardingsphere.datasource.names=writer",
//         "spring.shardingsphere.datasource.writer.jdbc-url=jdbc:mysql://127.0.0.1:3306/wings?autoReconnect=true&useSSL=false",
//         "spring.shardingsphere.datasource.writer.username=trydofor",
//         "spring.shardingsphere.datasource.writer.password=moilioncircle",
        })
@Disabled("手动执行，版本更新时处理")
@Slf4j
public class Wings5SchemaJournal {

    @Setter(onMethod = @__({@Autowired}))
    private SchemaJournalManager schemaJournalManager;

    @Test
    public void journal() {
        long commitId = 2020_0606_1423L;
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
