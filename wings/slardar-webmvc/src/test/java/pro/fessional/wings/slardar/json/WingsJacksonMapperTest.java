package pro.fessional.wings.slardar.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.qameta.allure.TmsLink;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.i18n.I18nString;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.silencer.datetime.DateTimePattern;
import pro.fessional.wings.silencer.encrypt.Aes256Provider;
import pro.fessional.wings.silencer.encrypt.SecretProvider;
import pro.fessional.wings.slardar.autodto.AutoI18nString;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.slardar.jackson.AesString;
import pro.fessional.wings.slardar.jackson.StringMapGenerator;
import pro.fessional.wings.slardar.jackson.StringMapHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pro.fessional.wings.silencer.spring.help.CommonPropHelper.MaskingValue;
import static pro.fessional.wings.slardar.context.TerminalAttribute.TerminalAddr;
import static pro.fessional.wings.slardar.context.TerminalAttribute.TerminalAgent;

/**
 * @author trydofor
 * @since 2019-06-26
 */
@SpringBootTest(properties = {
        "wings.slardar.datetime.zoned.auto=true",
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
    public void setup() {
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

    @SuppressWarnings("LombokGetterMayBeUsed")
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
    @TmsLink("C13067")
    public void testNaming() throws JsonProcessingException {
        NamingManual n1 = new NamingManual();
        String s1 = objectMapper.writeValueAsString(n1);
        log.info(s1);
        NamingLombok n2 = new NamingLombok();
        String s2 = objectMapper.writeValueAsString(n2);
        log.info(s2);
    }

    @Test
    @TmsLink("C13068")
    public void testEquals() throws IOException {
        log.info("=== ZoneId= " + ThreadNow.sysZoneId());
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
        private List<String> listVal = Arrays.asList("String", "List");
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
    @TmsLink("C13069")
    public void testI18nString() throws IOException {
        I18nJson obj = new I18nJson();
        ObjectWriter jackson = objectMapper.writerWithDefaultPrettyPrinter();
        String json = jackson.writeValueAsString(obj);
        assertEquals("""
                {
                  "codeManual" : "{0} can not be empty",
                  "codeIgnore" : "base.not-empty",
                  "textAuto" : "textAuto can not be empty",
                  "textDisabled" : {
                    "code" : "base.not-empty",
                    "hint" : "",
                    "args" : [ "textDisabled" ]
                  },
                  "longIgnore" : 0,
                  "mapIgnore" : {
                    "ikey" : "ival"
                  },
                  "mapDisabled" : {
                    "i18n" : {
                      "code" : "base.not-empty",
                      "hint" : "",
                      "args" : [ "textDisabled" ]
                    }
                  },
                  "mapAuto" : {
                    "i18n" : "textAuto can not be empty"
                  }
                }""", json.trim());
    }

    @Data
    @XmlRootElement
    public static class I18nJson {
        @AutoI18nString // valid
        private String codeManual = "base.not-empty";
        private String codeIgnore = "base.not-empty";
        // auto
        private I18nString textAuto = new I18nString("base.not-empty", "", "textAuto");
        @AutoI18nString(false) // disable
        private I18nString textDisabled = new I18nString("base.not-empty", "", "textDisabled");
        @AutoI18nString // invalid
        private Long longIgnore = 0L;
        @AutoI18nString // invalid
        private Map<String, String> mapIgnore = Collections.singletonMap("ikey", "ival");
        @AutoI18nString(false) //disable
        private Map<String, I18nString> mapDisabled = Collections.singletonMap("i18n", textDisabled);
        // auto
        private Map<String, I18nString> mapAuto = Collections.singletonMap("i18n", textAuto);
    }


    @Test
    @TmsLink("C13070")
    public void testI18nResult() throws IOException {
        ObjectWriter jackson = objectMapper.writerWithDefaultPrettyPrinter();

        R<I18nJson> r1 = R.ok("This is a message", new I18nJson());
        String j1 = jackson.writeValueAsString(r1);
        assertEquals("""
                {
                  "success" : true,
                  "message" : "This is a message",
                  "data" : {
                    "codeManual" : "{0} can not be empty",
                    "codeIgnore" : "base.not-empty",
                    "textAuto" : "textAuto can not be empty",
                    "textDisabled" : {
                      "code" : "base.not-empty",
                      "hint" : "",
                      "args" : [ "textDisabled" ]
                    },
                    "longIgnore" : 0,
                    "mapIgnore" : {
                      "ikey" : "ival"
                    },
                    "mapDisabled" : {
                      "i18n" : {
                        "code" : "base.not-empty",
                        "hint" : "",
                        "args" : [ "textDisabled" ]
                      }
                    },
                    "mapAuto" : {
                      "i18n" : "textAuto can not be empty"
                    }
                  }
                }""", j1);

        R<I18nJson> r2 = r1.setI18nMessage("base.not-empty", "Param1");
        String j2 = jackson.writeValueAsString(r2);
        assertEquals("""
                {
                  "success" : true,
                  "message" : "Param1 can not be empty",
                  "data" : {
                    "codeManual" : "{0} can not be empty",
                    "codeIgnore" : "base.not-empty",
                    "textAuto" : "textAuto can not be empty",
                    "textDisabled" : {
                      "code" : "base.not-empty",
                      "hint" : "",
                      "args" : [ "textDisabled" ]
                    },
                    "longIgnore" : 0,
                    "mapIgnore" : {
                      "ikey" : "ival"
                    },
                    "mapDisabled" : {
                      "i18n" : {
                        "code" : "base.not-empty",
                        "hint" : "",
                        "args" : [ "textDisabled" ]
                      }
                    },
                    "mapAuto" : {
                      "i18n" : "textAuto can not be empty"
                    }
                  }
                }""", j2);
    }

    @Test
    @TmsLink("C13071")
    public void testXml() throws IOException {
        ObjectMapper xmlMapper = jackson2ObjectMapperBuilder
                .createXmlMapper(true)
                .build();
        I18nJson i18nJson = new I18nJson();
        JsonIt jsonIt = new JsonIt();
        String i18n = xmlMapper.writeValueAsString(i18nJson);
        String json = xmlMapper.writeValueAsString(jsonIt);
        assertEquals(("""
                              <I18nJson>
                                <codeManual>{0} can not be empty</codeManual>
                                <codeIgnore>base.not-empty</codeIgnore>
                                <textAuto>textAuto can not be empty</textAuto>
                                <textDisabled>
                                  <code>base.not-empty</code>
                                  <hint></hint>
                                  <args>
                                    <item>textDisabled</item>
                                  </args>
                                </textDisabled>
                                <longIgnore>0</longIgnore>
                                <mapIgnore>
                                  <ikey>ival</ikey>
                                </mapIgnore>
                                <mapDisabled>
                                  <i18n>
                                    <code>base.not-empty</code>
                                    <hint></hint>
                                    <args>
                                      <item>textDisabled</item>
                                    </args>
                                  </i18n>
                                </mapDisabled>
                                <mapAuto>
                                  <i18n>textAuto can not be empty</i18n>
                                </mapAuto>
                              </I18nJson>""").replaceAll("\\s", ""), i18n.replaceAll("\\s", ""));
        assertEquals(("""
                              <JsonIt>
                                <intVal>2147483646</intVal>
                                <longVal>9223372036854775806</longVal>
                                <floatVal>1.1</floatVal>
                                <doubleVal>2.2</doubleVal>
                                <decimalVal>3.3</decimalVal>
                                <localDateTimeVal>2020-06-01 12:34:46</localDateTimeVal>
                                <localDateVal>2020-06-01</localDateVal>
                                <localTimeVal>12:34:46</localTimeVal>
                                <zonedDateTimeVal>2020-06-01 13:34:46 Asia/Tokyo</zonedDateTimeVal>
                                <zonedDateTimeValV>2020-06-01 13:34:46.000 Asia/Tokyo</zonedDateTimeValV>
                                <zonedDateTimeValZ>2020-06-01 13:34:46.000 +0900</zonedDateTimeValZ>
                                <instantVal>2020-06-01T12:34:46Z</instantVal>
                                <listVal>
                                  <listVal>String</listVal>
                                  <listVal>List</listVal>
                                </listVal>
                                <mapVal>
                                  <Map>1</Map>
                                </mapVal>
                                <bool-val>false</bool-val>
                              </JsonIt>""").replaceAll("\\s", ""), json.replaceAll("\\s", ""));

        JsonIt xmlJsonIt = xmlMapper.readValue(json, JsonIt.class);
        log.info("it={}", xmlJsonIt);
    }

    @Test
    @TmsLink("C13072")
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
        assertEquals("{intVal=2147483646, longVal=9223372036854775806, floatVal=1.1, doubleVal=2.2, decimalVal=3.3, localDateTimeVal=2020-06-01 12:34:46, localDateVal=2020-06-01, localTimeVal=12:34:46, zonedDateTimeVal=2020-06-01 13:34:46 Asia/Tokyo, zonedDateTimeValV=2020-06-01 13:34:46.000 Asia/Tokyo, zonedDateTimeValZ=2020-06-01 13:34:46.000 +0900, instantVal=2020-06-01T12:34:46Z, listVal=List, Map=1, bool-val=false}", t2.getResultTree().toString().trim());
    }

    @Test
    @TmsLink("C13073")
    public void testStringMapHelper() {
        I18nJson i18nJson = new I18nJson();
        JsonIt jsonIt = new JsonIt();
        Map<String, String> j1 = StringMapHelper.json(i18nJson, objectMapper);
        Map<String, String> j2 = StringMapHelper.json(jsonIt, objectMapper);

        Map<String, String> x1 = StringMapHelper.jaxb(i18nJson);
        Map<String, String> x2 = StringMapHelper.jaxb(jsonIt);

        assertEquals("{code=base.not-empty, codeIgnore=base.not-empty, codeManual={0} can not be empty, hint=, i18n=textAuto can not be empty, ikey=ival, longIgnore=0, textAuto=textAuto can not be empty}", j1.toString());
        assertEquals("{Map=1, bool-val=false, decimalVal=3.3, doubleVal=2.2, floatVal=1.1, instantVal=2020-06-01T12:34:46Z, intVal=2147483646, listVal=List, localDateTimeVal=2020-06-01 12:34:46, localDateVal=2020-06-01, localTimeVal=12:34:46, longVal=9223372036854775806, zonedDateTimeVal=2020-06-01 13:34:46 Asia/Tokyo, zonedDateTimeValV=2020-06-01 13:34:46.000 Asia/Tokyo, zonedDateTimeValZ=2020-06-01 13:34:46.000 +0900}", j2.toString());
        assertEquals("{codeIgnore=base.not-empty, codeManual=base.not-empty, hint=, key=ikey, longIgnore=0, value=ival}", x1.toString());
        assertEquals("{boolVal=false, decimalVal=3.3, doubleVal=2.2, floatVal=1.1, intVal=2147483646, key=Map, listVal=List, longVal=9223372036854775806, value=1}", x2.toString());
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
    @TmsLink("C13074")
    public void testNumber() throws JsonProcessingException {
        NumberAsString nas = new NumberAsString();
        NumberAsNumber nan = new NumberAsNumber();
        String s1 = objectMapper.writeValueAsString(nas);
        String s2 = objectMapper.writeValueAsString(nan);
        //
        assertEquals("{\"numLong\":10000,\"numInt\":10000,\"numDouble\":\"3.14159\",\"numDecimal\":\"2.71828\"}", s1);
        assertEquals("{\"numLong\":10000,\"numInt\":10000,\"numDouble\":3.14159,\"numDecimal\":2.71828}", s2);
    }


    @Test
    @TmsLink("C13075")
    public void testResource() throws JsonProcessingException {
        Map<String, Resource> res = new HashMap<>();
        res.put("wings-jackson-79.properties", new ClassPathResource("wings-conf/wings-jackson-79.properties"));
        final String json = objectMapper.writeValueAsString(res);
        Assertions.assertEquals("{\"wings-jackson-79.properties\":\"classpath:wings-conf/wings-jackson-79.properties\"}", json);
    }

    @Data
    public static class Aes256String {
        @AesString
        private String aes256;
        @AesString(SecretProvider.Config)
        private String aes256Config;
        @AesString(value = "unknown", misfire = AesString.Misfire.Masks)
        private String aes256Mask;
        @AesString(value = "unknown", misfire = AesString.Misfire.Empty)
        private String aes256Empty;
    }


    @Test
    @TmsLink("C13076")
    public void testAes256String() throws JsonProcessingException {
        Aes256String aes = new Aes256String();
        final String txt = "1234567890";
        aes.setAes256(txt);
        aes.setAes256Config(txt);
        aes.setAes256Mask(txt);
        aes.setAes256Empty(txt);

        String s1 = objectMapper.writeValueAsString(aes);
        log.info(s1);

        Aes256String aes2 = objectMapper.readValue(s1, Aes256String.class);

        final String as = Aes256Provider.system().encode64(txt);
        final String ac = Aes256Provider.config().encode64(txt);

        assertTrue(s1.contains(as));
        assertTrue(s1.contains(ac));
        assertTrue(s1.contains(MaskingValue));
        assertFalse(s1.contains(txt));

        aes.setAes256Mask(MaskingValue);
        aes.setAes256Empty(Null.Str);
        assertEquals(aes, aes2);
    }

    @Data
    @JacksonXmlRootElement(localName = "XmlRoot") // set RootName
    @JsonPropertyOrder(alphabetic = true) // make sure order
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES) // case ignore
    public static class MsNamingXml {
        @JsonProperty("Amount") // specify Upper name
        private BigDecimal Amount;
        private String Soap; // default lower name
    }

    @Test
    @TmsLink("C13077")
    public void testMsNamingXml() throws JsonProcessingException {
        final XmlMapper xmlMapper = jackson2ObjectMapperBuilder
                .createXmlMapper(true)
                .build();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        final MsNamingXml xmlOri = new MsNamingXml();
        xmlOri.setSoap("str");
        xmlOri.setAmount(new BigDecimal("3.14"));

        final String xmlHead = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        final String xmlStr = xmlMapper.writeValueAsString(xmlOri);
        log.info("testMsNamingXml={}", xmlStr);

        final String xmlStrUpper = """
                <XmlRoot>
                    <Amount>3.14</Amount>
                    <Soap>str</Soap>
                </XmlRoot>
                """.replaceAll("\\s", "");
        final String xmlStrLower = xmlStrUpper.replace("Soap", "soap");
        assertEquals(xmlStrLower, xmlStr); // filed name is lowercase by getter naming

        MsNamingXml xmlObjLower = xmlMapper.readValue(xmlHead + xmlStrLower, MsNamingXml.class);
        MsNamingXml xmlObjUpper = xmlMapper.readValue(xmlHead + xmlStrUpper, MsNamingXml.class);
        assertEquals(xmlOri, xmlObjUpper);
        assertEquals(xmlOri, xmlObjLower);
    }
}
