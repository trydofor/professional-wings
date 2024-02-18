package pro.fessional.wings.silencer.spring.boot;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.silencer.app.bean.TestEnabledCatConfiguration;
import pro.fessional.wings.silencer.app.bean.TestEnabledDogConfiguration;

/**
 * @author trydofor
 * @since 2023-11-17
 */
@SpringBootTest(properties = {
        "wings.feature.enable[pro.fessional.wings.silencer.app.bean.TestEnabledCatConfiguration]=false",
        "wings.feature.enable[pro.fessional.wings.silencer.app.bean.TestEnabledDogConfiguration]=false",
})
@Disabled("Investigate: Run separately to avoid impact")
public class WingsEnabledTopFalseTest {

    @Setter(onMethod_ = {@Autowired(required = false)})
    protected TestEnabledCatConfiguration wingsEnabledCatConfiguration;
    @Setter(onMethod_ = {@Autowired(required = false)})
    protected TestEnabledCatConfiguration.CatBean catBean;
    @Setter(onMethod_ = {@Autowired(required = false)})
    protected TestEnabledCatConfiguration.InnerCatConfiguration innerCatConfiguration;
    @Setter(onMethod_ = {@Autowired(required = false)})
    protected TestEnabledCatConfiguration.InnerCatBean innerCatBean;


    @Setter(onMethod_ = {@Autowired(required = false)})
    protected TestEnabledDogConfiguration wingsEnabledDogConfiguration;
    @Setter(onMethod_ = {@Autowired(required = false)})
    protected TestEnabledDogConfiguration.DogBean dogBean;
    @Setter(onMethod_ = {@Autowired(required = false)})
    protected TestEnabledDogConfiguration.InnerDogConfiguration innerDogConfiguration;
    @Setter(onMethod_ = {@Autowired(required = false)})
    protected TestEnabledDogConfiguration.InnerDogBean innerDogBean;


    @Test
    @TmsLink("C11029")
    public void wingsEnabledTopFalse() {
        Assertions.assertNull(wingsEnabledCatConfiguration, "change code and recompile to pass the testcase");
        Assertions.assertNull(catBean, "change code and recompile to pass the testcase");
        Assertions.assertNull(innerCatConfiguration, "change code and recompile to pass the testcase");
        Assertions.assertNull(innerCatBean, "change code and recompile to pass the testcase");

        Assertions.assertNull(wingsEnabledDogConfiguration, "change code and recompile to pass the testcase");
        Assertions.assertNull(dogBean, "change code and recompile to pass the testcase");
        Assertions.assertNull(innerDogConfiguration, "change code and recompile to pass the testcase");
        Assertions.assertNull(innerDogBean, "change code and recompile to pass the testcase");
    }
}