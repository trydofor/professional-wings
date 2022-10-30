package pro.fessional.wings.slardar.context;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;

/**
 * @author trydofor
 * @since 2022-10-10
 */
@SpringBootTest(properties = {
        "wings.silencer.debug.clock-offset = " + NowTest.Offset,
        "wings.silencer.i18n.zoneid=" + NowTest.Cn})
class NowTest {

    public static final String Cn = "Asia/Shanghai";
    public static final long Offset = 60_000;

    @Test
    void initClock() {
        long sysMs = System.currentTimeMillis();
        long nowMs = Now.millis();
        long ofs = nowMs - sysMs;
        System.out.println("offset=" + ofs);
        Assertions.assertTrue(ofs >= Offset);
    }

    @Test
    void clientClock() {
        ZoneId jp = ZoneId.of("Asia/Tokyo");
        TerminalContext.login()
                       .withLocale(Locale.US)
                       .withTimeZone(jp)
                       .withRemoteIp("localhost")
                       .withAgentInfo("Test")
                       .asUser(1);

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
