package pro.fessional.wings.slardar.fastjson;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONPath;
import com.alibaba.fastjson2.JSONWriter.Feature;
import com.alibaba.fastjson2.annotation.JSONField;
import io.qameta.allure.TmsLink;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.testing.silencer.data.BoxingArray;
import pro.fessional.wings.testing.silencer.data.BoxingValue;
import pro.fessional.wings.testing.silencer.data.CollectionValue;
import pro.fessional.wings.testing.silencer.data.CommonValue;
import pro.fessional.wings.testing.silencer.data.PrimitiveArray;
import pro.fessional.wings.testing.silencer.data.PrimitiveValue;
import pro.fessional.wings.testing.silencer.data.TransientPojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

/**
 * <a href="https://alibaba.github.io/fastjson2/register_custom_reader_writer_cn">register_custom_reader_writer_cn</a>
 * <a href="https://github.com/alibaba/fastjson2/issues/2582">negative bigdecimal -9223372036854775808</a>
 *
 * @author trydofor
 * @since 2022-10-25
 */
@Slf4j
class FastJsonTest {

    @Data
    public static class Dto {
        private boolean boolVal = true;
        private int intVal = 10086;
        @JSONField(format = "#,###")
        private long longVal = Long.MAX_VALUE;
        private double doubleVal = 100.86;
        private float floatVal = 100.86F;
        private BigDecimal bigDecimal = new BigDecimal("100.86");
        private LocalDateTime localDateTime = LocalDateTime.of(2022, 10, 24, 12, 34, 56);
        private ZonedDateTime zonedDateTime = localDateTime.atZone(ThreadNow.sysZoneId());
        private OffsetDateTime offsetDateTime = zonedDateTime.toOffsetDateTime();
    }

    @Test
    @TmsLink("C13010")
    public void testDefault() {
        testDefault(new Dto());
        testDefault(new BoxingArray().defaults());
        testDefault(new BoxingValue().defaults());
        testDefault(new CollectionValue().defaults());
        testDefault(new CommonValue().defaults());
        testDefault(new PrimitiveArray().defaults());
        testDefault(new PrimitiveValue().defaults());
        testDefault(new TransientPojo().defaults());
    }

    private <T> void testDefault(T t1) {
        final String s0 = JSON.toJSONString(t1);
        Class<?> clz = t1.getClass();
        log.info("testDefault, class={}, json={}", clz.getSimpleName(), s0);
        final Object d1 = JSON.parseObject(s0, clz);
        Assertions.assertEquals(t1, d1);
    }

    @Test
    @TmsLink("C13011")
    public void testString() {
        Dto d0 = new Dto();
        final String s0 = JSON.toJSONString(d0);
        log.info("testAsString, s0={}", s0);
        Assertions.assertTrue(s0.contains("\"longVal\":9223372036854775807"));
        final Dto d1 = JSON.parseObject(s0, Dto.class);
        Assertions.assertEquals(d0, d1);
    }

    /**
     * <a href="https://github.com/alibaba/fastjson2/issues/1537">WriteNonStringValueAsString format Number</a>
     */
    @Test
    @TmsLink("C13013")
    public void testSingle() {
        // default
        Assertions.assertEquals("true", JSON.toJSONString(true));
        Assertions.assertEquals("123", JSON.toJSONString(123));
        Assertions.assertEquals("123", JSON.toJSONString(Integer.valueOf("123")));
        Assertions.assertEquals(String.valueOf(Long.MAX_VALUE), JSON.toJSONString(Long.MAX_VALUE));
        Assertions.assertEquals("3.14", JSON.toJSONString(3.14));
        Assertions.assertEquals("3.14", JSON.toJSONString(Double.valueOf("3.14")));
        Assertions.assertEquals("3", JSON.toJSONString(new BigDecimal("3")));
        Assertions.assertEquals("3.14", JSON.toJSONString(new BigDecimal("3.14")));

        // as string
        Assertions.assertEquals("\"true\"", JSON.toJSONString(true, Feature.WriteNonStringValueAsString));
        Assertions.assertEquals("\"123\"", JSON.toJSONString(123, Feature.WriteNonStringValueAsString));
        Assertions.assertEquals("\"123\"", JSON.toJSONString(Integer.valueOf("123"), Feature.WriteNonStringValueAsString));
        Assertions.assertEquals("\"3.14\"", JSON.toJSONString(3.14, Feature.WriteNonStringValueAsString));
        Assertions.assertEquals("\"3.14\"", JSON.toJSONString(Double.valueOf("3.14"), Feature.WriteNonStringValueAsString));
        // BUG Fixed 2.0.34
        Assertions.assertEquals("\"3\"", JSON.toJSONString(new BigDecimal("3"), Feature.WriteNonStringValueAsString));
        // BUG Fixed 2.0.34
        Assertions.assertEquals("\"3.14\"", JSON.toJSONString(new BigDecimal("3.14"), Feature.WriteNonStringValueAsString));
    }

    @Test
    @TmsLink("C13126")
    public void testJsonPath() {
        CollectionValue data = new CollectionValue().defaults();
        R<CollectionValue> r = R.ok(data,"You're fired");
        String json = FastJsonHelper.string(r);
        JSONObject obj = FastJsonHelper.object(json);

        String json2 = FastJsonHelper.string(r, (com.alibaba.fastjson2.filter.Filter[]) null);
        Assertions.assertEquals(json, json2);

        JSONPath p1 = FastJsonHelper.path("$.success");
        JSONPath p2 = FastJsonHelper.path("$.success");
        Assertions.assertSame(p1, p2);
        Assertions.assertEquals(true, p1.eval(obj));

        JSONPath pn = FastJsonHelper.path("$.notfound");
        Assertions.assertNull(pn.eval(obj));

        JSONPath pd = FastJsonHelper.path("$.data.emptyList");
        Assertions.assertEquals(Collections.emptyList(), pd.eval(obj));

        JSONPath pl1 = FastJsonHelper.path("$.data.longList", List.class, Long.class);
        Assertions.assertEquals(CollectionValue.LongList, pl1.extract(json));

        JSONPath pl2 = FastJsonHelper.path("$.data.longList", List.class, Long.class);
        Assertions.assertEquals(CollectionValue.LongList, pl2.extract(json));
    }
}
