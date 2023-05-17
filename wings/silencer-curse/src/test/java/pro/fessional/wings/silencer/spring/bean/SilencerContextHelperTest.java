package pro.fessional.wings.silencer.spring.bean;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.silencer.WingsSilencerCurseApplication;
import pro.fessional.wings.silencer.spring.help.ApplicationContextHelper;

import java.util.List;
import java.util.Map;

import static pro.fessional.wings.silencer.spring.help.ApplicationContextHelper.PropertySourceUnsupported;

/**
 * @author trydofor
 * @since 2019-10-05
 */

@SpringBootTest(properties = {
        "spring.application.name=curse",
        "wings.silencer.inspect.properties=true",
})
@Slf4j
public class SilencerContextHelperTest {

    @Setter(onMethod_ = {@Autowired})
    private WingsSilencerCurseApplication.InnerFace innerFace;

    @Test
    public void testInnerFace() throws Exception {
        final String app = ApplicationContextHelper.getApplicationName();
        Assertions.assertEquals("curse", app);
        Assertions.assertNotNull(innerFace);
        final Class<?> ic = innerFace.getClass();
        final String nn = ic.getName();
        final String cn = ic.getCanonicalName();
        log.info("getName={}", nn);
        log.info("getCanonicalName={}", cn);
        Class<?> c1 = Class.forName(nn);
        final Object b0 = ApplicationContextHelper.getBean(ic);
        final Object b1 = ApplicationContextHelper.getBean(c1);
        String[] allBeanNames = ApplicationContextHelper.getContext().getBeanDefinitionNames();
        for (String beanName : allBeanNames) {
            log.info(beanName);
        }

        Assertions.assertSame(innerFace, b0);
        Assertions.assertSame(innerFace, b1);
    }

    @Test
    public void testListProperties() {
        final Map<String, String> prop = ApplicationContextHelper.listProperties();
        log.info("=== properties size={} ===", prop.size());
        for (Map.Entry<String, String> en : prop.entrySet()) {
            log.info("{}={}", en.getKey(), en.getValue());
        }
        log.info("=== properties end ===");
    }

    @Test
    public void testListPropertySource() {
        final Map<String, List<String>> prop = ApplicationContextHelper.listPropertySource();
        log.info("=== properties size={} ===", prop.size());
        for (String key : prop.keySet()) {
            log.info("{}", key);
        }
        log.info("{}={}", PropertySourceUnsupported, prop.get(PropertySourceUnsupported));
        log.info("=== properties end ===");
    }

    @Test
    public void testListPropertiesKeys() {
        final Map<String, String> prop = ApplicationContextHelper.listPropertiesKeys();
        log.info("=== properties size={} ===", prop.size());
        for (Map.Entry<String, String> en : prop.entrySet()) {
            log.info("{}={}", en.getKey(), en.getValue());
        }
        String key = "logging.pattern.dateformat";
        log.info("{}={}={}", key, prop.get(key), ApplicationContextHelper.getProperties(key));

        log.info("=== properties end ===");
    }

}
