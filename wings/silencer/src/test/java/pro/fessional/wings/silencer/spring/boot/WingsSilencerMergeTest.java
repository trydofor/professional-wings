package pro.fessional.wings.silencer.spring.boot;

import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.silencer.spring.prop.MergingProp;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author trydofor
 * @since 2019-06-25
 */

@SpringBootTest
public class WingsSilencerMergeTest {

    @Setter(onMethod_ = {@Autowired})
    private MergingProp mergingProp;

    @Test
    public void merge() {
        // replace
        Assertions.assertEquals(List.of("a"), mergingProp.getLst1());
        Assertions.assertEquals(List.of("a"), mergingProp.getLst2());
        Assertions.assertEquals(Set.of("a"), mergingProp.getSet1());
        Assertions.assertEquals(Set.of("a"), mergingProp.getSet2());


        Assertions.assertArrayEquals(new String[]{"a"}, mergingProp.getArr1());
        Assertions.assertArrayEquals(new String[]{"a"}, mergingProp.getArr2());

        // merge
        Assertions.assertEquals(Map.of("a","a","b","b"), mergingProp.getMap2());
        Assertions.assertEquals(Map.of("a","a","b","b"), mergingProp.getMap2());
        // merge
        MergingProp.Pojo po = new MergingProp.Pojo();
        po.setStr1("a");
        po.setStr2("b");
        Assertions.assertEquals(po, mergingProp.getPo1());
        Assertions.assertEquals(po, mergingProp.getPo2());
    }
}
