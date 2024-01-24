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
@SpringBootTest
public class WingsEnabledDefaultTest {

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
    @TmsLink("C11027")
    public void wingsEnabledDefault() {
        Assertions.assertNotNull(wingsEnabledCatConfiguration);
        Assertions.assertNotNull(catBean);
        Assertions.assertNotNull(innerCatConfiguration);
        Assertions.assertNotNull(innerCatBean);

        Assertions.assertNotNull(wingsEnabledDogConfiguration);
        Assertions.assertNotNull(dogBean);
        Assertions.assertNotNull(innerDogConfiguration);
        Assertions.assertNotNull(innerDogBean);

        Assertions.assertNotNull(testScanService);

        Assertions.assertTrue(TestEnabledCatConfiguration.autowire.get(wingsEnabledCatConfiguration));
        Assertions.assertTrue(TestEnabledCatConfiguration.autowire.get(innerCatConfiguration));
        Assertions.assertTrue(TestEnabledDogConfiguration.autowire.get(wingsEnabledDogConfiguration));
        Assertions.assertTrue(TestEnabledDogConfiguration.autowire.get(innerDogConfiguration));

        Assertions.assertTrue(TestEnabledCatConfiguration.listener.get(wingsEnabledCatConfiguration) > 0);
        Assertions.assertTrue(TestEnabledCatConfiguration.listener.get(innerCatConfiguration) > 0);
        Assertions.assertTrue(TestEnabledDogConfiguration.listener.get(wingsEnabledDogConfiguration) > 0);
        Assertions.assertTrue(TestEnabledDogConfiguration.listener.get(innerDogConfiguration) > 0);

        Assertions.assertNotNull(andBean);
        Assertions.assertNull(notBean);
        Assertions.assertNotNull(keyBean);
    }
}