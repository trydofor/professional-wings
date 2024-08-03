package pro.fessional.wings.tiny.task.service.impl;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.tiny.task.database.autogen.tables.pojos.WinTaskDefine;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author trydofor
 * @since 2024-08-02
 */
class TinyTaskBeatServiceImplTest {

    @Test
    @TmsLink("C15020")
    void calcBeatMills() {
        TinyTaskBeatServiceImpl bs = new TinyTaskBeatServiceImpl();
        WinTaskDefine td = new WinTaskDefine();
        td.setLastExec(LocalDateTime.parse("2024-07-31T02:01:00"));
        td.setTimingBeat(0);
        td.setTimingCron("0 1 2 * * *");
        td.setTimingZone("");
        long now = ThreadNow.millis();
        long next = bs.calcBeatMills(td, now);
        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(next), ZoneId.systemDefault());
        LocalDateTime nxt2 = LocalDateTime.parse("2024-08-02T02:00:00");
        Assertions.assertTrue(ldt.isAfter(nxt2), ldt + " > " + nxt2);

        td.setLastExec(LocalDateTime.ofInstant(Instant.ofEpochMilli(now), ZoneId.systemDefault()));
        td.setTimingRate(60);
        td.setTimingIdle(50);
        td.setTimingCron("");
        long nr2 = bs.calcBeatMills(td, now);
        long df = (nr2 - now) / 1000;
        Assertions.assertTrue(df > 120 - 2, "s=" + df);
    }
}