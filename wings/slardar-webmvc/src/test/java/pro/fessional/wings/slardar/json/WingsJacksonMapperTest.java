package pro.fessional.wings.slardar.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.i18n.I18nString;
import pro.fessional.wings.silencer.datetime.DateTimePattern;
import pro.fessional.wings.slardar.autodto.AutoI18nString;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.slardar.jackson.AesStringDeserializer;
import pro.fessional.wings.slardar.jackson.AesStringSerializer;
import pro.fessional.wings.slardar.jackson.StringMapGenerator;
import pro.fessional.wings.slardar.jackson.StringMapHelper;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static pro.fessional.wings.slardar.context.TerminalAttribute.TerminalAddr;
import static pro.fessional.wings.slardar.context.TerminalAttribute.TerminalAgent;

/**
 * @author trydofor
 * @since 2019-06-26
 */
@SpringBootTest(properties = {"debug = true",
                              "wings.slardar.datetime.zoned.auto=true",
                              "spring.wings.slardar.enabled.number=true",
                              "wings.slardar.jackson.empty-date=1970-01-01",
                              "wings.slardar.jackson.empty-list=true",
                              "wings.slardar.jackson.empty-map=true",
})
@Slf4j
public class WingsJacksonMapperTest {

    final static TimeZone systemTz = TimeZone.getTimeZone("Asia/Shanghai");
    final static TimeZone userTz = TimeZone.getTimeZone("Asia/Tokyo");

    @Setter(onMethod_ = {@Autowired})
    private ObjectMapper objectMapper;

    @Setter(onMethod_ = {@Autowired})
    private Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder;

    @BeforeEach
    public void init() {
        log.info("=== set locale to us ===");
        Locale.setDefault(Locale.US);
        // user timezone
        TimeZone.setDefault(systemTz);
        TerminalContext.Builder builder = new TerminalContext.Builder()
                .locale(Locale.US)
                .timeZone(userTz)
                .terminal(TerminalAddr, "localhost")
                .terminal(TerminalAgent, "test")
                .user(1);
        TerminalContext.login(builder.build());
    }

    @Data
    public static class NamingLombok {
        private Boolean objectType = true;

        public boolean isPrimaryType() {
            return true;
        }
    }

    public static class NamingManual {
        private boolean primaryType = true;
        private Boolean objectType = true;

        public boolean isPrimaryType() {
            return primaryType;
        }

        public void setPrimaryType(boolean primaryType) {
            this.primaryType = primaryType;
        }

        public Boolean getObjectType() {
            return objectType;
        }

        public void setObjectType(Boolean objectType) {
            this.objectType = objectType;
        }
    }

    @Test
    public void testNaming() throws JsonProcessingException {
        NamingManual n1 = new NamingManual();
        String s1 = objectMapper.writeValueAsString(n1);
        log.info(s1);
        NamingLombok n2 = new NamingLombok();
        String s2 = objectMapper.writeValueAsString(n2);
        log.info(s2);
    }

    @Test
    public void testEquals() throws IOException {
        log.info("=== ZoneId= " + ZoneId.systemDefault());
        JsonIt it = new JsonIt();
        log.info("===== to string ======");
        log.info("it={}", it);
        String json = objectMapper.writeValueAsString(it);
        log.info("===== write json ======");
        log.info(json);
        JsonIt obj = objectMapper.readValue(json, JsonIt.class);
        log.info("===== read json ======");
        log.info("obj={}", obj);

        String json2 = objectMapper.writeValueAsString(obj);
        JsonIt obj2 = objectMapper.readValue(json2, JsonIt.class);

        assertEquals(json, json2);
        assertEquals(obj, obj2);
        assertFalse(json.contains("Null"));
        assertFalse(json.contains("Empty"));
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
        private LocalDateTime localDateTimeVal = LocalDateTime.parse("2020-06-01T12:34:46");
        private LocalDate localDateVal = localDateTimeVal.toLocalDate();
        private LocalTime localTimeVal = localDateTimeVal.toLocalTime();
        private ZonedDateTime zonedDateTimeVal = localDateTimeVal.atZone(systemTz.toZoneId());
        @JsonFormat(pattern = DateTimePattern.PTN_FULL_23V)
        private ZonedDateTime zonedDateTimeValV = zonedDateTimeVal.withZoneSameInstant(userTz.toZoneId());
        @JsonFormat(pattern = DateTimePattern.PTN_FULL_23Z)
        private ZonedDateTime zonedDateTimeValZ = zonedDateTimeVal.withZoneSameInstant(userTz.toZoneId());
        private Instant instantVal = Instant.parse("2020-06-01T12:34:46.000Z");
        private List<String> listVal = Arrays.asList("字符串", "列表");
        private Map<String, Long> mapVal = new HashMap<>() {{put("Map", 1L);}};
        // empty
        private LocalDateTime localDateTimeEmpty = LocalDateTime.parse("1970-01-01T00:00:00");
        private LocalDateTime localDateTimeEmpty1 = localDateTimeEmpty.minusHours(12);
        private LocalDateTime localDateTimeEmpty2 = localDateTimeEmpty.plusHours(12);
        private LocalDate localDateEmpty = localDateTimeEmpty.toLocalDate();
        private ZonedDateTime zonedDateTimeEmpty = localDateTimeEmpty.atZone(systemTz.toZoneId());
        private OffsetDateTime offsetDateTimeEmpty = localDateTimeEmpty.atOffset(ZoneOffset.UTC);
        private List<String> listNull = null;
        private Map<String, Long> mapNull = null;
        private List<String> listEmpty = Collections.emptyList();
        private Map<String, Long> mapEmpty = Collections.emptyMap();
        private Integer[] integerArrNull = null;
        private Integer[] integerArrEmpty = new Integer[0];
        private int[] intArrNull = null;
        private int[] intArrEmpty = new int[0];

    }

    @Test
    public void testI18nString() throws IOException {
        I18nJson obj = new I18nJson();
        ObjectWriter jackson = objectMapper.writerWithDefaultPrettyPrinter();
        String json = jackson.writeValueAsString(obj);
        assertEquals("{\n" +
                     "  \"codeManual\" : \"{0} can not be empty\",\n" +
                     "  \"codeIgnore\" : \"base.not-empty\",\n" +
                     "  \"textAuto\" : \"textAuto can not be empty\",\n" +
                     "  \"textDisabled\" : {\n" +
                     "    \"code\" : \"base.not-empty\",\n" +
                     "    \"hint\" : \"\",\n" +
                     "    \"args\" : [ \"textDisabled\" ]\n" +
                     "  },\n" +
                     "  \"longIgnore\" : 0,\n" +
                     "  \"mapIgnore\" : {\n" +
                     "    \"ikey\" : \"ival\"\n" +
                     "  },\n" +
                     "  \"mapDisabled\" : {\n" +
                     "    \"i18n\" : {\n" +
                     "      \"code\" : \"base.not-empty\",\n" +
                     "      \"hint\" : \"\",\n" +
                     "      \"args\" : [ \"textDisabled\" ]\n" +
                     "    }\n" +
                     "  },\n" +
                     "  \"mapAuto\" : {\n" +
                     "    \"i18n\" : \"textAuto can not be empty\"\n" +
                     "  }\n" +
                     "}", json.trim());
    }

    @Data
    @XmlRootElement
    public static class I18nJson {
        @AutoI18nString // 有效
        private String codeManual = "base.not-empty";
        private String codeIgnore = "base.not-empty";
        // 自动
        private I18nString textAuto = new I18nString("base.not-empty", "", "textAuto");
        @AutoI18nString(false) //禁用
        private I18nString textDisabled = new I18nString("base.not-empty", "", "textDisabled");
        @AutoI18nString // 无效
        private Long longIgnore = 0L;
        @AutoI18nString // 无效
        private Map<String, String> mapIgnore = Collections.singletonMap("ikey", "ival");
        @AutoI18nString(false) //禁用
        private Map<String, I18nString> mapDisabled = Collections.singletonMap("i18n", textDisabled);
        // 自动
        private Map<String, I18nString> mapAuto = Collections.singletonMap("i18n", textAuto);
    }


    @Test
    public void testI18nResult() throws IOException {
        ObjectWriter jackson = objectMapper.writerWithDefaultPrettyPrinter();

        R<I18nJson> r1 = R.ok("这是一个消息", new I18nJson());
        String j1 = jackson.writeValueAsString(r1);
        assertEquals("{\n" +
                     "  \"success\" : true,\n" +
                     "  \"message\" : \"这是一个消息\",\n" +
                     "  \"data\" : {\n" +
                     "    \"codeManual\" : \"{0} can not be empty\",\n" +
                     "    \"codeIgnore\" : \"base.not-empty\",\n" +
                     "    \"textAuto\" : \"textAuto can not be empty\",\n" +
                     "    \"textDisabled\" : {\n" +
                     "      \"code\" : \"base.not-empty\",\n" +
                     "      \"hint\" : \"\",\n" +
                     "      \"args\" : [ \"textDisabled\" ]\n" +
                     "    },\n" +
                     "    \"longIgnore\" : 0,\n" +
                     "    \"mapIgnore\" : {\n" +
                     "      \"ikey\" : \"ival\"\n" +
                     "    },\n" +
                     "    \"mapDisabled\" : {\n" +
                     "      \"i18n\" : {\n" +
                     "        \"code\" : \"base.not-empty\",\n" +
                     "        \"hint\" : \"\",\n" +
                     "        \"args\" : [ \"textDisabled\" ]\n" +
                     "      }\n" +
                     "    },\n" +
                     "    \"mapAuto\" : {\n" +
                     "      \"i18n\" : \"textAuto can not be empty\"\n" +
                     "    }\n" +
                     "  }\n" +
                     "}", j1);

        R<I18nJson> r2 = r1.setI18nMessage("base.not-empty", "第一个参数");
        String j2 = jackson.writeValueAsString(r2);
        assertEquals("{\n" +
                     "  \"success\" : true,\n" +
                     "  \"message\" : \"第一个参数 can not be empty\",\n" +
                     "  \"data\" : {\n" +
                     "    \"codeManual\" : \"{0} can not be empty\",\n" +
                     "    \"codeIgnore\" : \"base.not-empty\",\n" +
                     "    \"textAuto\" : \"textAuto can not be empty\",\n" +
                     "    \"textDisabled\" : {\n" +
                     "      \"code\" : \"base.not-empty\",\n" +
                     "      \"hint\" : \"\",\n" +
                     "      \"args\" : [ \"textDisabled\" ]\n" +
                     "    },\n" +
                     "    \"longIgnore\" : 0,\n" +
                     "    \"mapIgnore\" : {\n" +
                     "      \"ikey\" : \"ival\"\n" +
                     "    },\n" +
                     "    \"mapDisabled\" : {\n" +
                     "      \"i18n\" : {\n" +
                     "        \"code\" : \"base.not-empty\",\n" +
                     "        \"hint\" : \"\",\n" +
                     "        \"args\" : [ \"textDisabled\" ]\n" +
                     "      }\n" +
                     "    },\n" +
                     "    \"mapAuto\" : {\n" +
                     "      \"i18n\" : \"textAuto can not be empty\"\n" +
                     "    }\n" +
                     "  }\n" +
                     "}", j2);
    }

    @Test
    public void testXml() throws IOException {
        ObjectMapper xmlMapper = jackson2ObjectMapperBuilder
                .createXmlMapper(true)
                .build();
        I18nJson i18nJson = new I18nJson();
        JsonIt jsonIt = new JsonIt();
        String i18n = xmlMapper.writeValueAsString(i18nJson);
        String json = xmlMapper.writeValueAsString(jsonIt);
        assertEquals(("<I18nJson>\n" +
                      "  <codeManual>{0} can not be empty</codeManual>\n" +
                      "  <codeIgnore>base.not-empty</codeIgnore>\n" +
                      "  <textAuto>textAuto can not be empty</textAuto>\n" +
                      "  <textDisabled>\n" +
                      "    <code>base.not-empty</code>\n" +
                      "    <hint></hint>\n" +
                      "    <args>\n" +
                      "      <item>textDisabled</item>\n" +
                      "    </args>\n" +
                      "  </textDisabled>\n" +
                      "  <longIgnore>0</longIgnore>\n" +
                      "  <mapIgnore>\n" +
                      "    <ikey>ival</ikey>\n" +
                      "  </mapIgnore>\n" +
                      "  <mapDisabled>\n" +
                      "    <i18n>\n" +
                      "      <code>base.not-empty</code>\n" +
                      "      <hint></hint>\n" +
                      "      <args>\n" +
                      "        <item>textDisabled</item>\n" +
                      "      </args>\n" +
                      "    </i18n>\n" +
                      "  </mapDisabled>\n" +
                      "  <mapAuto>\n" +
                      "    <i18n>textAuto can not be empty</i18n>\n" +
                      "  </mapAuto>\n" +
                      "</I18nJson>").replaceAll("\\s", ""), i18n.replaceAll("\\s", ""));
        assertEquals(("<JsonIt>\n" +
                      "  <intVal>2147483646</intVal>\n" +
                      "  <longVal>9223372036854775806</longVal>\n" +
                      "  <floatVal>1.1</floatVal>\n" +
                      "  <doubleVal>2.2</doubleVal>\n" +
                      "  <decimalVal>3.3</decimalVal>\n" +
                      "  <localDateTimeVal>2020-06-01 12:34:46</localDateTimeVal>\n" +
                      "  <localDateVal>2020-06-01</localDateVal>\n" +
                      "  <localTimeVal>12:34:46</localTimeVal>\n" +
                      "  <zonedDateTimeVal>2020-06-01 13:34:46 Asia/Tokyo</zonedDateTimeVal>\n" +
                      "  <zonedDateTimeValV>2020-06-01 13:34:46.000 Asia/Tokyo</zonedDateTimeValV>\n" +
                      "  <zonedDateTimeValZ>2020-06-01 13:34:46.000 +0900</zonedDateTimeValZ>\n" +
                      "  <instantVal>2020-06-01T12:34:46Z</instantVal>\n" +
                      "  <listVal>\n" +
                      "    <listVal>字符串</listVal>\n" +
                      "    <listVal>列表</listVal>\n" +
                      "  </listVal>\n" +
                      "  <mapVal>\n" +
                      "    <Map>1</Map>\n" +
                      "  </mapVal>\n" +
                      "  <bool-val>false</bool-val>\n" +
                      "</JsonIt>").replaceAll("\\s", ""), json.replaceAll("\\s", ""));

        JsonIt xmlJsonIt = xmlMapper.readValue(json, JsonIt.class);
        log.info("it={}", xmlJsonIt);
    }

    @Test
    public void testTreeMapGenerator() throws IOException {
        I18nJson i18nJson = new I18nJson();
        JsonIt jsonIt = new JsonIt();
        StringMapGenerator t1 = StringMapGenerator.treeMap();
        StringMapGenerator t2 = StringMapGenerator.linkMap();
        objectMapper.writeValue(t1, i18nJson);
        objectMapper.writeValue(t2, jsonIt);
        assertEquals("{code=base.not-empty, codeIgnore=base.not-empty, codeManual={0} can not be empty, hint=, i18n=textAuto can not be empty, ikey=ival, longIgnore=0, textAuto=textAuto can not be empty}", t1.getResultTree()
                                                                                                                                                                                                                .toString()
                                                                                                                                                                                                                .trim());
        assertEquals("{intVal=2147483646, longVal=9223372036854775806, floatVal=1.1, doubleVal=2.2, decimalVal=3.3, localDateTimeVal=2020-06-01 12:34:46, localDateVal=2020-06-01, localTimeVal=12:34:46, zonedDateTimeVal=2020-06-01 13:34:46 Asia/Tokyo, zonedDateTimeValV=2020-06-01 13:34:46.000 Asia/Tokyo, zonedDateTimeValZ=2020-06-01 13:34:46.000 +0900, instantVal=2020-06-01T12:34:46Z, listVal=列表, Map=1, bool-val=false}", t2.getResultTree().toString().trim());
    }

    @Test
    public void testHelper() {
        I18nJson i18nJson = new I18nJson();
        JsonIt jsonIt = new JsonIt();
        Map<String, String> j1 = StringMapHelper.json(i18nJson, objectMapper);
        Map<String, String> j2 = StringMapHelper.json(jsonIt, objectMapper);

        Map<String, String> x1 = StringMapHelper.jaxb(i18nJson);
        Map<String, String> x2 = StringMapHelper.jaxb(jsonIt);

        assertEquals("{code=base.not-empty, codeIgnore=base.not-empty, codeManual={0} can not be empty, hint=, i18n=textAuto can not be empty, ikey=ival, longIgnore=0, textAuto=textAuto can not be empty}", j1.toString());
        assertEquals("{Map=1, bool-val=false, decimalVal=3.3, doubleVal=2.2, floatVal=1.1, instantVal=2020-06-01T12:34:46Z, intVal=2147483646, listVal=列表, localDateTimeVal=2020-06-01 12:34:46, localDateVal=2020-06-01, localTimeVal=12:34:46, longVal=9223372036854775806, zonedDateTimeVal=2020-06-01 13:34:46 Asia/Tokyo, zonedDateTimeValV=2020-06-01 13:34:46.000 Asia/Tokyo, zonedDateTimeValZ=2020-06-01 13:34:46.000 +0900}", j2.toString());
        assertEquals("{codeIgnore=base.not-empty, codeManual=base.not-empty, hint=, key=ikey, longIgnore=0, value=ival}", x1.toString());
        assertEquals("{boolVal=false, decimalVal=3.3, doubleVal=2.2, floatVal=1.1, intVal=2147483646, key=Map, listVal=列表, longVal=9223372036854775806, value=1}", x2.toString());
    }

    //
    @Data
    public static class NumberAsString {
        private long numLong = 10000L;
        private int numInt = 10000;
        private double numDouble = 3.14159;
        private BigDecimal numDecimal = new BigDecimal("2.71828");
    }

    @Data
    public static class NumberAsNumber {
        @JsonRawValue()
        private long numLong = 10000L;
        @JsonRawValue()
        private int numInt = 10000;
        @JsonRawValue()
        private double numDouble = 3.14159;
        @JsonRawValue()
        private BigDecimal numDecimal = new BigDecimal("2.71828");
    }

    @Test
    public void testNumber() throws JsonProcessingException {
        NumberAsString nas = new NumberAsString();
        NumberAsNumber nan = new NumberAsNumber();
        String s1 = objectMapper.writeValueAsString(nas);
        String s2 = objectMapper.writeValueAsString(nan);
        //
        assertEquals("{\"numLong\":10000,\"numInt\":10000,\"numDouble\":\"3.14159\",\"numDecimal\":\"2.71828\"}", s1);
        assertEquals("{\"numLong\":10000,\"numInt\":10000,\"numDouble\":3.14159,\"numDecimal\":2.71828}", s2);
    }

    @Data
    public static class Aes256String {
        @JsonSerialize(using = AesStringSerializer.class)
        @JsonDeserialize(using = AesStringDeserializer.class)
        private String aes256;
    }

    @Test
    public void testAes256String() throws JsonProcessingException {
        Aes256String aes = new Aes256String();
        final String txt = "1234567890";
        aes.setAes256(txt);

        NumberAsNumber nan = new NumberAsNumber();
        String s1 = objectMapper.writeValueAsString(aes);
        Aes256String aes2 = objectMapper.readValue(s1, Aes256String.class);

        log.info(s1);
        assertEquals(aes, aes2);
        assertFalse(s1.contains(txt));
    }
}
