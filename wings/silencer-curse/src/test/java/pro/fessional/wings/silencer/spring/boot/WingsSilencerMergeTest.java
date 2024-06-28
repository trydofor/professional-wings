package pro.fessional.wings.silencer.spring.boot;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.silencer.app.conf.TestMergingProp;

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
    private TestMergingProp testMergingProp;

    @Test
    @TmsLink("C11005")
    public void propMergeOrReplace() {
        // replace
        Assertions.assertEquals(List.of("a"), testMergingProp.getLst1());
        Assertions.assertEquals(List.of("a"), testMergingProp.getLst2());
        Assertions.assertEquals(Set.of("a"), testMergingProp.getSet1());
        Assertions.assertEquals(Set.of("a"), testMergingProp.getSet2());


        Assertions.assertArrayEquals(new String[]{"a"}, testMergingProp.getArr1());
        Assertions.assertArrayEquals(new String[]{"a"}, testMergingProp.getArr2());

        // merge
        Assertions.assertEquals(Map.of("a","a","b","b"), testMergingProp.getMap2());
        Assertions.assertEquals(Map.of("a","a","b","b"), testMergingProp.getMap2());
        // merge
        TestMergingProp.Pojo po = new TestMergingProp.Pojo();
        po.setStr1("a");
        po.setStr2("b");
        Assertions.assertEquals(po, testMergingProp.getPo1());
        Assertions.assertEquals(po, testMergingProp.getPo2());
    }
}
