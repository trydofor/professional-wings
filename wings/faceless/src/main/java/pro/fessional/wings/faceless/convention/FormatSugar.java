package pro.fessional.wings.faceless.convention;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * null friendly formatter
 *
 * @author trydofor
 * @since 2024-05-06
 */
public class FormatSugar {

    /**
     * format dt by ptn. return elz if dt as empty.
     */
    @Contract("_,_,!null->!null; !null,_,_->!null")
    public static String format(LocalTime dt, @NotNull DateTimeFormatter ptn, String elz) {
        return EmptySugar.asEmptyValue(dt) ? elz : ptn.format(dt);
    }

    /**
     * format dt by ptn. return elz if dt as empty.
     */
    @Contract("_,_,!null->!null; !null,_,_->!null")
    public static String format(LocalDate dt, @NotNull DateTimeFormatter ptn, String elz) {
        return EmptySugar.asEmptyValue(dt) ? elz : ptn.format(dt);
    }

    /**
     * format dt by ptn. return elz if dt as empty.
     */
    @Contract("_,_,!null->!null; !null,_,_->!null")
    public static String format(LocalDateTime dt, @NotNull DateTimeFormatter ptn, String elz) {
        return EmptySugar.asEmptyValue(dt) ? elz : ptn.format(dt);
    }

    /**
     * format dt by ptn. return elz if dt as empty.
     */
    @Contract("_,_,!null->!null; !null,_,_->!null")
    public static String format(ZonedDateTime dt, @NotNull DateTimeFormatter ptn, String elz) {
        return EmptySugar.asEmptyValue(dt) ? elz : ptn.format(dt);
    }

    /**
     * format dt by ptn. return elz if dt as empty.
     */
    @Contract("_,_,!null->!null; !null,_,_->!null")
    public static String format(OffsetDateTime dt, @NotNull DateTimeFormatter ptn, String elz) {
        return EmptySugar.asEmptyValue(dt) ? elz : ptn.format(dt);
    }


    /**
     * format nb by ptn. return elz if nb as empty.
     */
    @Contract("_,_,!null->!null; !null,_,_->!null")
    public static String format(Integer nb, @NotNull NumberFormat ptn, String elz) {
        return EmptySugar.asEmptyValue(nb) ? elz : ptn.format(nb);
    }

    /**
     * format nb by ptn. return elz if nb as empty.
     */
    @Contract("_,_,!null->!null; !null,_,_->!null")
    public static String format(Long nb, @NotNull NumberFormat ptn, String elz) {
        return EmptySugar.asEmptyValue(nb) ? elz : ptn.format(nb);
    }

    /**
     * format nb by ptn. return elz if nb as empty.
     */
    @Contract("_,_,!null->!null; !null,_,_->!null")
    public static String format(Double nb, @NotNull NumberFormat ptn, String elz) {
        return EmptySugar.asEmptyValue(nb) ? elz : ptn.format(nb);
    }

    /**
     * format nb by ptn. return elz if nb as empty.
     */
    @Contract("_,_,!null->!null; !null,_,_->!null")
    public static String format(Float nb, @NotNull NumberFormat ptn, String elz) {
        return EmptySugar.asEmptyValue(nb) ? elz : ptn.format(nb);
    }

    /**
     * format nb by ptn. return elz if nb as empty.
     */
    @Contract("_,_,!null->!null; !null,_,_->!null")
    public static String format(BigDecimal nb, @NotNull NumberFormat ptn, String elz) {
        return EmptySugar.asEmptyValue(nb) ? elz : ptn.format(nb);
    }

    /**
     * format nb by ptn. return elz if nb as empty.
     */
    @Contract("_,_,!null->!null; !null,_,_->!null")
    public static String format(BigInteger nb, @NotNull NumberFormat ptn, String elz) {
        return EmptySugar.asEmptyValue(nb) ? elz : ptn.format(nb);
    }
}
