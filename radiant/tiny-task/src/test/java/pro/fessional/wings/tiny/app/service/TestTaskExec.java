package pro.fessional.wings.tiny.app.service;

import lombok.extern.slf4j.Slf4j;
import pro.fessional.mirana.time.DateLocaling;
import pro.fessional.mirana.time.Sleep;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.tiny.task.schedule.TinyTasker;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author trydofor
 * @since 2024-06-17
 */
@TinyTasker.Auto
@Slf4j
public class TestTaskExec {

    public static final List<Stat> CronExec = new ArrayList<>();
    public static final List<Stat> RateExec = new ArrayList<>();
    public static final List<Stat> IdleExec = new ArrayList<>();

    public static class Stat {
        public LocalDateTime exec;
        public LocalDateTime done;
        public long cost;
        public boolean fail;
    }

    public static final String TaskNameCron = "TestTaskExec#cron30s10";
    public static final String TaskNameRate = "TestTaskExec#rate30s10";
    public static final String TaskNameIdle = "TestTaskExec#idle30s10";


    @TinyTasker(cron = "*/30 * * * * *")
    public void cron30s10() {
        exec(CronExec, "cron30");
    }

    @TinyTasker(rate = 30)
    public void rate30s10() {
        exec(RateExec, "rate30");
    }

    @TinyTasker(idle = 30)
    public void idle30s10() {
        exec(IdleExec, "idle30");
    }

    private void exec(List<Stat> tst, String mtd) {
        final Stat stat = new Stat();
        tst.add(stat);
        stat.fail = tst.size() % 3 != 1;
        stat.exec = ThreadNow.localDateTime();

        log.info("TestTaskExec.{}", mtd);
        Sleep.ignoreInterrupt(10_000);

        stat.done = ThreadNow.localDateTime();
        stat.cost = DateLocaling.sysEpoch(stat.done) - DateLocaling.sysEpoch(stat.exec);

        if (stat.fail) {
            throw new RuntimeException("failed on not 1/3, time=" + tst.size());
        }
    }
}
