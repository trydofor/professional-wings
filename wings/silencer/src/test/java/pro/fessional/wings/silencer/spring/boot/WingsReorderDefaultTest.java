package pro.fessional.wings.silencer.spring.boot;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.silencer.app.bean.TestReorderConfiguration.GetterClass;
import pro.fessional.wings.silencer.app.bean.TestReorderConfiguration.OrderedClass;
import pro.fessional.wings.silencer.app.bean.TestReorderConfiguration.PlainClass;
import pro.fessional.wings.silencer.app.service.TestReorderService;

import java.util.List;

/**
 * @author trydofor
 * @since 2019-06-25
 */

@SpringBootTest(properties = {
        "wings.reorder.plainClass2=3",
        "wings.reorder.getterClass2=3",
        "wings.reorder.orderedClass2=3",
        "wings.reorder.testReorderServiceImpl2=3",
        "wings.enabled.silencer.bean-reorder=false",
})
public class WingsReorderDefaultTest {

    @Setter(onMethod_ = {@Autowired})
    private List<GetterClass> getterClasses;

    @Setter(onMethod_ = {@Autowired})
    private List<OrderedClass> orderedClass;

    @Setter(onMethod_ = {@Autowired})
    private List<PlainClass> plainClasses;

    @Setter(onMethod_ = {@Autowired})
    private List<TestReorderService> services;

    @Setter(onMethod_ = {@Autowired})
    private ObjectProvider<GetterClass> getterClassesProvider;

    @Setter(onMethod_ = {@Autowired})
    private ObjectProvider<OrderedClass> orderedClassProvider;

    @Setter(onMethod_ = {@Autowired})
    private ObjectProvider<PlainClass> plainClassProvider;

    @Setter(onMethod_ = {@Autowired})
    private ObjectProvider<TestReorderService> servicesProvider;

    @Test
    @TmsLink("C11031")
    public void orderDefault() {
        // Defined order
        Assertions.assertEquals("1,2", toString(getterClassesProvider.stream().toList()));
        Assertions.assertEquals("1,2", toString(orderedClassProvider.stream().toList()));
        Assertions.assertEquals("1,2", toString(plainClassProvider.stream().toList()));
        // scaned order
        // Assertions.assertEquals("2,1", toString(servicesProvider.stream().toList()));

        // @Order > Ordered.getOrder first only in @Bean, because the sort obj is methond ,not impl Ordered
        Assertions.assertEquals("2,1", toString(getterClasses));
        Assertions.assertEquals("2,1", toString(orderedClass));
        Assertions.assertEquals("2,1", toString(plainClasses));
        // Ordered.getOrder first, in @Service, the sort object is impl Ordered
        Assertions.assertEquals("2,1", toString(services));

        Assertions.assertEquals("2,1", toString(getterClassesProvider.orderedStream().toList()));
        Assertions.assertEquals("2,1", toString(orderedClassProvider.orderedStream().toList()));
        Assertions.assertEquals("2,1", toString(plainClassProvider.orderedStream().toList()));
        Assertions.assertEquals("2,1", toString(servicesProvider.orderedStream().toList()));

        TestReorderService impl2 = servicesProvider.getIfUnique();
        Assertions.assertNull(impl2);
    }

    private String toString(List<?> lst) {
        Assertions.assertNotNull(lst);
        StringBuilder buf = new StringBuilder();
        for (Object o : lst) {
            buf.append(',');
            buf.append(o.toString());
        }
        return buf.substring(1);
    }
}
