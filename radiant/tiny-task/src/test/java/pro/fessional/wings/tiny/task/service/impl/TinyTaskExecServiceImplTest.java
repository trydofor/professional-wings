package pro.fessional.wings.tiny.task.service.impl;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.fessional.mirana.time.DateLocaling;
import pro.fessional.wings.faceless.convention.EmptyValue;
import pro.fessional.wings.tiny.task.database.autogen.tables.pojos.WinTaskDefine;

import java.time.LocalDateTime;

/**
 * @author trydofor
 * @since 2024-08-03
 */
class TinyTaskExecServiceImplTest {

    @Test
    @TmsLink("C15021")
    void calcNextExec() {
        TinyTaskExecServiceImpl impl = new TinyTaskExecServiceImpl();
        WinTaskDefine td = new WinTaskDefine();
        td.setId(1L);
        td.setDuringExec(0);
        td.setDuringFail(0);
        td.setDuringDone(0);

        td.setTimingMiss(0L);
        td.setTimingTune(0);
        LocalDateTime last = LocalDateTime.of(2024, 8, 1, 8, 1);
        td.setLastExec(last);
        td.setLastExit(last.withSecond(30));


        td.setTimingCron("0 21 * * * *");
        td.setTimingIdle(0);
        td.setTimingRate(0);
        td.setTimingZone("");

        {
            // killed before next
            td.setNextExec(last.withHour(9)); // 2024-08-01 09:01
            long now = DateLocaling.sysEpoch(last.withMinute(20));  // 2024-08-01 08:20
            long next = impl.calcNextExec(td, now);
            LocalDateTime nxt = DateLocaling.sysLdt(next);
            Assertions.assertEquals(LocalDateTime.of(2024, 8, 1, 9, 1), nxt);
        }
        {
            // empty next
            td.setNextExec(EmptyValue.DATE_TIME);
            long now = DateLocaling.sysEpoch(last.withMinute(20));  // 2024-08-01 08:20
            long next = impl.calcNextExec(td, now);
            LocalDateTime nxt = DateLocaling.sysLdt(next);
            Assertions.assertEquals(LocalDateTime.of(2024, 8, 1, 8, 21), nxt);
        }
        {
            // 08:01___mis9___mis10___mis11
            //N0= 10:21
            //Now
            //Nx= 10:36 = 10:21 + :15 (25% N1-N0)
            //N1= 11:21
            td.setNextExec(last.withHour(9));  // 2024-08-01 09:01
            long now = DateLocaling.sysEpoch(last.withHour(10).withMinute(36));  // 2024-08-02 11:01
            long next = impl.calcNextExec(td, now);
            LocalDateTime nxt = DateLocaling.sysLdt(next);
            Assertions.assertEquals(LocalDateTime.of(2024, 8, 1, 10, 21), nxt);
        }
        {
            // 08:01___mis9___mis10___mis11
            //N0= 10:21
            //Nx= 10:36 = 10:21 + :15 (25% N1-N0)
            //Now
            //N1= 11:21
            td.setNextExec(last.withHour(9));  // 2024-08-01 09:01
            long now = DateLocaling.sysEpoch(last.withHour(10).withMinute(37));  // 2024-08-02 11:01
            long next = impl.calcNextExec(td, now);
            LocalDateTime nxt = DateLocaling.sysLdt(next);
            Assertions.assertEquals(LocalDateTime.of(2024, 8, 1, 11, 21), nxt);
        }

        td.setTimingCron("");
        td.setTimingIdle(0);
        td.setTimingRate(3600);
        td.setTimingZone("");

        {
            // killed before next
            td.setNextExec(last.withHour(9)); // 2024-08-01 09:01
            long now = DateLocaling.sysEpoch(last.withMinute(20));  // 2024-08-01 08:20
            long next = impl.calcNextExec(td, now);
            LocalDateTime nxt = DateLocaling.sysLdt(next);
            Assertions.assertEquals(LocalDateTime.of(2024, 8, 1, 9, 1), nxt);
        }
        {
            // empty next
            td.setNextExec(EmptyValue.DATE_TIME);
            long now = DateLocaling.sysEpoch(last.withMinute(20));  // 2024-08-01 08:20
            long next = impl.calcNextExec(td, now);
            LocalDateTime nxt = DateLocaling.sysLdt(next);
            Assertions.assertEquals(LocalDateTime.of(2024, 8, 1, 9, 1), nxt);
        }
        {
            // 08:01___mis9___mis10___mis11
            //N0= 10:01
            //Now
            //Nx= 10:16 = 10:01 + :15 (25% N1-N0)
            //N1= 11:01
            td.setNextExec(last.withHour(9));  // 2024-08-01 09:01
            long now = DateLocaling.sysEpoch(last.withHour(10).withMinute(16));  // 2024-08-02 11:01
            long next = impl.calcNextExec(td, now);
            LocalDateTime nxt = DateLocaling.sysLdt(next);
            Assertions.assertEquals(LocalDateTime.of(2024, 8, 1, 10, 1), nxt);
        }
        {
            // 08:01___mis9___mis10___mis11
            //N0= 10:01
            //Nx= 10:16 = 10:01 + :15 (25% N1-N0)
            //Now
            //N1= 11:01
            td.setNextExec(last.withHour(9));  // 2024-08-01 09:01
            long now = DateLocaling.sysEpoch(last.withHour(10).withMinute(17));  // 2024-08-02 11:01
            long next = impl.calcNextExec(td, now);
            LocalDateTime nxt = DateLocaling.sysLdt(next);
            Assertions.assertEquals(LocalDateTime.of(2024, 8, 1, 11, 1), nxt);
        }
    }
}