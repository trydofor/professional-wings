package pro.fessional.wings.warlock.other;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.TmsLink;
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
    @TmsLink("C14035")
    public void param1Mvc() throws JsonProcessingException {
        testMvc("/test/param1/str.json", objectMapper.writeValueAsString("string"), "\"string\"");
        testMvc("/test/param1/str.json", "\"string\"", "\"string\"");
        testMvc("/test/param1/str.json", "string", "string");

        String intVal = String.valueOf(Integer.MAX_VALUE);
        String intStr = "\"" + intVal + "\"";
        testMvc("/test/param1/int.json", objectMapper.writeValueAsString(Integer.MAX_VALUE), intVal, intStr);
        testMvc("/test/param1/int.json", objectMapper.writeValueAsString(Integer.valueOf(intVal)), intVal, intStr);
        testMvc("/test/param1/int.json", intStr, intVal, intStr);
        testMvc("/test/param1/int.json", intVal, intVal, intStr);

        String int64Val = String.valueOf(Long.MAX_VALUE);
        String int64Str = "\"" + int64Val + "\"";
        testMvc("/test/param1/int64.json", objectMapper.writeValueAsString(Long.MAX_VALUE), int64Val, int64Str);
        testMvc("/test/param1/int64.json", objectMapper.writeValueAsString(Long.valueOf(int64Val)), int64Val, int64Str);
        testMvc("/test/param1/int64.json", int64Str, int64Val, int64Str);
        testMvc("/test/param1/int64.json", int64Val, int64Val, int64Str);

        String boolVal = "true";
        String boolStr = "\"" + boolVal + "\"";
        testMvc("/test/param1/bol.json", objectMapper.writeValueAsString(true), boolVal);
        testMvc("/test/param1/bol.json", objectMapper.writeValueAsString(Boolean.TRUE), boolVal);
        testMvc("/test/param1/bol.json", boolStr, boolVal);
        testMvc("/test/param1/bol.json", boolVal, boolVal);
        testMvc("/test/param1/ldt.json", objectMapper.writeValueAsString(ldt), "\"2021-06-06 06:06:06\"");
        testMvc("/test/param1/ldt.json", "\"2021-06-06 06:06:06\"", "\"2021-06-06 06:06:06\"");
        testMvc("/test/param1/enu.json", objectMapper.writeValueAsString(LogLevel.TRACE), "\"TRACE\"");
        testMvc("/test/param1/enu.json", "\"TRACE\"", "\"TRACE\"");

        String tenVal = "10";
        String tenStr = "\"" + tenVal + "\"";
        testMvc("/test/param1/dec.json", objectMapper.writeValueAsString(BigDecimal.TEN), tenVal, tenStr);
        testMvc("/test/param1/dec.json", tenStr, tenVal, tenStr);
        testMvc("/test/param1/dec.json", tenVal, tenVal, tenStr);
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
    @TmsLink("C14036")
    public void param1Obj() throws JsonProcessingException {
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
        String res = null;
        for (String s : acc) {
            if (s.equals(ft) || s.equals(jk)) {
                res = s;
                break;
            }
        }
        log.info("fastjson=[{}], jackson=[{}]", ft, jk);
        Assertions.assertNotNull(res, String.join(",", acc));
    }

    private void testMvc(String uri, String body, String... acc) {
        final String str1 = OkHttpClientHelper.postJson(okHttpClient, host + uri, body);
        String res = null;
        for (String s : acc) {
            if (s.equals(str1)) {
                res = s;
                break;
            }
        }
        log.info("uri={}, body={}, res={}", uri, body, str1);
        Assertions.assertNotNull(res, str1 + " in " + String.join(",", acc));
    }
}
