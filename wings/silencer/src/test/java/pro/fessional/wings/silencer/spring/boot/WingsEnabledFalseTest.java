package pro.fessional.wings.silencer.spring.boot;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.silencer.app.bean.TestEnabledCatConfiguration;
import pro.fessional.wings.silencer.app.bean.TestEnabledDogConfiguration;
import pro.fessional.wings.silencer.app.service.TestScanService;

/**
 * @author trydofor
 * @since 2023-11-17
 */
@SpringBootTest(properties = {
        "wings.feature.prefix[pro.fessional.wings.silencer.app.bean.TestEnabledCatConfiguration*]=catty.enabled",
        "catty.enabled.catBean=true",
        "catty.enabled.pro.fessional.wings.silencer.app.bean.TestEnabledCatConfiguration.catBean=false",
        "catty.enabled.pro.fessional.wings.silencer.app.bean.TestEnabledCatConfiguration.autowire=false",
        "catty.enabled.pro.fessional.wings.silencer.app.bean.TestEnabledCatConfiguration.listener=false",
        "catty.enabled.pro.fessional.wings.silencer.app.bean.TestEnabledCatConfiguration$InnerCatConfiguration=false",
        "kitty.enabled.pro.fessional.wings.silencer.app.bean.TestEnabledCatConfiguration$ComponentScan=false",

        "wings.enabled.pro.fessional.wings.silencer.app.bean.TestEnabledDogConfiguration.dogBean=false",
        "wings.enabled.pro.fessional.wings.silencer.app.bean.TestEnabledDogConfiguration.autowire=false",
        "wings.enabled.pro.fessional.wings.silencer.app.bean.TestEnabledDogConfiguration.listener=false",
        "wings.enabled.pro.fessional.wings.silencer.app.bean.TestEnabledDogConfiguration$InnerDogConfiguration=false",
        "wings.enabled.pro.fessional.wings.silencer.app.service.TestScanService=false",

        "wings.cat.key-bean=false",
})
public class WingsEnabledFalseTest {

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

    @Setter(onMethod_ = {@Autowired(required = false)})
    protected TestScanService testScanService;

    @Setter(onMethod_ = {@Autowired(required = false)})
    protected TestEnabledCatConfiguration.AndBean andBean;
    @Setter(onMethod_ = {@Autowired(required = false)})
    protected TestEnabledCatConfiguration.NotBean notBean;
    @Setter(onMethod_ = {@Autowired(required = false)})
    protected TestEnabledCatConfiguration.KeyBean keyBean;

    @Test
    @TmsLink("C11028")
    public void wingsEnabledFalse() {
        Assertions.assertNotNull(wingsEnabledCatConfiguration);
        Assertions.assertNull(catBean);
        Assertions.assertNull(innerCatConfiguration);
        Assertions.assertNull(innerCatBean);

        Assertions.assertNotNull(wingsEnabledDogConfiguration);
        Assertions.assertNull(dogBean);
        Assertions.assertNull(innerDogConfiguration);
        Assertions.assertNull(innerDogBean);

        Assertions.assertNull(testScanService);

        Assertions.assertTrue(TestEnabledCatConfiguration.autowire.get(wingsEnabledCatConfiguration));
//        Assertions.assertTrue(TestEnabledCatConfiguration.autowire.get(innerCatConfiguration));
        Assertions.assertTrue(TestEnabledDogConfiguration.autowire.get(wingsEnabledDogConfiguration));
//        Assertions.assertTrue(TestEnabledDogConfiguration.autowire.get(innerDogConfiguration));

        Assertions.assertTrue(TestEnabledCatConfiguration.listener.get(wingsEnabledCatConfiguration) > 0);
//        Assertions.assertTrue(TestEnabledCatConfiguration.listener.get(innerCatConfiguration) > 0);
        Assertions.assertTrue(TestEnabledDogConfiguration.listener.get(wingsEnabledDogConfiguration) > 0);
//        Assertions.assertTrue(TestEnabledDogConfiguration.listener.get(innerDogConfiguration) > 0);

        Assertions.assertNull(andBean);
        Assertions.assertNotNull(notBean);
        Assertions.assertNull(keyBean);
    }
}