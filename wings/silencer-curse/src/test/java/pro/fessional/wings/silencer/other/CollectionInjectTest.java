package pro.fessional.wings.silencer.other;

import io.qameta.allure.TmsLink;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author trydofor
 * @since 2023-03-22
 */

@SpringBootTest
@Slf4j
@Setter(onMethod_ = {@Autowired})
@Getter
public class CollectionInjectTest {

    private List<Dto> listEmpty = Collections.emptyList();
    private List<Dto> listValue0 = new LinkedList<>();
    private List<Dto> listValue1 = new LinkedList<>(List.of(new Dto(1)));

    private Map<String, Dto> mapEmpty = Collections.emptyMap();
    private Map<String, Dto> mapValue0 = new HashMap<>();
    private Map<String, Dto> mapValue1 = new HashMap<>(Map.of("dto1", new Dto(1)));


    @Autowired
    public void setListValue1(List<Dto> listValue1) {
        this.listValue1.addAll(listValue1);
    }

    @Autowired
    public void setMapValue1(Map<String, Dto> mapValue1) {
        this.mapValue1.putAll(mapValue1);
    }

    @Test
    @TmsLink("C11013")
    public void testList() {
        Assertions.assertEquals(1, listEmpty.size());
        Assertions.assertEquals(1, listValue0.size());
        Assertions.assertEquals(2, listValue1.size());
        Assertions.assertEquals(ArrayList.class, listEmpty.getClass());
        Assertions.assertEquals(ArrayList.class, listValue0.getClass());
        Assertions.assertEquals(LinkedList.class, listValue1.getClass());
    }

    @Test
    @TmsLink("C11014")
    public void testMap() {
        Assertions.assertEquals(1, mapEmpty.size());
        Assertions.assertEquals(1, mapValue0.size());
        Assertions.assertEquals(2, mapValue1.size());
        Assertions.assertEquals(LinkedHashMap.class, mapEmpty.getClass());
        Assertions.assertEquals(LinkedHashMap.class, mapValue0.getClass());
        Assertions.assertEquals(HashMap.class, mapValue1.getClass());
    }

    @Data
    public static class Dto {
        private final int id;
    }
}
