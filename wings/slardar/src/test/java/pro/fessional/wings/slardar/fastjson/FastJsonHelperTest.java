package pro.fessional.wings.slardar.fastjson;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter.Feature;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.fessional.mirana.time.ThreadNow;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

import static pro.fessional.wings.slardar.fastjson.FastJsonHelper.DefaultWriter;

/**
 * https://alibaba.github.io/fastjson2/register_custom_reader_writer_cn
 *
 * @author trydofor
 * @since 2022-10-25
 */
@Slf4j
class FastJsonHelperTest {

    static {
        FastJsonHelper.initGlobal(true);
    }

    @Data
    public static class Dto {
        private int intVal = 10086;
        @JSONField(format = "#,###")
        private long longVal = 10086;
        private double doubleVal = 100.86;
        private float floatVal = 100.86F;
        private BigDecimal bigDecimal = new BigDecimal("100.86");
        private LocalDateTime localDateTime = LocalDateTime.of(2022, 10, 24, 12, 34, 56);
        private ZonedDateTime zonedDateTime = localDateTime.atZone(ThreadNow.sysZoneId());
        private OffsetDateTime offsetDateTime = zonedDateTime.toOffsetDateTime();
    }

    @Test
    public void testDefault() {
        Dto d0 = new Dto();
        final String s0 = JSON.toJSONString(d0, DefaultWriter());
        log.info("testDefault, s0={}", s0);
        final Dto d1 = JSON.parseObject(s0, Dto.class, FastJsonHelper.DefaultReader());
        Assertions.assertEquals(d0, d1);
    }

    @Test
    public void testString() {
        Dto d0 = new Dto();
        final String s0 = FastJsonHelper.string(d0);
        log.info("testAsString, s0={}", s0);
        Assertions.assertTrue(s0.contains("\"longVal\":\"10086\""));
        final Dto d1 = FastJsonHelper.object(s0, Dto.class);
        Assertions.assertEquals(d0, d1);
    }

    @Test
    public void testFormatString() {
        Dto d0 = new Dto();
        final String s0 = JSON.toJSONString(d0, FastJsonFilters.NumberFormatString, DefaultWriter());
        log.info("testAsString, s0={}", s0);
        Assertions.assertTrue(s0.contains("\"longVal\":\"10,086\""));
        final Dto d1 = FastJsonHelper.object(s0.replace("\"longVal\":\"10,086\"", "\"longVal\":\"10086\""), Dto.class);
        Assertions.assertEquals(d0, d1);
    }

    /**
     * <a href="https://github.com/alibaba/fastjson2/issues/1537">WriteNonStringValueAsString format Number</a>
     */
    @Test
    public void testSingle() {
        // default
        Assertions.assertEquals("true", JSON.toJSONString(true));
        Assertions.assertEquals("123", JSON.toJSONString(123));
        Assertions.assertEquals("123", JSON.toJSONString(Integer.valueOf("123")));
        Assertions.assertEquals("3.14", JSON.toJSONString(3.14));
        Assertions.assertEquals("3.14", JSON.toJSONString(Double.valueOf("3.14")));
        Assertions.assertEquals("3", JSON.toJSONString(new BigDecimal("3")));
        Assertions.assertEquals("3.14", JSON.toJSONString(new BigDecimal("3.14")));

        // as string
        Assertions.assertEquals("true", JSON.toJSONString(true, Feature.WriteNonStringValueAsString));
        Assertions.assertEquals("\"123\"", JSON.toJSONString(123, Feature.WriteNonStringValueAsString));
        Assertions.assertEquals("\"123\"", JSON.toJSONString(Integer.valueOf("123"), Feature.WriteNonStringValueAsString));
        Assertions.assertEquals("\"3.14\"", JSON.toJSONString(3.14, Feature.WriteNonStringValueAsString));
        Assertions.assertEquals("\"3.14\"", JSON.toJSONString(Double.valueOf("3.14"), Feature.WriteNonStringValueAsString));
        // BUG Fixed 2.0.34
        Assertions.assertEquals("\"3\"", JSON.toJSONString(new BigDecimal("3"), Feature.WriteNonStringValueAsString));
        // BUG Fixed 2.0.34
        Assertions.assertEquals("\"3.14\"", JSON.toJSONString(new BigDecimal("3.14"), Feature.WriteNonStringValueAsString));
    }

}
