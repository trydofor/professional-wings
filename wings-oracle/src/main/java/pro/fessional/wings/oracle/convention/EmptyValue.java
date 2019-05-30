package pro.fessional.wings.oracle.convention;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author trydofor
 * @since 2019-05-13
 */
public class EmptyValue {

    public static final int INT = 0;
    public static final long BIGINT = 0L;
    public static final double DOUBLE = 0.0D;
    public static final float FLOAT = 0.0F;

    @NotNull
    public static final String CHAR = "";
    @NotNull
    public static final String VARCHAR = CHAR;
    @NotNull
    public static final BigDecimal DECIMAL = new BigDecimal("0.00");
    @NotNull
    public static final LocalDate DATE = LocalDate.of(1000, 1, 1);
    @NotNull
    public static final LocalTime TIME = LocalTime.of(0, 0, 0, 0);
    @NotNull
    public static final LocalDateTime DATE_TIME = LocalDateTime.of(DATE, TIME);
}
