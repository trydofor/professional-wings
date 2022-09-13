package pro.fessional.wings.faceless.convention;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
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

    public static boolean isEmptyValue(Long v) {
        return v == null || v == EmptyValue.BIGINT;
    }

    public static boolean isEmptyValue(Double v) {
        return v == null || v == EmptyValue.DOUBLE;
    }

    public static boolean isEmptyValue(Float v) {
        return v == null || v == EmptyValue.FLOAT;
    }

    public static boolean isEmptyValue(BigDecimal v) {
        return v == null || v.equals(EmptyValue.DECIMAL);
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

    // ///
    public static boolean asEmptyValue(String v) {
        return v == null || v.trim().isEmpty();
    }

    public static boolean asEmptyValue(Integer v) {
        return v == null || v == EmptyValue.INT;
    }

    public static boolean asEmptyValue(Long v) {
        return v == null || v == EmptyValue.BIGINT;
    }

    public static boolean asEmptyValue(Double x) {
        if (x == null) return true;
        double v = x;
        return v > EmptyValue.DOUBLE_AS_MIN && v < EmptyValue.DOUBLE_AS_MAX;
    }

    public static boolean asEmptyValue(Float x) {
        if (x == null) return true;
        float v = x;
        return v > EmptyValue.FLOAT_AS_MIN && v < EmptyValue.FLOAT_AS_MAX;
    }

    public static boolean asEmptyValue(BigDecimal v) {
        return v == null || (v.compareTo(EmptyValue.DECIMAL_AS_MIN) > 0 && v.compareTo(EmptyValue.DECIMAL_AS_MAX) < 0);
    }

    public static boolean asEmptyValue(LocalDate v) {
        return v == null ||
                (v.getYear() == EmptyValue.DATE.getYear()
                        && v.getMonth() == EmptyValue.DATE.getMonth()
                        && v.getDayOfMonth() == EmptyValue.DATE.getDayOfMonth());
    }

    public static boolean asEmptyValue(LocalTime v) {
        return v == null ||
                (v.getHour() == EmptyValue.TIME.getHour()
                        && v.getMinute() == EmptyValue.TIME.getMinute()
                        && v.getSecond() == EmptyValue.TIME.getSecond());
    }

    /**
     * 仅比较日期，不比较时间
     */
    public static boolean asEmptyValue(LocalDateTime v) {
        return v == null || asEmptyValue(v.toLocalDate());
    }

    //
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

    //
    public static String emptyToNull(String v) {
        return asEmptyValue(v) ? null : v;
    }

    public static Integer emptyToNull(Integer v) {
        return asEmptyValue(v) ? null : v;
    }

    public static Long emptyToNull(Long v) {
        return asEmptyValue(v) ? null : v;
    }

    public static Double emptyToNull(Double v) {
        return asEmptyValue(v) ? null : v;
    }

    public static Float emptyToNull(Float v) {
        return asEmptyValue(v) ? null : v;
    }

    public static BigDecimal emptyToNull(BigDecimal v) {
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

    public static Boolean emptyToNull(Boolean v) {
        return nullToTrue(v) ? null : v;
    }
}
