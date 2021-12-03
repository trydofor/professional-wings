package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import static pro.fessional.wings.slardar.jackson.FormatNumberSerializer.Digital;

/**
 * 可定制Number的精度和格式
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
     * Integer,Long 类型
     */
    private Nf integer = new Nf();

    /**
     * Float,Double 类型
     */
    private Nf floats = new Nf();

    /**
     * BigDecimal 类型
     */
    private Nf decimal = new Nf();

    @Data
    public static class Nf {
        /*
         * 千分位用`,`占位，在separator替换，空表示无效
         */
        private DecimalFormat format = null;
        /*
         * 舍入模式，默认RoundingMode.FLOOR
         */
        private RoundingMode round = null;
        /*
         * 整数位分隔符，如千分位，可替换format中的`,`
         */
        private String separator = ",";

        /**
         * 是否忽略WRITE_NUMBERS_AS_STRINGS，强制写number，需要注意format
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
