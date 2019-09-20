package pro.fessional.wings.silencer.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pro.fessional.mirana.i18n.I18nString;
import pro.fessional.wings.silencer.datetime.DateTimePattern;
import pro.fessional.wings.silencer.jackson.JsonI18nString;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author trydofor
 * @since 2019-06-26
 */
@RunWith(SpringRunner.class)
//@SpringBootTest(properties = {"debug = true"})
@SpringBootTest
public class WingsJacksonMapperTest {

    private ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Test
    public void testEquals() throws IOException {
        System.out.println("=== ZoneId= " + ZoneId.systemDefault());
        JsonIt it = new JsonIt();
        System.out.println("===== to string ======");
        System.out.println(it);
        String json = objectMapper.writeValueAsString(it);
        System.out.println("===== write json ======");
        System.out.println(json);
        JsonIt obj = objectMapper.readValue(json, JsonIt.class);
        System.out.println("===== read json ======");
        System.out.println(obj);

        String json2 = objectMapper.writeValueAsString(obj);
        JsonIt obj2 = objectMapper.readValue(json2, JsonIt.class);

        Assert.assertEquals(json, json2);
        Assert.assertEquals(obj, obj2);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JsonIt {
        private boolean boolVal = false;
        private int intVal = Integer.MAX_VALUE - 1;
        private long longVal = Long.MAX_VALUE - 1;
        private float floatVal = 1.1F;
        private double doubleVal = 2.2D;
        private BigDecimal decimalVal = new BigDecimal("3.3");
        private Integer intNull = null;
        private Long longNull = null;
        private LocalDate localDateVal = LocalDate.now();
        private LocalTime localTimeVal = LocalTime.now();
        private LocalDateTime localDateTimeVal = LocalDateTime.now();
        private ZonedDateTime zonedDateTimeVal = ZonedDateTime.now();
        @JsonFormat(pattern = DateTimePattern.PTN_FULL_23V)
        private ZonedDateTime zonedDateTimeValV = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("America/New_York"));
        @JsonFormat(pattern = DateTimePattern.PTN_FULL_23Z)
        private ZonedDateTime zonedDateTimeValZ = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("America/New_York"));
        private Instant instantVal = Instant.now();
        private Date utilDateVal = new Date();
        private Calendar calendarVal = Calendar.getInstance();
        private List<String> listVal = Arrays.asList("字符串", "列表");
        private Map<String, Long> mapVal = new HashMap<String, Long>() {{put("Map", 1L);}};
    }

    @Test
    public void testI18nString() throws IOException {
        I18nJson obj = new I18nJson();
        String json = objectMapper.writeValueAsString(obj);
        System.out.println(json.replace(",", ",\n"));
    }

    @Data
    public static class I18nJson {
        @JsonI18nString // 有效
        private String codeManual = "base.not-empty";
        private String codeIgnore = "base.not-empty";
        // 自动
        private I18nString textAuto = new I18nString("base.not-empty", "", "textAuto");
        @JsonI18nString(false) //禁用
        private I18nString textDisabled = new I18nString("base.not-empty", "", "textDisabled");
        @JsonI18nString // 无效
        private Long longIgnore = 0L;
        @JsonI18nString // 无效
        private Map<String, String> mapIgnore = Collections.singletonMap("ikey", "ival");
        @JsonI18nString(false) //禁用
        private Map<String, I18nString> mapDisabled = Collections.singletonMap("i18n", textDisabled);
        // 自动
        private Map<String, I18nString> mapAuto = Collections.singletonMap("i18n", textAuto);
    }
}
