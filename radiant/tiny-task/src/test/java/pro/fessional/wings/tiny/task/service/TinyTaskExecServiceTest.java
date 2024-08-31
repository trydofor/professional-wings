package pro.fessional.wings.tiny.task.service;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.page.PageQuery;
import pro.fessional.mirana.page.PageResult;
import pro.fessional.mirana.stat.JvmStat;
import pro.fessional.mirana.time.Sleep;
import pro.fessional.wings.tiny.app.service.TestTaskExec;
import pro.fessional.wings.tiny.task.database.autogen.tables.pojos.WinTaskResult;
import pro.fessional.wings.tiny.task.service.TinyTaskListService.Item;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * @author trydofor
 * @since 2024-06-17
 */
@SpringBootTest(properties = {
    "wings.slardar.ding-notice.default.access-token=",
    "wings.tiny.task.define[TinyTaskCheckHealth].timing-idle=20",
    "wings.tiny.task.define[TinyTaskCheckHealth].timing-tune=-5"
})
@DependsOnDatabaseInitialization
@Slf4j
class TinyTaskExecServiceTest {

    @Setter(onMethod_ = { @Autowired })
    protected TinyTaskListService tinyTaskListService;

    @Test
    @TmsLink("C15014")
    void run70sThenCheck() {
        Sleep.ignoreInterrupt(70 * 1000L);
        HashMap<String, Item> tasks = new HashMap<>();
        PageQuery pq = new PageQuery(1, 100);
        for (Item item : tinyTaskListService.listDefined(pq)) {
            tasks.put(item.getTaskerName(), item);
        }

        Item checkHealth = tasks.get("TinyTaskBeatServiceImpl#checkHealth");
        Assertions.assertNotNull(checkHealth);
        PageResult<WinTaskResult> checkHealthResult = tinyTaskListService.listResult(checkHealth.getId(), pq);

        Item execCron = tasks.get(TestTaskExec.TaskNameCron);
        Assertions.assertNotNull(execCron);
        PageResult<WinTaskResult> execCronResult = tinyTaskListService.listResult(execCron.getId(), pq);

        Item execRate = tasks.get(TestTaskExec.TaskNameRate);
        Assertions.assertNotNull(execRate);
        PageResult<WinTaskResult> execRateResult = tinyTaskListService.listResult(execRate.getId(), pq);

        Item execIdle = tasks.get(TestTaskExec.TaskNameIdle);
        Assertions.assertNotNull(execIdle);
        PageResult<WinTaskResult> execIdleResult = tinyTaskListService.listResult(execIdle.getId(), pq);

        check(checkHealthResult, 2, 20);
        check(execCronResult, TestTaskExec.CronExec.size(), 30);
        check(execRateResult, TestTaskExec.RateExec.size(), 30);
        check(execIdleResult, TestTaskExec.IdleExec.size(), 30 + 10);
    }

    private void check(PageResult<WinTaskResult> result, int size, int span) {
        Assertions.assertTrue(result.getSize() >= size);
        LocalDateTime prevExec = null;
        final int pid = JvmStat.jvmPid();
        for (WinTaskResult it : result) {
            if (it.getTaskPid() != pid) {
                continue;
            }
            LocalDateTime timeExec = it.getTimeExec();
            long spaned = 0;
            if (prevExec != null) {
                spaned = Duration.between(timeExec, prevExec).abs().toSeconds() + 2;
                log.info("WinTaskResult, id={}, exec={}, exit={}, fail={}, spaned={}", it.getTaskId(), timeExec, it.getTimeExit(), it.getExitFail(), spaned);
                Assertions.assertTrue(spaned >= span, "spaned=" + spaned + " vs span=" + span);
            }
            else {
                log.info("WinTaskResult, id={}, exec={}, exit={}, fail={}", it.getTaskId(), timeExec, it.getTimeExit(), it.getExitFail());
            }

            prevExec = timeExec;
        }
    }
}