package pro.fessional.wings.silencer.spring.bean;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pro.fessional.mirana.code.Crc8Long;
import pro.fessional.mirana.code.LeapCode;

import static org.junit.Assert.*;

/**
 * @author trydofor
 * @since 2019-10-05
 */

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"debug = true"})
public class WingsMiranaConfigurationTest {

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
    public void testCode(){
        long number = 1979L;
        long encode = crc8Long.encode(number);
        long decode = crc8Long.decode(encode);
        assertEquals(number,decode);

        String s = leapCode.encode26(number);
        long decode1 = leapCode.decode(s);
        assertEquals(number,decode1);
    }
}