package pro.fessional.wings.testing.silencer.data;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author trydofor
 * @since 2024-06-05
 */
@Data
public class CommonValue implements DefaultData<CommonValue> {

    public static final String PosPaiN15 = "3141592653589793";
    public static final String NegPaiN15 = "-3141592653589793";
    public static final String PosPaiD15 = "3.141592653589793";
    public static final String NegPaiD15 = "-3.141592653589793";
    public static final String Zero = "0";
    public static final String ZeroDot2 = "0.00";

    public static final ZoneId ZidUs = ZoneId.of("America/New_York");
    public static final ZoneId ZidJp = ZoneId.of("Asia/Tokyo");
    public static final ZoneId ZidUtc = ZoneId.of("UTC");

    public static final LocalDate LdValue = LocalDate.of(2023, 4, 5);
    public static final LocalTime LtValue = LocalTime.of(6, 7, 8, 0);
    public static final LocalDateTime LdtValue = LdValue.atTime(LtValue);
    public static final ZonedDateTime ZdtValueUs = ZonedDateTime.of(LdValue, LtValue, ZidUs);
    public static final ZonedDateTime ZdtValueJp = ZonedDateTime.of(LdValue, LtValue, ZidJp);
    public static final OffsetDateTime OdtValueUs = ZdtValueUs.toOffsetDateTime();
    public static final OffsetDateTime OdtValueJp = ZdtValueJp.toOffsetDateTime();

    private String strNull = null;
    private String strEmpty = null;
    private String strPai = null;
    private BigDecimal bigdecNull = null;
    private BigDecimal bigdecZero2 = null;
    private BigDecimal bigdecPaiPos = null;
    private BigDecimal bigdecPaiNeg = null;
    private BigInteger bigintNull = null;
    private BigInteger bigintZero = null;
    private BigInteger bigintPaiPos = null;
    private BigInteger bigintPaiNeg = null;
    private LocalDate localDateNull = null;
    private LocalDate localDateValue = null;
    private LocalTime localTimeNull = null;
    private LocalTime localTimeValue = null;
    private LocalDateTime localDateTimeNull = null;
    private LocalDateTime localDateTimeValue = null;
    private ZonedDateTime zoneDateTimeNull = null;
    private ZonedDateTime zoneDateTimeValueUs = null;
    private ZonedDateTime zoneDateTimeValueJp = null;
    private OffsetDateTime offsetDateTimeNull = null;
    private OffsetDateTime offsetDateTimeValueUs = null;
    private OffsetDateTime offsetDateTimeValueJp = null;

    @Override
    public CommonValue defaults() {
        this.strNull = null;
        this.strEmpty = "";
        this.strPai = PosPaiD15;
        this.bigdecNull = null;
        this.bigdecZero2 = new BigDecimal(ZeroDot2);
        this.bigdecPaiPos = new BigDecimal(PosPaiD15);
        this.bigdecPaiNeg = new BigDecimal(NegPaiD15);
        this.bigintNull = null;
        this.bigintZero = new BigInteger(Zero);
        this.bigintPaiPos = new BigInteger(PosPaiN15);
        this.bigintPaiNeg = new BigInteger(NegPaiN15);
        this.localDateNull = null;
        this.localDateValue = LdValue;
        this.localTimeNull = null;
        this.localTimeValue = LtValue;
        this.localDateTimeNull = null;
        this.localDateTimeValue = LdtValue;
        this.zoneDateTimeNull = null;
        this.zoneDateTimeValueUs = ZdtValueUs;
        this.zoneDateTimeValueJp = ZdtValueJp;
        this.offsetDateTimeNull = null;
        this.offsetDateTimeValueUs = OdtValueUs;
        this.offsetDateTimeValueJp = OdtValueJp;
        return this;
    }
}
