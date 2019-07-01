package pro.fessional.wings.example.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import kotlin.collections.MapsKt;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pro.fessional.wings.silencer.spring.bean.WingsJacksonConfiguration;

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
    public void test() throws IOException {
        JsonIt it = new JsonIt();
        System.out.println("===========");
        System.out.println(it);
        String json = objectMapper.writeValueAsString(it);
        System.out.println("===========");
        System.out.println(json);
        JsonIt obj = objectMapper.readValue(json, JsonIt.class);
        System.out.println("===========");
        System.out.println(obj);
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
        private ZonedDateTime zonedDateTimeVal = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("America/New_York"));
        private Instant instantVal = Instant.now();
        private Date utilDateVal = new Date();
        private Calendar calendarVal = Calendar.getInstance();
        private List<String> listVal = Arrays.asList("字符串", "列表");
        private Map<String, Long> mapVal = new HashMap<String, Long>() {{put("Map", 1L);}};
    }
}
