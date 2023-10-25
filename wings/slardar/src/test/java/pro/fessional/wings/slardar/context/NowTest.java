package pro.fessional.wings.slardar.context;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;

import static pro.fessional.wings.slardar.context.TerminalAttribute.TerminalAddr;
import static pro.fessional.wings.slardar.context.TerminalAttribute.TerminalAgent;

/**
 * @author trydofor
 * @since 2022-10-10
 */
@SpringBootTest(properties = {
        "wings.silencer.tweak.clock-offset = " + NowTest.Offset,
        "wings.silencer.i18n.zoneid=" + NowTest.Cn})
class NowTest {

    public static final String Cn = "Asia/Shanghai";
    public static final long Offset = 60_000;

    @Test
    @TmsLink("C13005")
    void initClock() {
        long sysMs = System.currentTimeMillis();
        long nowMs = Now.millis();
        long ofs = nowMs - sysMs;
        System.out.println("offset=" + ofs);
        Assertions.assertTrue(ofs >= Offset);
    }

    @Test
    @TmsLink("C13006")
    void clientClock() {
        ZoneId jp = ZoneId.of("Asia/Tokyo");
        TerminalContext.Builder builder = new TerminalContext.Builder()
                .locale(Locale.US)
                .timeZone(jp)
                .terminal(TerminalAddr, "localhost")
                .terminal(TerminalAgent, "Test")
                .user(1);
        TerminalContext.login(builder.build());

        ZonedDateTime szd = Now.zonedDateTime();
        ZonedDateTime czd = Now.clientZonedDateTime();

        System.out.println("Asia/Shanghai szd=" + szd);
        System.out.println("Asia/Tokyo czd=" + czd);

        final LocalDateTime sld = szd.toLocalDateTime();
        final LocalDateTime cld = czd.toLocalDateTime();
        final Duration dur = Duration.between(sld, cld);
        System.out.println("PT1H dur=" + dur);

        Assertions.assertEquals(jp, czd.getZone());
        Assertions.assertEquals(ZoneId.of(Cn), szd.getZone());
        Assertions.assertTrue(dur.getSeconds() >= 3600);
    }
}
