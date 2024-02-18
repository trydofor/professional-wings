package pro.fessional.wings.silencer.spring.boot;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.silencer.app.bean.TestEnabledCat2Configuration;
import pro.fessional.wings.silencer.app.bean.TestEnabledDog2Configuration;

/**
 * @author trydofor
 * @since 2023-11-17
 */
@SpringBootTest(properties = {
        "wings.feature.enable[pro.fessional.wings.silencer.app.bean.TestEnabledCat2Configuration*]=false",
        "wings.feature.enable[pro.fessional.wings.silencer.app.bean.TestEnabledDog2Configuration*]=false",
})
public class WingsEnabledTopFalseTest {

    @Setter(onMethod_ = {@Autowired(required = false)})
    protected TestEnabledCat2Configuration wingsEnabledCat2Configuration;
    @Setter(onMethod_ = {@Autowired(required = false)})
    protected TestEnabledCat2Configuration.Cat2Bean cat2Bean;
    @Setter(onMethod_ = {@Autowired(required = false)})
    protected TestEnabledCat2Configuration.InnerCat2Configuration innerCat2Configuration;
    @Setter(onMethod_ = {@Autowired(required = false)})
    protected TestEnabledCat2Configuration.InnerCat2Bean innerCat2Bean;


    @Setter(onMethod_ = {@Autowired(required = false)})
    protected TestEnabledDog2Configuration wingsEnabledDog2Configuration;
    @Setter(onMethod_ = {@Autowired(required = false)})
    protected TestEnabledDog2Configuration.Dog2Bean dog2Bean;
    @Setter(onMethod_ = {@Autowired(required = false)})
    protected TestEnabledDog2Configuration.InnerDog2Configuration innerDog2Configuration;
    @Setter(onMethod_ = {@Autowired(required = false)})
    protected TestEnabledDog2Configuration.InnerDog2Bean innerDog2Bean;


    @Test
    @TmsLink("C11029")
    public void wingsEnabledTopFalse() {
        Assertions.assertNull(wingsEnabledCat2Configuration, "change code and recompile to pass the testcase");
        Assertions.assertNull(cat2Bean, "change code and recompile to pass the testcase");
        Assertions.assertNull(innerCat2Configuration, "change code and recompile to pass the testcase");
        Assertions.assertNull(innerCat2Bean, "change code and recompile to pass the testcase");

        Assertions.assertNull(wingsEnabledDog2Configuration, "change code and recompile to pass the testcase");
        Assertions.assertNull(dog2Bean, "change code and recompile to pass the testcase");
        Assertions.assertNull(innerDog2Configuration, "change code and recompile to pass the testcase");
        Assertions.assertNull(innerDog2Bean, "change code and recompile to pass the testcase");
    }
}