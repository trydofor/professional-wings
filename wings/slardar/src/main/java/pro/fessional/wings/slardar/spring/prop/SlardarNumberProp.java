package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import static pro.fessional.wings.slardar.jackson.FormatNumberSerializer.Digital;

/**
 * Customizable precision and format of Number, support JsonFormat pattern.
 * Must use BigDecimal instead of Float and Double to avoid precision loss.
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SlardarNumberProp.Key)
public class SlardarNumberProp {

    public static final String Key = "wings.slardar.number";

    /**
     * format of Integer, Long. `empty` means disable.
     * The thousandth separator uses `,`, which can be replaced to other
     * at runtime according to the separator setting.
     */
    private Nf integer = new Nf();

    /**
     * Float, Double
     */
    private Nf floats = new Nf();

    /**
     * BigDecimal, BigInteger
     */
    private Nf decimal = new Nf();

    @Data
    public static class Nf {
        /*
         * format of Integer, Long. `empty` means disable.
         * The thousandth separator uses `,`, which can be replaced to other
         * at runtime according to the separator setting.
         */
        private DecimalFormat format = null;
        /*
         * RoundingMode.FLOOR
         */
        private RoundingMode round = null;
        /*
         * When Shape==ANY, integer separator, eg. thousandths.
         */
        private String separator = ",";

        /**
         * <pre>
         * whether the value is output as a string or a number in js
         *
         * `auto` - auto-match, number below 52bit, string above
         * `true` - force number, ignore WRITE_NUMBERS_AS_STRINGS
         * `false` - force string, avoid loss of precision.
         *
         * Whether to ignore WRITE_NUMBERS_AS_STRINGS, force to write number, need to pay attention to the
         * format compatibility. For example, using bigint in js and setting is auto, the boundary (inclusive)
         * will automatically switch between number and string.
         * </pre>
         */
        private Digital digital = Digital.False;

        public boolean isEnable() {
            return format != null;
        }

        public DecimalFormat getWellFormat() {
            if (format == null) return null;
            if (StringUtils.hasText(separator) && separator.charAt(0) != ',') {
                DecimalFormatSymbols customSymbols = new DecimalFormatSymbols();
                customSymbols.setGroupingSeparator(separator.charAt(0));
                format.setDecimalFormatSymbols(customSymbols);
            }
            if (round != null) {
                format.setRoundingMode(round);
            }
            return format;
        }
    }
}
