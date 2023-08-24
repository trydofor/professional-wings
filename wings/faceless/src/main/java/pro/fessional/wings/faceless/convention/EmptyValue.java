package pro.fessional.wings.faceless.convention;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author trydofor
 * @since 2019-05-13
 */
@SuppressWarnings("CanBeFinal")
public class EmptyValue {

    private EmptyValue() {
    }

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

    public static final LocalDate DATE_AS_MIN = DATE.minusDays(1);
    public static final LocalDate DATE_AS_MAX = DATE.plusDays(1);

    public static final LocalDateTime DATE_TIME_AS_MIN = LocalDateTime.of(DATE_AS_MIN, TIME);

    public static final LocalDateTime DATE_TIME_AS_MAX = LocalDateTime.of(DATE_AS_MAX, TIME);

    // Can be assigned externally to change the scope of asEmptyValue
    public static double DOUBLE_AS_MIN = -0.00001D;
    public static double DOUBLE_AS_MAX = 0.00001D;
    public static double FLOAT_AS_MIN = -0.00001F;
    public static double FLOAT_AS_MAX = 0.00001F;
    @NotNull
    public static BigDecimal DECIMAL_AS_MIN = new BigDecimal("-0.00001");
    @NotNull
    public static BigDecimal DECIMAL_AS_MAX = new BigDecimal("0.00001");

}
