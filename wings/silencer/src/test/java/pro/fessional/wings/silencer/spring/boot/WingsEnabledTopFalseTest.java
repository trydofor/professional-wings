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
        "wings.silencer.conditional.enable[pro.fessional.wings.silencer.app.bean.WingsEnabledCatConfiguration]=false",
        "wings.silencer.conditional.enable[pro.fessional.wings.silencer.app.bean.WingsEnabledDogConfiguration]=false",
})
public class WingsEnabledTopFalseTest {

    @Setter(onMethod_ = {@Autowired(required = false)})
    protected WingsEnabledCatConfiguration.CatBean catBean;
    @Setter(onMethod_ = {@Autowired(required = false)})
    protected WingsEnabledCatConfiguration.InnerCatBean innerCatBean;

    @Setter(onMethod_ = {@Autowired(required = false)})
    protected WingsEnabledDogConfiguration.DogBean dogBean;
    @Setter(onMethod_ = {@Autowired(required = false)})
    protected WingsEnabledDogConfiguration.InnerDogBean innerDogBean;

    @Test
    @TmsLink("C11029")
    public void test() {
        Assertions.assertNull(catBean);
        Assertions.assertNull(innerCatBean);

        Assertions.assertNull(dogBean);
        Assertions.assertNull(innerDogBean);
    }
}