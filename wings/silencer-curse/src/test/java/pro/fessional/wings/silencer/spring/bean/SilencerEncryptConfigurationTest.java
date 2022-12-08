package pro.fessional.wings.silencer.spring.bean;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.code.Crc8Long;
import pro.fessional.mirana.code.LeapCode;
import pro.fessional.wings.silencer.WingsSilencerCurseApplication;
import pro.fessional.wings.silencer.spring.help.ApplicationContextHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2019-10-05
 */

@SpringBootTest(properties = {"debug = true", "spring.application.name=curse"})
@Slf4j
public class SilencerEncryptConfigurationTest {

    @Setter(onMethod_ = {@Autowired})
    private WingsSilencerCurseApplication.InnerFace innerFace;

    private Crc8Long crc8Long;
    private LeapCode leapCode;

    @Autowired
    public void setCrc8Long(Crc8Long crc8Long) {
        this.crc8Long = crc8Long;
    }

    @Autowired
    public void setLeapCode(LeapCode leapCode) {
        this.leapCode = leapCode;
    }

    @Test
    public void testCode() {
        long number = 1979L;
        long encode = crc8Long.encode(number);
        long decode = crc8Long.decode(encode);
        assertEquals(number, decode);

        String s = leapCode.encode26(number);
        long decode1 = leapCode.decode(s);
        assertEquals(number, decode1);
    }

    @Test
    public void testOther() throws Exception {
        final String app = ApplicationContextHelper.getApplicationName();
        Assertions.assertEquals("curse", app);
        Assertions.assertNotNull(innerFace);
        final Class<?> ic = innerFace.getClass();
        final String nn = ic.getName();
        final String cn = ic.getCanonicalName();
        log.info("getName={}",nn);
        log.info("getCanonicalName={}",cn);
        Class<?> c1 = Class.forName(nn);
        final Object b0 = ApplicationContextHelper.getBean(ic);
        final Object b1 = ApplicationContextHelper.getBean(c1);
        String[] allBeanNames = ApplicationContextHelper.getContext().getBeanDefinitionNames();
        for(String beanName : allBeanNames) {
            log.info(beanName);
        }

        Assertions.assertSame(innerFace, b0);
        Assertions.assertSame(innerFace, b1);
    }
}
