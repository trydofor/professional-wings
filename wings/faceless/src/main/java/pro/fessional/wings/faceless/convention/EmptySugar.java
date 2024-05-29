package pro.fessional.wings.faceless.convention;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

/**
 * `isXxx/notXxx` for exact comparison, `asXxx/nonXxx` for range comparison
 *
 * @author trydofor
 * @since 2019-05-13
 */
public class EmptySugar {

    public static boolean isEmptyValue(String v) {
        return v == null || v.equals(EmptyValue.VARCHAR);
    }

    public static boolean isEmptyValue(Integer v) {
        return v == null || v == EmptyValue.INT;
    }

    public static boolean isEmptyValue(int v) {
        return v == EmptyValue.INT;
    }

    public static boolean isEmptyValue(Long v) {
        return v == null || v == EmptyValue.BIGINT;
    }

    public static boolean isEmptyValue(long v) {
        return v == EmptyValue.BIGINT;
    }

    public static boolean isEmptyValue(Double v) {
        return v == null || v == EmptyValue.DOUBLE;
    }

    public static boolean isEmptyValue(double v) {
        return v == EmptyValue.DOUBLE;
    }

    public static boolean isEmptyValue(Float v) {
        return v == null || v == EmptyValue.FLOAT;
    }

    public static boolean isEmptyValue(float v) {
        return v == EmptyValue.FLOAT;
    }

    public static boolean isEmptyValue(BigDecimal v) {
        return v == null || v.equals(EmptyValue.DECIMAL);
    }

    public static boolean isEmptyValue(BigInteger v) {
        return v == null || v.intValue() == EmptyValue.INT;
    }

    public static boolean isEmptyValue(LocalDate v) {
        return v == null || v.equals(EmptyValue.DATE);
    }

    public static boolean isEmptyValue(LocalTime v) {
        return v == null || v.equals(EmptyValue.TIME);
    }

    public static boolean isEmptyValue(LocalDateTime v) {
        return v == null || v.equals(EmptyValue.DATE_TIME);
    }

    public static boolean isEmptyValue(ZonedDateTime v) {
        return v == null || isEmptyValue(v.toLocalDateTime());
    }

    public static boolean isEmptyValue(OffsetDateTime v) {
        return v == null || isEmptyValue(v.toLocalDateTime());
    }

    public static boolean notEmptyValue(String v) {
        return !isEmptyValue(v);
    }

    public static boolean notEmptyValue(Integer v) {
        return !isEmptyValue(v);
    }

    public static boolean notEmptyValue(int v) {
        return !isEmptyValue(v);
    }

    public static boolean notEmptyValue(Long v) {
        return !isEmptyValue(v);
    }

    public static boolean notEmptyValue(long v) {
        return !isEmptyValue(v);
    }

    public static boolean notEmptyValue(Double v) {
        return !isEmptyValue(v);
    }

    public static boolean notEmptyValue(double v) {
        return !isEmptyValue(v);
    }

    public static boolean notEmptyValue(Float v) {
        return !isEmptyValue(v);
    }

    public static boolean notEmptyValue(float v) {
        return !isEmptyValue(v);
    }

    public static boolean notEmptyValue(BigDecimal v) {
        return !isEmptyValue(v);
    }

    public static boolean notEmptyValue(BigInteger v) {
        return !isEmptyValue(v);
    }

    public static boolean notEmptyValue(LocalDate v) {
        return !isEmptyValue(v);
    }

    public static boolean notEmptyValue(LocalTime v) {
        return !isEmptyValue(v);
    }

    public static boolean notEmptyValue(LocalDateTime v) {
        return !isEmptyValue(v);
    }

    public static boolean notEmptyValue(ZonedDateTime v) {
        return !isEmptyValue(v);
    }

    public static boolean notEmptyValue(OffsetDateTime v) {
        return !isEmptyValue(v);
    }

    // /////////////////////

    public static boolean asEmptyValue(String v) {
        return v == null || v.trim().isEmpty();
    }

    public static boolean asEmptyValue(Integer v) {
        return v == null || v == EmptyValue.INT;
    }

    public static boolean asEmptyValue(int v) {
        return v == EmptyValue.INT;
    }

    public static boolean asEmptyValue(Long v) {
        return v == null || v == EmptyValue.BIGINT;
    }

    public static boolean asEmptyValue(long v) {
        return v == EmptyValue.BIGINT;
    }

    public static boolean asEmptyValue(Double x) {
        if (x == null) return true;
        double v = x;
        return v > EmptyValue.DOUBLE_AS_MIN && v < EmptyValue.DOUBLE_AS_MAX;
    }

    public static boolean asEmptyValue(double v) {
        return v > EmptyValue.DOUBLE_AS_MIN && v < EmptyValue.DOUBLE_AS_MAX;
    }

    public static boolean asEmptyValue(Float x) {
        if (x == null) return true;
        float v = x;
        return v > EmptyValue.FLOAT_AS_MIN && v < EmptyValue.FLOAT_AS_MAX;
    }

    public static boolean asEmptyValue(float v) {
        return v > EmptyValue.FLOAT_AS_MIN && v < EmptyValue.FLOAT_AS_MAX;
    }

    public static boolean asEmptyValue(BigDecimal v) {
        return v == null || (v.compareTo(EmptyValue.DECIMAL_AS_MIN) > 0 && v.compareTo(EmptyValue.DECIMAL_AS_MAX) < 0);
    }

    public static boolean asEmptyValue(BigInteger v) {
        return v == null || v.intValue() == EmptyValue.INT;
    }

    /**
     * Consider time zone, ±24H
     */
    public static boolean asEmptyValue(LocalDate v) {
        return v == null ||
               EmptyValue.DATE.equals(v) ||
               EmptyValue.DATE_AS_MIN.equals(v) ||
               EmptyValue.DATE_AS_MAX.equals(v);
    }

    /**
     * Compare hour, minute and second only, without the time below the second
     */
    public static boolean asEmptyValue(LocalTime v) {
        return v == null ||
               (v.getHour() == EmptyValue.TIME.getHour()
                && v.getMinute() == EmptyValue.TIME.getMinute()
                && v.getSecond() == EmptyValue.TIME.getSecond());
    }

    /**
     * Compare date only, without time
     */
    public static boolean asEmptyValue(LocalDateTime v) {
        return v == null || asEmptyValue(v.toLocalDate());
    }

    /**
     * Compare date only, without time
     */
    public static boolean asEmptyValue(ZonedDateTime v) {
        return v == null || asEmptyValue(v.toLocalDate());
    }

    /**
     * Compare date only, without time
     */
    public static boolean asEmptyValue(OffsetDateTime v) {
        return v == null || asEmptyValue(v.toLocalDate());
    }


    public static boolean nonEmptyValue(String v) {
        return !asEmptyValue(v);
    }

    public static boolean nonEmptyValue(Integer v) {
        return !asEmptyValue(v);
    }

    public static boolean nonEmptyValue(int v) {
        return !asEmptyValue(v);
    }

    public static boolean nonEmptyValue(Long v) {
        return !asEmptyValue(v);
    }

    public static boolean nonEmptyValue(long v) {
        return !asEmptyValue(v);
    }

    public static boolean nonEmptyValue(Double x) {
        return !asEmptyValue(x);
    }

    public static boolean nonEmptyValue(double x) {
        return !asEmptyValue(x);
    }

    public static boolean nonEmptyValue(Float x) {
        return !asEmptyValue(x);
    }

    public static boolean nonEmptyValue(float x) {
        return !asEmptyValue(x);
    }

    public static boolean nonEmptyValue(BigDecimal v) {
        return !asEmptyValue(v);
    }

    public static boolean nonEmptyValue(BigInteger v) {
        return !asEmptyValue(v);
    }

    /**
     * Consider time zone, ±24H
     */
    public static boolean nonEmptyValue(LocalDate v) {
        return !asEmptyValue(v);
    }

    /**
     * Compare hour, minute and second only, without the time below the second
     */
    public static boolean nonEmptyValue(LocalTime v) {
        return !asEmptyValue(v);
    }

    /**
     * Compare date only, without time
     */
    public static boolean nonEmptyValue(LocalDateTime v) {
        return !asEmptyValue(v);
    }

    /**
     * Compare date only, without time
     */
    public static boolean nonEmptyValue(ZonedDateTime v) {
        return !asEmptyValue(v);
    }

    /**
     * Compare date only, without time
     */
    public static boolean nonEmptyValue(OffsetDateTime v) {
        return !asEmptyValue(v);
    }


    // /////////////////////

    public static String nullToEmpty(String v) {
        return v == null ? EmptyValue.VARCHAR : v;
    }

    public static Integer nullToEmpty(Integer v) {
        return v == null ? EmptyValue.INT : v;
    }

    public static Long nullToEmpty(Long v) {
        return v == null ? EmptyValue.BIGINT : v;
    }

    public static Double nullToEmpty(Double v) {
        return v == null ? EmptyValue.DOUBLE : v;
    }

    public static Float nullToEmpty(Float v) {
        return v == null ? EmptyValue.FLOAT : v;
    }

    public static BigDecimal nullToEmpty(BigDecimal v) {
        return v == null ? EmptyValue.DECIMAL : v;
    }

    public static LocalDate nullToEmpty(LocalDate v) {
        return v == null ? EmptyValue.DATE : v;
    }

    public static LocalTime nullToEmpty(LocalTime v) {
        return v == null ? EmptyValue.TIME : v;
    }

    public static LocalDateTime nullToEmpty(LocalDateTime v) {
        return v == null ? EmptyValue.DATE_TIME : v;
    }

    public static boolean nullToTrue(Boolean v) {
        return v == null || v;
    }

    public static boolean nullToFalse(Boolean v) {
        return v != null && v;
    }

    // /////////////////////

    public static String emptyToNull(String v) {
        return asEmptyValue(v) ? null : v;
    }

    public static Integer emptyToNull(Integer v) {
        return asEmptyValue(v) ? null : v;
    }

    public static Integer emptyToNull(int v) {
        return asEmptyValue(v) ? null : v;
    }

    public static Long emptyToNull(Long v) {
        return asEmptyValue(v) ? null : v;
    }

    public static Long emptyToNull(long v) {
        return asEmptyValue(v) ? null : v;
    }

    public static Double emptyToNull(Double v) {
        return asEmptyValue(v) ? null : v;
    }


    public static Double emptyToNull(double v) {
        return asEmptyValue(v) ? null : v;
    }

    public static Float emptyToNull(Float v) {
        return asEmptyValue(v) ? null : v;
    }

    public static Float emptyToNull(float v) {
        return asEmptyValue(v) ? null : v;
    }

    public static BigDecimal emptyToNull(BigDecimal v) {
        return asEmptyValue(v) ? null : v;
    }

    public static BigInteger emptyToNull(BigInteger v) {
        return asEmptyValue(v) ? null : v;
    }

    public static LocalDate emptyToNull(LocalDate v) {
        return asEmptyValue(v) ? null : v;
    }

    public static LocalTime emptyToNull(LocalTime v) {
        return asEmptyValue(v) ? null : v;
    }

    public static LocalDateTime emptyToNull(LocalDateTime v) {
        return asEmptyValue(v) ? null : v;
    }

    public static ZonedDateTime emptyToNull(ZonedDateTime v) {
        return asEmptyValue(v) ? null : v;
    }


    public static OffsetDateTime emptyToNull(OffsetDateTime v) {
        return asEmptyValue(v) ? null : v;
    }

    public static Boolean emptyToNull(Boolean v) {
        return nullToTrue(v) ? null : v;
    }
}
