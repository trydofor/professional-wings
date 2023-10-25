package pro.fessional.wings.slardar.jackson;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.io.InputStreams;

import java.util.Map;

/**
 * @author trydofor
 * @since 2022-11-30
 */
@SpringBootTest
class JacksonHelperTest {

    @Test
    @TmsLink("C13014")
    void testObject() {
        final String jsonText = InputStreams.readText(JacksonHelperTest.class.getResourceAsStream("/complex.json"));
        final Map<?, ?> jsonMap = JacksonHelper.object(jsonText, Map.class);

        final String mxlText = InputStreams.readText(JacksonHelperTest.class.getResourceAsStream("/complex.xml"));
        final Map<?, ?> xmlMap = JacksonHelper.object(mxlText, Map.class);

        Assertions.assertEquals(jsonMap, xmlMap);
    }
}
