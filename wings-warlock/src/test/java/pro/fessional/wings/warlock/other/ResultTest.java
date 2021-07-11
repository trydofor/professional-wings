package pro.fessional.wings.warlock.other;

import com.alibaba.fastjson.JSON;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.Output;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.page.PageResult;
import pro.fessional.wings.slardar.serialize.KryoSimple;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author trydofor
 * @since 2021-07-08
 */
public class ResultTest {

    @SneakyThrows
    @Test
    public void testJackson() {
        final R<Object> r1 = R
                .ok()
                .setMessage("message")
                .setData("data")
                .setCode("code")
                .setCause("cause")
                .setI18nMessage("i18nCode", "1");
        ObjectMapper om = new ObjectMapper();
        final String json = om.writeValueAsString(r1);
        System.out.println(json);
        final R<Object> r2 = om.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<R<Object>>() {});
        Assertions.assertEquals(r1, r2);
        Assertions.assertNull(r2.getCause());
        Assertions.assertNull(r2.getI18nArgs());
        Assertions.assertNull(r2.getI18nCode());
    }

    @SneakyThrows
    @Test
    public void testFastjson() {
        final R<Object> r1 = R
                .ok()
                .setMessage("message")
                .setData("data")
                .setCode("code")
                .setCause("cause")
                .setI18nMessage("i18nCode", "1");
        final String json = JSON.toJSONString(r1);
        System.out.println(json);
        final R<Object> r2 = JSON.parseObject(json, new com.alibaba.fastjson.TypeReference<R<Object>>() {});
        Assertions.assertEquals(r1, r2);
        Assertions.assertNull(r2.getCause());
        Assertions.assertNull(r2.getI18nArgs());
        Assertions.assertNull(r2.getI18nCode());

        PageResult<String> p1 = new PageResult<String>()
                .addData("1")
                .setTotalInfo(30, 20);
        System.out.println(JSON.toJSONString(p1));
        System.out.println(JSON.toJSONString(p1.addMeta("left", 10)));
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    public void testKryo() {
        final R<Object> r1 = R
                .ok()
                .setMessage("message")
                .setData("data")
                .setCode("code")
                .setCause("cause")
                .setI18nMessage("i18nCode", "1");
        final Kryo kryo = KryoSimple.getKryo();
        final Output output = KryoSimple.getOutput();
        kryo.writeClassAndObject(output, r1);
        final byte[] bytes = output.toBytes();
        final R<Object> r2 = (R<Object>) kryo.readClassAndObject(new ByteBufferInput(bytes));
        Assertions.assertEquals(r1, r2);
        Assertions.assertEquals(r1.getCause(), r2.getCause());
        Assertions.assertArrayEquals(r1.getI18nArgs(), r2.getI18nArgs());
        Assertions.assertEquals(r1.getI18nCode(), r2.getI18nCode());
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    public void testSerial() {
        final R<Object> r1 = R
                .ok()
                .setMessage("message")
                .setData("data")
                .setCode("code")
                .setCause("cause")
                .setI18nMessage("i18nCode", "1");

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(r1);
        out.close();

        final byte[] bytes = bos.toByteArray();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        final R<Object> r2 = (R<Object>) ois.readObject();

        Assertions.assertEquals(r1, r2);
        Assertions.assertEquals(r1.getCause(), r2.getCause());
        Assertions.assertArrayEquals(r1.getI18nArgs(), r2.getI18nArgs());
        Assertions.assertEquals(r1.getI18nCode(), r2.getI18nCode());
    }
}
