package pro.fessional.wings.silencer.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.i18n.I18nString;
import pro.fessional.wings.silencer.datetime.DateTimePattern;
import pro.fessional.wings.silencer.jackson.JsonI18nString;
import pro.fessional.wings.silencer.jackson.StringMapGenerator;
import pro.fessional.wings.silencer.jackson.StringMapHelper;

import javax.xml.bind.annotation.XmlRootElement;
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

    @Setter(onMethod = @__({@Autowired}))
    private ObjectMapper objectMapper;

    @Setter(onMethod = @__({@Autowired}))
    private XmlMapper xmlMapper;


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
    @XmlRootElement
    public static class JsonIt {
        @JsonProperty("bool-val")
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
        ObjectWriter jackson = objectMapper.writerWithDefaultPrettyPrinter();
        String json = jackson.writeValueAsString(obj);
        System.out.println(json);
    }

    @Data
    @XmlRootElement
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


    @Test
    public void testI18nResult() throws IOException {
        ObjectWriter jackson = objectMapper.writerWithDefaultPrettyPrinter();

        R<I18nJson> r1 = R.ok("这是一个消息", new I18nJson());
        String j1 = jackson.writeValueAsString(r1);
        System.out.println(j1);

        R<I18nJson> r2 = r1.toI18n("base.not-empty", "第一个参数");
        String j2 = jackson.writeValueAsString(r2);
        System.out.println(j2);
    }

    @Test
    public void testXml() throws IOException {
        ObjectWriter jackson = xmlMapper.writerWithDefaultPrettyPrinter();
        I18nJson i18nJson = new I18nJson();
        JsonIt jsonIt = new JsonIt();
        String i18n = jackson.writeValueAsString(i18nJson);
        String json = jackson.writeValueAsString(jsonIt);
        System.out.println(i18n);
        System.out.println("===========");
        System.out.println(json);
    }

    @Test
    public void testTreeMapGenerator() throws IOException {
        I18nJson i18nJson = new I18nJson();
        JsonIt jsonIt = new JsonIt();
        StringMapGenerator t1 = StringMapGenerator.treeMap();
        StringMapGenerator t2 = StringMapGenerator.linkMap();
        objectMapper.writeValue(t1, i18nJson);
        objectMapper.writeValue(t2, jsonIt);
        System.out.println(t1.getResultTree());
        System.out.println(t2.getResultTree());
        System.out.println("======");
    }

    @Test
    public void testHelper(){
        I18nJson i18nJson = new I18nJson();
        JsonIt jsonIt = new JsonIt();
        Map<String, String> j1 = StringMapHelper.json(i18nJson, objectMapper);
        Map<String, String> j2 = StringMapHelper.json(jsonIt, objectMapper);

        Map<String, String> x1 = StringMapHelper.jaxb(i18nJson);
        Map<String, String> x2 = StringMapHelper.jaxb(jsonIt);

        System.out.println(j1);
        System.out.println(j2);
        System.out.println(x1);
        System.out.println(x2);
    }
}
