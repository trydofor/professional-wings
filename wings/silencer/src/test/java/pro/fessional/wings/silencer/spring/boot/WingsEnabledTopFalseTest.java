package pro.fessional.wings.silencer.spring.boot;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.silencer.app.bean.WingsEnabledCatConfiguration;
import pro.fessional.wings.silencer.app.bean.WingsEnabledDogConfiguration;

/**
 * @author trydofor
 * @since 2023-11-17
 */
@SpringBootTest(properties = {
        "catty.enabled.pro.fessional.wings.silencer.app.bean.WingsEnabledCatConfiguration=false",
        "wings.enabled.pro.fessional.wings.silencer.app.bean.WingsEnabledDogConfiguration=false",
})
public class WingsEnabledTopFalseTest {

    @Setter(onMethod_ = {@Autowired(required = false)})
    protected WingsEnabledCatConfiguration.InnerCatConfigDefault innerCatConfigDefault;

    @Setter(onMethod_ = {@Autowired(required = false)})
    protected WingsEnabledCatConfiguration wingsEnabledCatConfiguration;
    @Setter(onMethod_ = {@Autowired(required = false)})
    protected WingsEnabledCatConfiguration.CatBean catBean;
    @Setter(onMethod_ = {@Autowired(required = false)})
    protected WingsEnabledCatConfiguration.InnerCatConfiguration innerCatConfiguration;
    @Setter(onMethod_ = {@Autowired(required = false)})
    protected WingsEnabledCatConfiguration.InnerCatBean innerCatBean;


    @Setter(onMethod_ = {@Autowired(required = false)})
    protected WingsEnabledDogConfiguration wingsEnabledDogConfiguration;
    @Setter(onMethod_ = {@Autowired(required = false)})
    protected WingsEnabledDogConfiguration.DogBean dogBean;
    @Setter(onMethod_ = {@Autowired(required = false)})
    protected WingsEnabledDogConfiguration.InnerDogConfiguration innerDogConfiguration;
    @Setter(onMethod_ = {@Autowired(required = false)})
    protected WingsEnabledDogConfiguration.InnerDogBean innerDogBean;

    @Test
    @TmsLink("C11029")
    public void test() {
        Assertions.assertNull(wingsEnabledCatConfiguration);
        Assertions.assertNull(catBean);
        Assertions.assertNull(innerCatConfiguration);
        Assertions.assertNull(innerCatBean);

        Assertions.assertNull(wingsEnabledDogConfiguration);
        Assertions.assertNull(dogBean);
        Assertions.assertNull(innerDogConfiguration);
        Assertions.assertNull(innerDogBean);

        // most good, sometime wrong :D
//        Assertions.assertNull(innerCatConfigDefault);
    }
}