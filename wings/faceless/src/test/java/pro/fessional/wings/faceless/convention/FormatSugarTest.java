package pro.fessional.wings.faceless.convention;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Test;
import pro.fessional.mirana.time.ThreadNow;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pro.fessional.mirana.time.DateFormatter.FMT_DATE_10;
import static pro.fessional.mirana.time.DateFormatter.FMT_FULL_19;
import static pro.fessional.mirana.time.DateFormatter.FMT_TIME_08;

/**
 * @author trydofor
 * @since 2024-05-06
 */
class FormatSugarTest {

    @Test
    @TmsLink("C12149")
    void format() {
        LocalDateTime ldt = LocalDateTime.of(2023, 5, 6, 7, 8, 9);
        LocalDate ld = ldt.toLocalDate();
        LocalTime lt = ldt.toLocalTime();
        String elz = "-";

        assertEquals("07:08:09", FormatSugar.format(lt, FMT_TIME_08, elz));
        assertEquals(elz, FormatSugar.format((LocalTime) null, FMT_TIME_08, elz));
        assertEquals(elz, FormatSugar.format(EmptyValue.TIME, FMT_TIME_08, elz));

        assertEquals("2023-05-06", FormatSugar.format(ld, FMT_DATE_10, elz));
        assertEquals(elz, FormatSugar.format((LocalDate) null, FMT_DATE_10, elz));
        assertEquals(elz, FormatSugar.format(EmptyValue.DATE, FMT_DATE_10, elz));

        assertEquals("2023-05-06 07:08:09", FormatSugar.format(ldt, FMT_FULL_19, elz));
        assertEquals(elz, FormatSugar.format((LocalDateTime) null, FMT_FULL_19, elz));
        assertEquals(elz, FormatSugar.format(EmptyValue.DATE_TIME, FMT_FULL_19, elz));

        ZonedDateTime zdt = ldt.atZone(ThreadNow.utcZoneId());
        assertEquals("2023-05-06 07:08:09", FormatSugar.format(zdt, FMT_FULL_19, elz));
        assertEquals(elz, FormatSugar.format((ZonedDateTime) null, FMT_FULL_19, elz));
        assertEquals(elz, FormatSugar.format(EmptyValue.DATE_TIME.atZone(ThreadNow.utcZoneId()), FMT_FULL_19, elz));

        OffsetDateTime odt = zdt.toOffsetDateTime();
        assertEquals("2023-05-06 07:08:09", FormatSugar.format(odt, FMT_FULL_19, elz));
        assertEquals(elz, FormatSugar.format((OffsetDateTime) null, FMT_FULL_19, elz));
        assertEquals(elz, FormatSugar.format(EmptyValue.DATE_TIME.atZone(ThreadNow.utcZoneId()).toOffsetDateTime(), FMT_FULL_19, elz));

        DecimalFormat nf = new DecimalFormat("#");
        assertEquals("1", FormatSugar.format(1, nf, elz));
        assertEquals(elz, FormatSugar.format((Integer) null, nf, elz));
        assertEquals(elz, FormatSugar.format(EmptyValue.INT, nf, elz));

        assertEquals("1", FormatSugar.format(1L, nf, elz));
        assertEquals(elz, FormatSugar.format((Long) null, nf, elz));
        assertEquals(elz, FormatSugar.format(EmptyValue.BIGINT, nf, elz));

        assertEquals("1", FormatSugar.format(1.0F, nf, elz));
        assertEquals(elz, FormatSugar.format((Float) null, nf, elz));
        assertEquals(elz, FormatSugar.format(EmptyValue.FLOAT, nf, elz));

        assertEquals("1", FormatSugar.format(1.0D, nf, elz));
        assertEquals(elz, FormatSugar.format((Double) null, nf, elz));
        assertEquals(elz, FormatSugar.format(EmptyValue.DOUBLE, nf, elz));

        assertEquals("1", FormatSugar.format(BigDecimal.ONE, nf, elz));
        assertEquals(elz, FormatSugar.format((BigDecimal) null, nf, elz));
        assertEquals(elz, FormatSugar.format(EmptyValue.DECIMAL, nf, elz));

        assertEquals("1", FormatSugar.format(BigInteger.ONE, nf, elz));
        assertEquals(elz, FormatSugar.format((BigInteger) null, nf, elz));
        assertEquals(elz, FormatSugar.format(new BigInteger("0"), nf, elz));

    }
}