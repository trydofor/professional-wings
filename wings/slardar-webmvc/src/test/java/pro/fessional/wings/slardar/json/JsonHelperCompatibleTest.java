package pro.fessional.wings.slardar.json;

import com.alibaba.fastjson2.JSON;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.slardar.fastjson.FastJsonHelper;
import pro.fessional.wings.slardar.jackson.JacksonHelper;
import pro.fessional.wings.slardar.jackson.JacksonHelper.Style;
import pro.fessional.wings.testing.silencer.data.BoxingArray;
import pro.fessional.wings.testing.silencer.data.BoxingValue;
import pro.fessional.wings.testing.silencer.data.CollectionValue;
import pro.fessional.wings.testing.silencer.data.CommonValue;
import pro.fessional.wings.testing.silencer.data.PrimitiveArray;
import pro.fessional.wings.testing.silencer.data.PrimitiveValue;
import pro.fessional.wings.testing.silencer.data.TransientPojo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Ø
 *
 * @author trydofor
 * @since 2024-06-04
 */
@SpringBootTest(
    properties = {
        "wings.slardar.number.integer.format=",
        "wings.slardar.number.floats.format=",
        "wings.slardar.number.decimal.format=",
    }
)
@Slf4j
public class JsonHelperCompatibleTest {

    private boolean asserts = true;

    /**
     * <pre>
     * almost hardcode, can not impl or config format
     * * boolean - with/out quote
     * * number - with/out quote
     * * zoned datetime - 2023-04-05T06:07:08[America/New_York]
     * * offset datetime - 2023-04-05T06:07:08-04:00
     * * byte[] - [-128, 127]
     * * char[] - [char, char]
     * </pre>
     */
    @Test
    @TmsLink("C13124")
    public void testFastjson() {
        fastjson(true, "true");
        fastjson(false, "false");
        fastjson(Byte.MIN_VALUE, "-128");
        fastjson(Byte.MAX_VALUE, "127");
        fastjson(Character.MIN_VALUE, "\"\\u0000\"");
        fastjson(Character.MAX_VALUE, "￿"); // not empty, but char
        fastjson(Short.MIN_VALUE, "-32768");
        fastjson(Short.MAX_VALUE, "32767");
        fastjson(Integer.MIN_VALUE, "-2147483648");
        fastjson(Integer.MAX_VALUE, "2147483647");
        fastjson(Long.MIN_VALUE, "\"-9223372036854775808\"");
        fastjson(Long.MAX_VALUE, "\"9223372036854775807\"");
        fastjson(PrimitiveValue.FloatPip, "3.1415927");
        fastjson(PrimitiveValue.FloatPin, "-3.1415927");
        fastjson(PrimitiveValue.DoublePip, "3.141592653589793");
        fastjson(PrimitiveValue.DoublePin, "-3.141592653589793");
        fastjson(new BigDecimal(CommonValue.ZeroDot2), CommonValue.ZeroDot2);
        fastjson(new BigDecimal(CommonValue.PosPaiD15), CommonValue.PosPaiD15);
        fastjson(new BigDecimal(CommonValue.NegPaiD15), CommonValue.NegPaiD15);
        fastjson(new BigInteger(CommonValue.Zero), CommonValue.Zero);
        fastjson(new BigInteger(CommonValue.PosPaiN15), CommonValue.PosPaiN15);
        fastjson(new BigInteger(CommonValue.NegPaiN15), CommonValue.NegPaiN15);
        fastjson(CommonValue.LdValue, "\"2023-04-05\"");
        fastjson(CommonValue.LtValue, "\"06:07:08\"");
        fastjson(CommonValue.LdtValue, "\"2023-04-05 06:07:08\"");
        fastjson(CommonValue.ZdtValueUs, "\"2023-04-05T06:07:08[America/New_York]\"");
        fastjson(CommonValue.ZdtValueJp, "\"2023-04-05T06:07:08[Asia/Tokyo]\"");
        fastjson(CommonValue.OdtValueUs, "\"2023-04-05T06:07:08-04:00\"");
        fastjson(CommonValue.OdtValueJp, "\"2023-04-05T06:07:08+09:00\"");

        fastjson(BoxingArray.BoolArrEmpty);
        fastjson(BoxingArray.BoolArrValue);
        fastjson(BoxingArray.ByteArrEmpty);
        fastjson(BoxingArray.ByteArrValue);
        fastjson(BoxingArray.CharArrEmpty);
        fastjson(BoxingArray.CharArrValue);
        fastjson(BoxingArray.ShortArrEmpty);
        fastjson(BoxingArray.ShortArrValue);
        fastjson(BoxingArray.IntArrEmpty);
        fastjson(BoxingArray.IntArrValue);
        fastjson(BoxingArray.LongArrEmpty);
        fastjson(BoxingArray.LongArrValue);
        fastjson(BoxingArray.FloatArrEmpty);
        fastjson(BoxingArray.FloatArrValue);
        fastjson(BoxingArray.DoubleArrEmpty);
        fastjson(BoxingArray.DoubleArrValue);

        fastjson(CollectionValue.EmptyList);
        fastjson(CollectionValue.EmptySet);
        fastjson(CollectionValue.EmptyMap);
        fastjson(CollectionValue.BoolList);
        fastjson(CollectionValue.BoolSet);
        fastjson(CollectionValue.BoolMap);
        fastjson(CollectionValue.LongList);
        fastjson(CollectionValue.LongSet);
        fastjson(CollectionValue.LongMap);
        fastjson(CollectionValue.DoubleList);
        fastjson(CollectionValue.DoubleSet);
        fastjson(CollectionValue.DoubleMap);

        //
        fastjson(new CommonValue().defaults(), "\"offsetDateTimeValueUs\":\"2023-04-05T06:07:08-04:00\"", "\"zoneDateTimeValueUs\":\"2023-04-05T06:07:08[America/New_York]\"");
        fastjson(new CollectionValue().defaults(), "\"boolList\":[true,false]", "\"boolMap\":{\"k0\":true,\"k1\":false}"); // MAP NOT WORKS
        fastjson(new BoxingValue().defaults(), "\"boolTrue\":true", "\"intMin\":-2147483648");
        fastjson(new PrimitiveValue().defaults(), "\"boolTrue\":true", "\"intMin\":-2147483648");
        fastjson(new BoxingArray().defaults(), "\"boolArrValue\":[true,false]", "\"intArrValue\":[-2147483648,2147483647]");
        fastjson(new PrimitiveArray().defaults(), "\"boolArrValue\":[true,false]", "\"intArrValue\":[-2147483648,2147483647]");
        // fastjson handle @Transient method
        TransientPojo transient2 = new TransientPojo().defaults();
        transient2.setTranValue(null);
        fastjson(new TransientPojo().defaults(), transient2, "\"tranBoth\":true", "\"tranGetter\":true", "\"tranSetter\":true");
    }

    private <T> void fastjson(T t1, String... ins) {
        fastjson(t1, t1, ins);
    }

    private <T> void fastjson(T t1, T t2, String... ins) {
        log.info("fastjson, value={}", t1);
        Class<?> clz = t1.getClass();

        final String jsonByFastPlain = JSON.toJSONString(t1);
        log.info("jsonByFastPlain={}", jsonByFastPlain);
        final String jsonByFastWings = FastJsonHelper.string(t1);

        if (ins == null || ins.length == 0) {
            ins = new String[]{};
            log.info("jsonByFastWings={}, class={}", jsonByFastWings, clz.getSimpleName());
        }
        else {
            log.info("jsonByFastWings={}, class={}, ins-len={}", jsonByFastWings, clz.getSimpleName(), ins.length);
        }

        final Object objectByFastWings1 = FastJsonHelper.object(jsonByFastWings, clz);
        final Object objectByFastWings2 = FastJsonHelper.object(jsonByFastPlain, clz);
        final Object objectByFastPlain = JSON.parseObject(jsonByFastWings, clz);
        // read by jackson
        Object objectByJackWings = JacksonHelper.object(jsonByFastWings, clz);
        Object objectByJackPlain = JacksonHelper.object(Style.Plain, fixFastWingsJackPlain(clz, jsonByFastWings), clz);

        if (!asserts) return;

        for (String s : ins) {
            Assertions.assertTrue(jsonByFastWings.equals(s) || jsonByFastWings.contains(s), s + " not found");
        }

        if (clz.isArray()) {
            Assertions.assertArrayEquals((Object[]) t2, (Object[]) objectByFastWings1);
            Assertions.assertArrayEquals((Object[]) t2, (Object[]) objectByFastWings2);
            Assertions.assertArrayEquals((Object[]) t2, (Object[]) objectByFastPlain);
            Assertions.assertArrayEquals((Object[]) t2, (Object[]) fixFastWingsJack(objectByJackWings));
            Assertions.assertArrayEquals((Object[]) t2, (Object[]) fixFastWingsJack(objectByJackPlain));
        }
        else {

            Assertions.assertEquals(t2, fixCollectionValueType(t2, objectByFastWings1));
            Assertions.assertEquals(t2, fixCollectionValueType(t2, objectByFastWings2));
            Assertions.assertEquals(t2, fixCollectionValueType(t2, objectByFastPlain));
            Assertions.assertEquals(t2, fixCollectionValueType(t2, fixFastWingsJack(objectByJackWings)));
            Assertions.assertEquals(t2, fixCollectionValueType(t2, fixFastWingsJack(objectByJackPlain)));
        }
    }

    @SuppressWarnings("all")
    private Object fixCollectionValueType(Object t2, Object obj) {
        if (t2 instanceof List ts && !ts.isEmpty() && obj instanceof List cs) {
            Object v0 = ts.get(0);
            Function<String, Object> fun = null;
            if (v0 instanceof Long) {
                fun = Long::valueOf;
            }
            else if (v0 instanceof Double) {
                fun = Double::valueOf;
            }

            if (fun != null) {
                ArrayList<Object> tmp = new ArrayList<>(cs);
                cs.clear();
                for (Object o : tmp) {
                    cs.add(fun.apply(o.toString()));
                }
            }
        }
        else if (t2 instanceof Set ts && !ts.isEmpty() && obj instanceof Set cs) {
            Object v0 = ts.iterator().next();
            Function<String, Object> fun = null;
            if (v0 instanceof Long) {
                fun = Long::valueOf;
            }
            else if (v0 instanceof Double) {
                fun = Double::valueOf;
            }

            if (fun != null) {
                ArrayList<Object> tmp = new ArrayList<>(cs);
                cs.clear();
                for (Object o : tmp) {
                    cs.add(fun.apply(o.toString()));
                }
            }
        }
        else if (t2 instanceof Map ts && !ts.isEmpty() && obj instanceof Map cs) {
            Object v0 = ts.values().iterator().next();
            Function<String, Object> fun = null;
            if (v0 instanceof Long) {
                fun = Long::valueOf;
            }
            else if (v0 instanceof Double) {
                fun = Double::valueOf;
            }

            if (fun != null) {
                HashMap<Object, Object> tmp = new HashMap<>(cs);
                cs.clear();
                for (Map.Entry<Object, Object> en : tmp.entrySet()) {
                    cs.put(en.getKey(), fun.apply(en.getValue().toString()));
                }
            }
        }
        return obj;
    }

    private String fixFastWingsJackPlain(Class<?> clz, String json) {
        if (clz == CommonValue.class || clz == LocalDateTime.class || clz == ZonedDateTime.class || clz == OffsetDateTime.class) {
            return json
                // fastjson by default, 2023-04-05 06:07:08 for localdatetime, other 2023-04-05T06:07:08
                .replace("2023-04-05 06:07:08", "2023-04-05T06:07:08")
                ;
        }
        return json;
    }

    private Object fixFastWingsJack(Object obj) {
        if (obj instanceof TransientPojo vo) {
            // fastjson output but jackson ignore @Transient method
            vo.setTranBoth(true);
            vo.setTranGetter(true);
            vo.setTranSetter(true);
            return vo;
        }
        return obj;
    }

    /**
     * <pre>
     * impl or config by wings
     * * boolean - without quote (jackson default)
     * * byte[] - base64 (jackson default)
     * * char[] - String (jackson default)
     * * number not safe - with quote
     * * number floating - with quote
     * * zoned datetime - 2023-04-05 06:07:08 America/New_York
     * * offset datetime - 2023-04-05 06:07:08 -04:00
     * </pre>
     */
    @Test
    @TmsLink("C13125")
    public void testJackson() {

        jackson(true, "true");
        jackson(false, "false");
        jackson(Byte.MIN_VALUE, "-128");
        jackson(Byte.MAX_VALUE, "127");
        jackson(Character.MIN_VALUE, "\"\\u0000\"");
        jackson(Character.MAX_VALUE, "￿"); // not empty, but char
        jackson(Short.MIN_VALUE, "-32768");
        jackson(Short.MAX_VALUE, "32767");
        jackson(Integer.MIN_VALUE, "-2147483648");
        jackson(Integer.MAX_VALUE, "2147483647");
        jackson(Long.MIN_VALUE, "\"-9223372036854775808\"");
        jackson(Long.MAX_VALUE, "\"9223372036854775807\"");
        jackson(PrimitiveValue.FloatPip, "3.1415927");
        jackson(PrimitiveValue.FloatPin, "-3.1415927");
        jackson(PrimitiveValue.DoublePip, "3.141592653589793");
        jackson(PrimitiveValue.DoublePin, "-3.141592653589793");
        jackson(new BigDecimal(CommonValue.ZeroDot2), CommonValue.ZeroDot2);
        jackson(new BigDecimal(CommonValue.PosPaiD15), CommonValue.PosPaiD15);
        jackson(new BigDecimal(CommonValue.NegPaiD15), CommonValue.NegPaiD15);
        jackson(new BigInteger(CommonValue.Zero), CommonValue.Zero);
        jackson(new BigInteger(CommonValue.PosPaiN15), CommonValue.PosPaiN15);
        jackson(new BigInteger(CommonValue.NegPaiN15), CommonValue.NegPaiN15);
        jackson(CommonValue.LdValue, "\"2023-04-05\"");
        jackson(CommonValue.LtValue, "\"06:07:08\"");
        jackson(CommonValue.LdtValue, "\"2023-04-05 06:07:08\"");
        jackson(CommonValue.ZdtValueUs, "\"2023-04-05 06:07:08 America/New_York\"");
        jackson(CommonValue.ZdtValueJp, "\"2023-04-05 06:07:08 Asia/Tokyo\"");
        jackson(CommonValue.OdtValueUs, "\"2023-04-05 06:07:08 -04:00\"");
        jackson(CommonValue.OdtValueJp, "\"2023-04-05 06:07:08 +09:00\"");

        jackson(BoxingArray.BoolArrEmpty);
        jackson(BoxingArray.BoolArrValue);
        jackson(BoxingArray.ByteArrEmpty);
        jackson(BoxingArray.ByteArrValue);
        jackson(BoxingArray.CharArrEmpty);
        jackson(BoxingArray.CharArrValue);
        jackson(BoxingArray.ShortArrEmpty);
        jackson(BoxingArray.ShortArrValue);
        jackson(BoxingArray.IntArrEmpty);
        jackson(BoxingArray.IntArrValue);
        jackson(BoxingArray.LongArrEmpty);
        jackson(BoxingArray.LongArrValue);
        jackson(BoxingArray.FloatArrEmpty);
        jackson(BoxingArray.FloatArrValue);
        jackson(BoxingArray.DoubleArrEmpty);
        jackson(BoxingArray.DoubleArrValue);

        jackson(CollectionValue.EmptyList);
        jackson(CollectionValue.EmptySet);
        jackson(CollectionValue.EmptyMap);
        jackson(CollectionValue.BoolList);
        jackson(CollectionValue.BoolSet);
        jackson(CollectionValue.BoolMap);
        jackson(CollectionValue.LongList);
        jackson(CollectionValue.LongSet);
        jackson(CollectionValue.LongMap);
        jackson(CollectionValue.DoubleList);
        jackson(CollectionValue.DoubleSet);
        jackson(CollectionValue.DoubleMap);

        jackson(new CommonValue().defaults(), "\"offsetDateTimeValueUs\":\"2023-04-05 06:07:08 -04:00\"", "\"zoneDateTimeValueUs\":\"2023-04-05 06:07:08 America/New_York\"");
        jackson(new CollectionValue().defaults(), "\"boolList\":[true,false]", "\"boolMap\":{\"k0\":true,\"k1\":false}"); // MAP NOT WORKS
        jackson(new BoxingValue().defaults(), "\"boolTrue\":true", "\"intMin\":-2147483648");
        jackson(new PrimitiveValue().defaults(), "\"boolTrue\":true", "\"intMin\":-2147483648");
        jackson(new BoxingArray().defaults(), "\"boolArrValue\":[true,false]", "\"intArrValue\":[-2147483648,2147483647]");
        jackson(new PrimitiveArray().defaults(), "\"boolArrValue\":[true,false]", "\"intArrValue\":[-2147483648,2147483647]");
        // jackson ignore transient field and @Transient method
        jackson(new TransientPojo().defaults(), new TransientPojo(), "{}");
    }


    private <T> void jackson(T t1, String... ins) {
        jackson(t1, t1, ins);
    }

    private <T> void jackson(T t1, T t2, String... ins) {
        log.info("jackson, value={}", t1);

        final String jsonByJackPlain = JacksonHelper.string(Style.Plain, t1);
        log.info("jsonByJackPlain={}", jsonByJackPlain);
        final String jsonByJackWings = JacksonHelper.string(t1);

        Class<?> clz = t1.getClass();
        if (ins == null || ins.length == 0) {
            ins = new String[]{};
            log.info("jsonByJackWings={}, class={}", jsonByJackWings, clz.getSimpleName());
        }
        else {
            log.info("jsonByJackWings={}, class={}, ins-len={}", jsonByJackWings, clz.getSimpleName(), ins.length);
        }

        final Object objectByJackWings1 = JacksonHelper.object(jsonByJackWings, clz);
        final Object objectByJackWings2 = JacksonHelper.object(jsonByJackPlain, clz);
        final Object objectByJackPlain = JacksonHelper.object(Style.Plain, jsonByJackWings, clz);
        // read by fastjson
        final Object objectByFastWings = FastJsonHelper.object(fixJackWingsFastWings(clz, jsonByJackWings), clz);
        final Object objectByFastPlain = JSON.parseObject(fixJackWingsFastWings(clz, jsonByJackWings), clz);

        if (!asserts) return;

        for (String s : ins) {
            Assertions.assertTrue(jsonByJackWings.equals(s) || jsonByJackWings.contains(s), s + " not found");
        }

        if (clz.isArray()) {
            Assertions.assertArrayEquals((Object[]) t2, (Object[]) objectByJackWings1);
            Assertions.assertArrayEquals((Object[]) t2, (Object[]) objectByJackWings2);
            Assertions.assertArrayEquals((Object[]) t2, (Object[]) objectByJackPlain);
            Assertions.assertArrayEquals((Object[]) t2, (Object[]) objectByFastWings);
            Assertions.assertArrayEquals((Object[]) t2, (Object[]) objectByFastPlain);
        }
        else {
            Assertions.assertEquals(t2, fixCollectionValueType(t2, objectByJackWings1));
            Assertions.assertEquals(t2, fixCollectionValueType(t2, objectByJackWings2));
            Assertions.assertEquals(t2, fixCollectionValueType(t2, objectByJackPlain));
            Assertions.assertEquals(t2, fixCollectionValueType(t2, objectByFastWings));
            Assertions.assertEquals(t2, fixCollectionValueType(t2, objectByFastPlain));
        }
    }

    private String fixJackWingsFastWings(Class<?> clz, String json) {
        if (clz == PrimitiveArray.class) {
            return json
                // jackson handle byte[] with base64, fastjson not
                .replace("\"byteArrValue\":\"gH8=\"", "\"byteArrValue\":[-128, 127]")
                .replace("\"byteArrEmpty\":\"\"", "\"byteArrEmpty\":[]");
        }
        return json;
    }
}
