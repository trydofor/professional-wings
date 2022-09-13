package pro.fessional.wings.slardar.json;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.slardar.serialize.JSONParser;
import pro.fessional.wings.slardar.serialize.JsonConversion;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author trydofor
 * @since 2022-03-09
 */
@Slf4j
class JsonConversionTest {

    private final JsonConversion conversionService = new JsonConversion();

    @Test
    void canConvert() {
        Assertions.assertTrue(conversionService.canConvert(String.class, Map.class));
        Assertions.assertTrue(conversionService.canConvert(Map.class, String.class));
        Assertions.assertFalse(conversionService.canConvert(StringBuilder.class, Map.class));
        Assertions.assertFalse(conversionService.canConvert(Map.class, StringBuilder.class));

        final TypeDescriptor strTd = TypeDescriptor.valueOf(String.class);
        final TypeDescriptor bldTd = TypeDescriptor.valueOf(StringBuilder.class);
        final TypeDescriptor mapTd = TypeDescriptor.valueOf(Map.class);
        Assertions.assertTrue(conversionService.canConvert(strTd, mapTd));
        Assertions.assertTrue(conversionService.canConvert(mapTd, strTd));
        Assertions.assertFalse(conversionService.canConvert(bldTd, mapTd));
        Assertions.assertFalse(conversionService.canConvert(mapTd, bldTd));
    }

    @Data
    public static class Dto {
        private String name = "Jackson";
        private Integer age = 24;
        private LocalDateTime ldt = LocalDateTime.now();
        private ZonedDateTime zdt = ZonedDateTime.now();
        private BigDecimal ten = new BigDecimal("10.000");
        private Map<String, String> note = new HashMap<>();
        private List<String> grow = new ArrayList<>();
    }

    @Test
    void convert() {
        Dto dto = new Dto();
        Map<String, String> map = new HashMap<>();
        map.put("one", "一");
        map.put("tow", "二");
        List<String> lst = new ArrayList<>();
        lst.add("Mon");
        lst.add("Tur");
        dto.setGrow(lst);
        dto.setNote(map);

        String jsonDto = JSON.toJSONString(dto);
        String jsonMap = JSON.toJSONString(map);
        String jsonLst = JSON.toJSONString(lst);
        log.info("jsonDto=\n" + jsonDto);
        log.info("jsonMap=\n" + jsonMap);
        log.info("jsonLst=\n" + jsonLst);

        Dto dto1 = conversionService.convert(jsonDto, Dto.class);
        Assertions.assertEquals(dto, dto1);
        Object dto2 = conversionService.convert(jsonDto, null, TypeDescriptor.valueOf(Dto.class));
        Assertions.assertEquals(dto, dto2);

        final TypeDescriptor strTd = TypeDescriptor.valueOf(String.class);
        Object map1 = conversionService.convert(jsonMap, null, TypeDescriptor.map(Map.class, strTd, strTd));
        Assertions.assertEquals(map, map1);

        Object lst1 = conversionService.convert(jsonLst, null, TypeDescriptor.collection(List.class, strTd));
        Assertions.assertEquals(lst, lst1);
    }

    @Test
    void parse() {
        Dto dto = new Dto();
        R<Dto> rd = R.okData(dto);
        String rd0 = JSON.toJSONString(rd);
        //
        Type rdt = new TypeReference<R<Dto>>(){}.getType();
        R<Dto> rd1 = JSON.parseObject(rd0, rdt);
        log.info("rd1={}", rd1);
        //
        final ResolvableType tat = ResolvableType.forClassWithGenerics(R.class, Dto.class);
        R<Dto> rd2 = JSON.parseObject(rd0, tat.getType());
        log.info("rd2={}", rd2);
        //
        R<Dto> rd3 = JSONParser.parse(rd0, tat);
        log.info("rd3={}", rd3);

        Assertions.assertEquals(dto, rd1.getData());
        Assertions.assertEquals(dto, rd2.getData());
        Assertions.assertEquals(dto, rd3.getData());
    }
}
