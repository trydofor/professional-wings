package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
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

import java.time.ZoneId;
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
        ObjectMapper jsonMapper = new ObjectMapper()
            .findAndRegisterModules()
            .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        final String jsonText = InputStreams.readText(JacksonTest.class.getResourceAsStream("/complex.json"));
        final Map<?, ?> jsonMap = jsonMapper.readValue(jsonText, Map.class);

        ObjectMapper xmlMapper = new XmlMapper()
            .findAndRegisterModules()
            .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        final String mxlText = InputStreams.readText(JacksonTest.class.getResourceAsStream("/complex.xml"));
        final Map<?, ?> xmlMap = xmlMapper.readValue(mxlText, Map.class);

        Assertions.assertEquals(jsonMap, xmlMap);
        testDefault(jsonMapper, new BoxingArray().defaults());
        testDefault(jsonMapper, new BoxingValue().defaults());
        testDefault(jsonMapper, new CollectionValue().defaults());
        testDefault(jsonMapper, new PrimitiveArray().defaults());
        testDefault(jsonMapper, new PrimitiveValue().defaults());
        testDefault(jsonMapper, new TransientPojo().defaults());
        testDefault(jsonMapper, new CommonValue().defaults());
    }

    @SneakyThrows
    private <T> void testDefault(ObjectMapper jsonMapper, T t1) {
        String s0 = jsonMapper.writeValueAsString(t1);
        Class<?> clz = t1.getClass();
        log.info("testDefault, class={}, json={}", clz.getSimpleName(), s0);
        if (t1 instanceof TransientPojo vo) {
            vo.setTranGetter(null);
            vo.setTranSetter(null);
            vo.setTranBoth(null);
        }
        else if (t1 instanceof CommonValue vo) {
            vo.setZoneDateTimeValueUs(vo
                .getZoneDateTimeValueUs()
                .withZoneSameInstant(ZoneId.of("-04:00"))
            );
            vo.setZoneDateTimeValueJp(vo
                .getZoneDateTimeValueJp()
                .withZoneSameInstant(ZoneId.of("+09:00"))
            );
        }
        final Object d1 = jsonMapper.readValue(s0, clz);
        Assertions.assertEquals(t1, d1);
    }
}
