package pro.fessional.wings.slardar.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2021-07-05
 */
@SpringBootTest(properties =
        {"debug = true",
         "spring.wings.slardar.enabled.number=true",
         "wings.slardar.number.decimal.separator=_",
         "wings.slardar.number.floats.format=#.00",
         "wings.slardar.number.decimal.format=#.00",
        })
public class DecimalFormatTest {

    @Setter(onMethod_ = {@Autowired})
    private ObjectMapper objectMapper;

    @Test
    public void testFloat() {
        DecimalFormat df = new DecimalFormat("￥,####.00");
        DecimalFormatSymbols customSymbols = new DecimalFormatSymbols();
        customSymbols.setGroupingSeparator('_');
        df.setDecimalFormatSymbols(customSymbols);
        df.setRoundingMode(RoundingMode.FLOOR);
        assertEquals("￥10_0000.00", df.format(10_0000L));
        assertEquals("￥10_0000.12", df.format(10_0000.125D));
    }

    @Test
    public void testInteger() {
        DecimalFormat df = new DecimalFormat("#");
        DecimalFormatSymbols customSymbols = new DecimalFormatSymbols();
        customSymbols.setGroupingSeparator('_');
        df.setDecimalFormatSymbols(customSymbols);
        df.setRoundingMode(RoundingMode.FLOOR);
        assertEquals("100000", df.format(10_0000L));
        assertEquals("100000", df.format(10_0000D));
    }


    @Data
    public static class DecStr {
        private int intVal = 123456;
        private Integer intObj = intVal;

        private long longVal = 123456L;
        private Long longObj = longVal;

        private float floatVal = 123456.789F;
        private Float floatObj = floatVal;
        private double doubleVal = 123456.789D;
        private Double doubleObj = doubleVal;

        private BigDecimal decimalObj = new BigDecimal("123456.789");
    }

    @Data
    public static class DecFmt {
        @JsonFormat(pattern = ",##.0")
        private int intVal = 123456;
        @JsonFormat(pattern = ",##.0")
        private Integer intObj = intVal;
        @JsonFormat(pattern = ",###.0")
        private long longVal = 123456L;
        @JsonFormat(pattern = ",###.0")
        private Long longObj = longVal;
        @JsonFormat(pattern = ",####.0")
        private float floatVal = 123456.789F;
        @JsonFormat(pattern = ",####.0")
        private Float floatObj = floatVal;
        @JsonFormat(pattern = ",####.0")
        private double doubleVal = 123456.789D;
        @JsonFormat(pattern = ",####.0")
        private Double doubleObj = doubleVal;
        @JsonFormat(pattern = "￥,####.0")
        private BigDecimal decimalObj = new BigDecimal("123456.789");
    }

    @Data
    public static class DecRaw {
        @JsonRawValue()
        private int intVal = 123456;
        @JsonRawValue()
        private Integer intObj = intVal;
        @JsonRawValue()
        private long longVal = 123456L;
        @JsonRawValue()
        private Long longObj = longVal;
        @JsonRawValue()
        private float floatVal = 123456.789F;
        @JsonRawValue()
        private Float floatObj = floatVal;
        @JsonRawValue()
        private double doubleVal = 123456.789D;
        @JsonRawValue()
        private Double doubleObj = doubleVal;
        @JsonRawValue()
        private BigDecimal decimalObj = new BigDecimal("123456.789");
    }

    @Test
    public void testDecStr() throws JsonProcessingException {
        final String decStr = objectMapper.writeValueAsString(new DecStr());
        System.out.println(decStr);
        Assertions.assertEquals("{\"intVal\":123456,"
                                + "\"intObj\":123456,"
                                + "\"longVal\":123456,"
                                + "\"longObj\":123456,"
                                + "\"floatVal\":\"123456.78\","
                                + "\"floatObj\":\"123456.78\","
                                + "\"doubleVal\":\"123456.78\","
                                + "\"doubleObj\":\"123456.78\","
                                + "\"decimalObj\":\"123456.78\"}"
                , decStr);
    }

    @Test
    public void testDecRaw() throws JsonProcessingException {
        final String decRaw = objectMapper.writeValueAsString(new DecRaw());
        System.out.println(decRaw);
        Assertions.assertEquals("{\"intVal\":123456,"
                                + "\"intObj\":123456,"
                                + "\"longVal\":123456,"
                                + "\"longObj\":123456,"
                                + "\"floatVal\":123456.79,"
                                + "\"floatObj\":123456.79,"
                                + "\"doubleVal\":123456.789,"
                                + "\"doubleObj\":123456.789,"
                                + "\"decimalObj\":123456.789}"
                , decRaw);

    }

    @Test
    public void testDecFmt() throws JsonProcessingException {
        final String decFmt = objectMapper.writeValueAsString(new DecFmt());
        System.out.println(decFmt);
        Assertions.assertEquals("{\"intVal\":12,34,56.0,"
                                + "\"intObj\":12,34,56.0,"
                                + "\"longVal\":123,456.0,"
                                + "\"longObj\":123,456.0,"
                                + "\"floatVal\":\"12,3456.7\","
                                + "\"floatObj\":\"12,3456.7\","
                                + "\"doubleVal\":\"12,3456.7\","
                                + "\"doubleObj\":\"12,3456.7\","
                                + "\"decimalObj\":\"￥12_3456.7\"}"
                , decFmt);
    }
}
