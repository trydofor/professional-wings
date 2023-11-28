package pro.fessional.wings.silencer.spring.boot;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import pro.fessional.wings.silencer.scanner.bean.TestBeanScan;
import pro.fessional.wings.silencer.scanner.noscan.TestBeanNoScan;

/**
 * @author trydofor
 * @since 2023-10-27
 */
@SpringBootTest(properties = {
        "wings.enabled.silencer.scanner=true",
        "wings.silencer.scanner.bean=scanner/bean, /ti12/lgd /ti12/ar//",
})
class WingsSpringBeanScannerTest {

    @Setter(onMethod_ = {@Autowired})
    private ApplicationContext applicationContext;


    @Test
    @TmsLink("C11025")
    public void test() {
        applicationContext.getBean(TestBeanScan.class);

        try {
            applicationContext.getBean(TestBeanNoScan.class);
            Assertions.fail();
        }
        catch (NoSuchBeanDefinitionException e) {
            Assertions.assertTrue(true);
        }
    }
}