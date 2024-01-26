package pro.fessional.wings.slardar.datatime;

import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * @author trydofor
 * @since 2021-05-18
 */
@Slf4j
public class SmartFormatterTest {

    @Test
    @TmsLink("C13007")
    public void infoFormat() {
        LocalDateTime d = LocalDateTime.of(2021, 1, 2, 3, 0, 0, 0);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy[-MM][-dd][ ][HH][:mm][:ss][ ][VV]");
        log.info("format={}",d.format(df));
    }

    @Test
    @TmsLink("C13008")
    public void testDatetime() {
        LocalDateTime d1 = LocalDateTime.of(2021, 1, 2, 3, 4, 5, 0);
        final String smPtn = "yyyy[-][/][.]M[-][/][.]d[ ]['T']H:m:s";
        assertDatetime(d1, "2021-1-2 3:4:5", smPtn);
        assertDatetime(d1, "2021-01-2 03:4:5", smPtn);
        assertDatetime(d1, "2021-01-02 03:04:05", smPtn);
        assertDatetime(d1, "2021-01-02T03:04:05", smPtn);
        assertDatetime(d1, "2021/01/02T03:04:05", smPtn);
        assertDatetime(d1, "2021.01/02T03:04:05", smPtn);

        final String usPtn = "[MMMM][MMM][M]/d/[yyyy][yy][ ]['T']H:m:s";
        assertDatetime(d1, "1/2/21 3:4:5", usPtn);
        assertDatetime(d1, "1/2/21 03:4:5", usPtn);
        assertDatetime(d1, "01/2/2021T3:4:5", usPtn);
        assertDatetime(d1, "01/2/2021T03:4:5", usPtn);

        assertDatetime(d1, "Jan/2/21 3:4:5", usPtn);
        assertDatetime(d1, "January/2/21 03:4:5", usPtn);
        assertDatetime(d1, "Jan/2/2021T3:4:5", usPtn);
        assertDatetime(d1, "January/2/2021T03:4:5", usPtn);
    }

    private void assertDatetime(LocalDateTime ldt, String date, String parser) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(parser);
        final LocalDateTime pd = LocalDateTime.parse(date, df);
        Assertions.assertEquals(ldt, pd);
    }

    final TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");

    @Test
    @TmsLink("C13009")
    public void testZoned() {
        TimeZone.setDefault(tz);
        LocalDateTime d1 = LocalDateTime.of(2021, 1, 2, 8, 4, 5, 0);
        final String smPtn = "yyyy[-][/][.]M[-][/][.]d[ ]['T']H:m:s[.SSS][XXXXX][XXXX][XXX][XX][X]['['][ ][VV][']']";
        assertZoned(d1, "2021-1-2 0:4:5Z", smPtn);
        assertZoned(d1, "2021-1-2 8:4:5Asia/Shanghai", smPtn);
        assertZoned(d1, "2021-1-2 8:4:5 Asia/Shanghai", smPtn);

        assertZoned(d1, "2021-1-2 0:4:5Z", smPtn);
        assertZoned(d1, "2021-1-2 8:4:5+0800", smPtn);
        assertZoned(d1, "2021-1-2 8:4:5+08:00[Asia/Shanghai]", smPtn);
        assertZoned(d1, "2021-1-2 8:4:5+08:00:00[Asia/Shanghai]", smPtn);
        assertZoned(d1, "2021-1-2 8:4:5[Asia/Shanghai]", smPtn);
    }

    private void assertZoned(LocalDateTime ldt, String date, String parser) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(parser);
        final ZonedDateTime pd = ZonedDateTime.parse(date, df);
        Assertions.assertEquals(ldt, pd.withZoneSameInstant(tz.toZoneId()).toLocalDateTime());
    }
}
