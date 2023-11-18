package pro.fessional.wings.silencer.spring.boot;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import pro.fessional.wings.silencer.app.bean.WingsEnabledCatConfiguration;
import pro.fessional.wings.silencer.app.bean.WingsEnabledDogConfiguration;
import pro.fessional.wings.silencer.app.service.ScanService;

/**
 * @author trydofor
 * @since 2023-11-17
 */
@SpringBootTest(properties = {
        "spring.catty.enabled.catBean=false",
        "spring.catty.enabled.pro.fessional.wings.silencer.app.bean.WingsEnabledCatConfiguration.autowire=false",
        "spring.catty.enabled.pro.fessional.wings.silencer.app.bean.WingsEnabledCatConfiguration.listener=false",
        "spring.catty.enabled.pro.fessional.wings.silencer.app.bean.WingsEnabledCatConfiguration$InnerCatConfiguration=false",
        "spring.kitty.enabled.pro.fessional.wings.silencer.app.bean.WingsEnabledCatConfiguration$ComponentScan=false",

        "spring.wings.enabled.pro.fessional.wings.silencer.app.bean.WingsEnabledDogConfiguration.dogBean=false",
        "spring.wings.enabled.pro.fessional.wings.silencer.app.bean.WingsEnabledDogConfiguration.autowire=false",
        "spring.wings.enabled.pro.fessional.wings.silencer.app.bean.WingsEnabledDogConfiguration.listener=false",
        "spring.wings.enabled.pro.fessional.wings.silencer.app.bean.WingsEnabledDogConfiguration$InnerDogConfiguration=false",
        "spring.wings.enabled.pro.fessional.wings.silencer.app.service.ScanService=false",

        "wings.cat.key-bean=false",
})
@DirtiesContext
public class WingsEnabledFalseTest {

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

    @Setter(onMethod_ = {@Autowired(required = false)})
    protected ScanService scanService;

    @Setter(onMethod_ = {@Autowired(required = false)})
    protected WingsEnabledCatConfiguration.AndBean andBean;
    @Setter(onMethod_ = {@Autowired(required = false)})
    protected WingsEnabledCatConfiguration.NotBean notBean;
    @Setter(onMethod_ = {@Autowired(required = false)})
    protected WingsEnabledCatConfiguration.KeyBean keyBean;

    @Test
    @TmsLink("C11028")
    public void test() {
        Assertions.assertNotNull(wingsEnabledCatConfiguration);
        Assertions.assertNull(catBean);
        Assertions.assertNull(innerCatConfiguration);
        Assertions.assertNull(innerCatBean);

        Assertions.assertNotNull(wingsEnabledDogConfiguration);
        Assertions.assertNull(dogBean);
        Assertions.assertNull(innerDogConfiguration);
        Assertions.assertNull(innerDogBean);

        Assertions.assertNull(scanService);

        Assertions.assertTrue(WingsEnabledCatConfiguration.autowire.get(wingsEnabledCatConfiguration));
//        Assertions.assertTrue(WingsEnabledCatConfiguration.autowire.get(innerCatConfiguration));
        Assertions.assertTrue(WingsEnabledDogConfiguration.autowire.get(wingsEnabledDogConfiguration));
//        Assertions.assertTrue(WingsEnabledDogConfiguration.autowire.get(innerDogConfiguration));

        Assertions.assertTrue(WingsEnabledCatConfiguration.listener.get(wingsEnabledCatConfiguration) > 0);
//        Assertions.assertTrue(WingsEnabledCatConfiguration.listener.get(innerCatConfiguration) > 0);
        Assertions.assertTrue(WingsEnabledDogConfiguration.listener.get(wingsEnabledDogConfiguration) > 0);
//        Assertions.assertTrue(WingsEnabledDogConfiguration.listener.get(innerDogConfiguration) > 0);

        Assertions.assertNull(andBean);
        Assertions.assertNotNull(notBean);
        Assertions.assertNull(keyBean);
    }
}