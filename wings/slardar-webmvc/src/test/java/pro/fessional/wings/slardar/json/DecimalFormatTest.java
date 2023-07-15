package pro.fessional.wings.slardar.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2021-07-05
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.wings.slardar.enabled.number=true",
                "wings.slardar.number.decimal.separator=_",
                "wings.slardar.number.floats.format=#.00",
                "wings.slardar.number.decimal.format=#.00",
        })
@Slf4j
public class DecimalFormatTest {

    @Setter(onMethod_ = {@Autowired})
    private ObjectMapper objectMapper;

    @Setter(onMethod_ = {@Autowired})
    private RestTemplate restTemplate;

    @Setter(onMethod_ = {@Value("http://localhost:${local.server.port}")})
    private String domain;


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
        private BigInteger integerObj = new BigInteger("123456789");
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
        @JsonFormat(pattern = "￥,####.0", shape = JsonFormat.Shape.STRING)
        private BigDecimal decimalShp = new BigDecimal("123456.789");
        @JsonFormat(pattern = "￥,####.0")
        private BigInteger integerObj = new BigInteger("123456789");
        @JsonFormat(pattern = "￥,####.0", shape = JsonFormat.Shape.STRING)
        private BigInteger integerShp = new BigInteger("123456789");
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
        @JsonRawValue()
        private BigInteger integerObj = new BigInteger("123456789");
    }

    @Test
    public void testDecStr() throws JsonProcessingException {
        final String decStr = objectMapper.writeValueAsString(new DecStr());
        log.info(decStr);
        Assertions.assertEquals("{\"intVal\":123456,"
                                + "\"intObj\":123456,"
                                + "\"longVal\":123456,"
                                + "\"longObj\":123456,"
                                + "\"floatVal\":\"123456.78\","
                                + "\"floatObj\":\"123456.78\","
                                + "\"doubleVal\":\"123456.78\","
                                + "\"doubleObj\":\"123456.78\","
                                + "\"decimalObj\":\"123456.78\","
                                + "\"integerObj\":\"123456789.00\"}"
                , decStr);
    }

    @Test
    public void testDecRaw() throws JsonProcessingException {
        final String decRaw = objectMapper.writeValueAsString(new DecRaw());
        log.info(decRaw);
        Assertions.assertEquals("{\"intVal\":123456,"
                                + "\"intObj\":123456,"
                                + "\"longVal\":123456,"
                                + "\"longObj\":123456,"
                                + "\"floatVal\":123456.79,"
                                + "\"floatObj\":123456.79,"
                                + "\"doubleVal\":123456.789,"
                                + "\"doubleObj\":123456.789,"
                                + "\"decimalObj\":123456.789,"
                                + "\"integerObj\":123456789}"
                , decRaw);

    }

    @Test
    public void testDecFmt() throws JsonProcessingException {
        final String decFmt = objectMapper.writeValueAsString(new DecFmt());
        log.info(decFmt);
        Assertions.assertEquals("{\"intVal\":12,34,56.0,"
                                + "\"intObj\":12,34,56.0,"
                                + "\"longVal\":123,456.0,"
                                + "\"longObj\":123,456.0,"
                                + "\"floatVal\":\"12,3456.7\","
                                + "\"floatObj\":\"12,3456.7\","
                                + "\"doubleVal\":\"12,3456.7\","
                                + "\"doubleObj\":\"12,3456.7\","
                                + "\"decimalObj\":\"￥12_3456.7\","
                                + "\"decimalShp\":\"￥12,3456.7\","
                                + "\"integerObj\":\"￥1_2345_6789.0\","
                                + "\"integerShp\":\"￥1,2345,6789.0\"}"
                , decFmt);
    }

    @Test
    public void testJsSafe() throws JsonProcessingException {
        TreeMap<String, Long> js = new TreeMap<>();
        js.put("maxSafe0", 9007199254740990L);
        js.put("maxSafe1", 9007199254740991L);
        js.put("maxSafe2", 9007199254740992L);
        js.put("minSafe0", -9007199254740990L);
        js.put("minSafe1", -9007199254740991L);
        js.put("minSafe2", -9007199254740992L);
        final String jsFmt = objectMapper.writeValueAsString(js);
        log.info(jsFmt);
        Assertions.assertEquals("{\"maxSafe0\":9007199254740990,"
                                + "\"maxSafe1\":\"9007199254740991\","
                                + "\"maxSafe2\":\"9007199254740992\","
                                + "\"minSafe0\":-9007199254740990,"
                                + "\"minSafe1\":\"-9007199254740991\","
                                + "\"minSafe2\":\"-9007199254740992\"}"
                , jsFmt);
    }

    @Data
    public static class DateFmt {
        private LocalDate ldt = LocalDate.of(2022, 2, 2);
        private String str = "string";
    }

    public static class DateMmm extends DateFmt {
        @JsonFormat(pattern = "MMM dd, yyyy")
        private LocalDate ldt;
    }

    public static class DateWrp {
        @JsonSerialize(as = DateMmm.class)
        private DateFmt df;
    }

    @Test
    public void testDateFmt() throws JsonProcessingException {
        final DateFmt df = new DateFmt();
        Assertions.assertEquals("{\"ldt\":\"2022-02-02\",\"str\":\"string\"}", objectMapper.writeValueAsString(df));
        final DateWrp dw = new DateWrp();
        dw.df = df;
        Assertions.assertEquals("{\"df\":{\"ldt\":\"Feb 02, 2022\",\"str\":\"string\"}}", objectMapper.writeValueAsString(dw));
    }

    @Test
    public void testViewDec() {
        final String dec = restTemplate.getForObject(domain + "/test/json-dec.json", String.class);
        final String sub = restTemplate.getForObject(domain + "/test/json-sub.json", String.class);
        final String api = restTemplate.getForObject(domain + "/test/json-api.json", String.class);
        log.info("dec=" + dec);
        log.info("sub=" + sub);
        log.info("api=" + api);
        Assertions.assertEquals("{\"success\":true,\"data\":{\"dec\":\"12345.67\",\"str\":\"string\"}}", dec);
        Assertions.assertEquals("{\"success\":true,\"data\":{\"dec\":\"12,345.67\",\"str\":\"string\"}}", sub);
        Assertions.assertEquals("{\"success\":true,\"data\":{\"key\":\"12,345.67\"}}", api);
    }
}
