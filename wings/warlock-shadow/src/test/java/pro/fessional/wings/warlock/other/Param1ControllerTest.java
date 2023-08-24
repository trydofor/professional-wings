package pro.fessional.wings.warlock.other;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.slardar.fastjson.FastJsonHelper;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpClientHelper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author trydofor
 * @since 2023-06-06
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class Param1ControllerTest {

    @Setter(onMethod_ = {@Value("http://localhost:${local.server.port}")})
    private String host;

    @Setter(onMethod_ = {@Autowired})
    private OkHttpClient okHttpClient;

    @Setter(onMethod_ = {@Autowired})
    protected ObjectMapper objectMapper;

    final LocalDateTime ldt = LocalDateTime.of(2021, 6, 6, 6, 6, 6);

    @Test
    public void testMvc() throws JsonProcessingException {
        testMvc("/test/param1/str.json", objectMapper.writeValueAsString("string"), "\"string\"");
        testMvc("/test/param1/str.json", "\"string\"", "\"string\"");
        testMvc("/test/param1/str.json", "string", "string");
        testMvc("/test/param1/int.json", objectMapper.writeValueAsString(123), "123", "\"123\"");
        testMvc("/test/param1/int.json", objectMapper.writeValueAsString(Integer.valueOf("123")), "123", "\"123\"");
        testMvc("/test/param1/int.json", "\"123\"", "123", "\"123\"");
        testMvc("/test/param1/int.json", "123", "123", "\"123\"");
        testMvc("/test/param1/bol.json", objectMapper.writeValueAsString(true), "true");
        testMvc("/test/param1/bol.json", objectMapper.writeValueAsString(Boolean.TRUE), "true");
        testMvc("/test/param1/bol.json", "\"true\"", "true");
        testMvc("/test/param1/bol.json", "true", "true");
        testMvc("/test/param1/ldt.json", objectMapper.writeValueAsString(ldt), "\"2021-06-06 06:06:06\"");
        testMvc("/test/param1/ldt.json", "\"2021-06-06 06:06:06\"", "\"2021-06-06 06:06:06\"");
        testMvc("/test/param1/enu.json", objectMapper.writeValueAsString(LogLevel.TRACE), "\"TRACE\"");
        testMvc("/test/param1/enu.json", "\"TRACE\"", "\"TRACE\"");
        testMvc("/test/param1/dec.json", objectMapper.writeValueAsString(BigDecimal.TEN), "10", "\"10\"");
        testMvc("/test/param1/dec.json", "\"10\"", "10", "\"10\"");
        testMvc("/test/param1/dec.json", "10", "10", "\"10\"");
    }

    /**
     * fastjson=["string"], jackson=["string"]
     * fastjson=["123"], jackson=[123]
     * fastjson=["123"], jackson=[123]
     * fastjson=[true], jackson=[true]
     * fastjson=[true], jackson=[true]
     * fastjson=["TRACE"], jackson=["TRACE"]
     * fastjson=[10], jackson=["10"]
     * fastjson=["2021-06-06 06:06:06"], jackson=["2021-06-06 06:06:06"]
     */
    @Test
    public void testObj() throws JsonProcessingException {
        testObj("string", "\"string\"");
        testObj(123, "123", "\"123\"");
        testObj(Integer.valueOf("123"), "123", "\"123\"");
        testObj(true, "true");
        testObj(Boolean.TRUE, "true");
        testObj(LogLevel.TRACE, "\"TRACE\"");
        testObj(BigDecimal.TEN, "10", "\"10\"");
        testObj(ldt, "\"2021-06-06 06:06:06\"");
    }

    private void testObj(Object obj, String... acc) throws JsonProcessingException {
        final String ft = FastJsonHelper.string(obj);
        final String jk = objectMapper.writeValueAsString(obj);
        boolean ok = false;
        for (String s : acc) {
            if (s.equals(ft) || s.equals(jk)) {
                ok = true;
                break;
            }
        }
        System.out.printf("fastjson=[%s], jackson=[%s]\n", ft, jk);
        Assertions.assertTrue(ok, String.join(",", acc));
    }

    private void testMvc(String uri, String body, String... acc) {
        final String str1 = OkHttpClientHelper.postJson(okHttpClient, host + uri, body);
        boolean ok = false;
        for (String s : acc) {
            if (s.equals(str1)) {
                ok = true;
                break;
            }
        }
        Assertions.assertTrue(ok, str1 + " in " + String.join(",", acc));
    }
}
