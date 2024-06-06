package pro.fessional.wings.slardar.jackson;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.io.InputStreams;
import pro.fessional.wings.testing.silencer.data.BoxingArray;
import pro.fessional.wings.testing.silencer.data.BoxingValue;
import pro.fessional.wings.testing.silencer.data.CollectionValue;
import pro.fessional.wings.testing.silencer.data.CommonValue;
import pro.fessional.wings.testing.silencer.data.PrimitiveArray;
import pro.fessional.wings.testing.silencer.data.PrimitiveValue;
import pro.fessional.wings.testing.silencer.data.TransientPojo;

import java.util.Map;

/**
 * @author trydofor
 * @since 2022-11-30
 */
@SpringBootTest
@Slf4j
class JacksonTest {

    @Test
    @TmsLink("C13014")
    void jacksonJsonAndXml() throws JsonProcessingException {
        ObjectMapper jsonMapper = new ObjectMapper();

        final String jsonText = InputStreams.readText(JacksonTest.class.getResourceAsStream("/complex.json"));
        final Map<?, ?> jsonMap = jsonMapper.readValue(jsonText, Map.class);

        XmlMapper xmlMapper = new XmlMapper();
        final String mxlText = InputStreams.readText(JacksonTest.class.getResourceAsStream("/complex.xml"));
        final Map<?, ?> xmlMap = xmlMapper.readValue(mxlText, Map.class);

        Assertions.assertEquals(jsonMap, xmlMap);
        testDefault(new BoxingArray().defaults());
        testDefault(new BoxingValue().defaults());
        testDefault(new CollectionValue().defaults());
        testDefault(new CommonValue().defaults());
        testDefault(new PrimitiveArray().defaults());
        testDefault(new PrimitiveValue().defaults());
        testDefault(new TransientPojo().defaults());
    }

    private <T> void testDefault(T t1){
        final String s0 = JSON.toJSONString(t1);
        Class<?> clz = t1.getClass();
        log.info("testDefault, class={}, json={}", clz.getSimpleName(), s0);
        final Object d1 = JSON.parseObject(s0, clz);
        Assertions.assertEquals(t1, d1);
    }
}
