package pro.fessional.wings.example.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.fessional.wings.faceless.flywave.SchemaJournalManager;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.flywave.SchemaShardingManager;
import pro.fessional.wings.faceless.spring.conf.WingsFlywaveVerProperties;
import pro.fessional.wings.faceless.util.FlywaveRevisionSqlScanner;

import java.util.SortedMap;

/**
 * @author trydofor
 * @since 2019-06-30
 */

@Controller
@AllArgsConstructor
@Slf4j
public class TestFlywaveController {

    private final SchemaShardingManager schemaShardingManager;
    private final SchemaRevisionManager schemaRevisionManager;
    private final SchemaJournalManager schemaJournalManager;
    private final WingsFlywaveVerProperties wingsFlywaveVerProperties;

    @RequestMapping("/flywave-revi.json")
    @ResponseBody
    public String flywaveRevi(@RequestParam("revi") long revi) {
        StringBuilder sb = new StringBuilder();
        if (revi <= 2019051201) {
            sb.append("\n开始初始化");
            SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls = FlywaveRevisionSqlScanner.scan(SchemaRevisionManager.REVISIONSQL_PATH);
            schemaRevisionManager.checkAndInitSql(sqls, 0);
            sb.append("\n结束初始化");
        }
        sb.append("\n开始执行版本=" + revi);
        schemaRevisionManager.publishRevision(revi, 0);
        sb.append("\n开始执行版本=" + revi);
        return sb.toString();
    }

    @RequestMapping("/flywave-shard.json")
    @ResponseBody
    public String flywaveShard(@RequestParam("table") String table) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n开始分表=" + table + "，数量=2");
        schemaShardingManager.publishShard(table, 2);
        sb.append("\n结束分表=" + table + "，数量=2");

        sb.append("\n开始迁移数据=" + table + "，数量=2");
        schemaShardingManager.shardingData(table, true);
        sb.append("\n结束迁移数据=" + table + "，数量=2");

        return sb.toString();
    }

    @RequestMapping("/flywave-journal.json")
    @ResponseBody
    public String flywaveJournal(@RequestParam("table") String table, @RequestParam("enable") boolean enable) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n开始初始化跟踪配置=" + table);
        SchemaJournalManager.JournalDdl ddls = new SchemaJournalManager.JournalDdl(
                wingsFlywaveVerProperties.getJournalUpdate(),
                wingsFlywaveVerProperties.getTriggerUpdate(),
                wingsFlywaveVerProperties.getJournalDelete(),
                wingsFlywaveVerProperties.getTriggerDelete()
        );
        schemaJournalManager.checkAndInitDdl(table, ddls, 0);
        sb.append("\n结束初始化跟踪配置=" + table);

        // 开启关闭
        sb.append("\n开始更新追踪=" + table);
        schemaJournalManager.publishUpdate(table, enable, 0);
        sb.append("\n结束更新追踪=" + table);

        sb.append("\n开始删除追踪=" + table);
        schemaJournalManager.publishDelete(table, enable, 0);
        sb.append("\n结束删除追踪=" + table);

        return sb.toString();
    }
}
