package pro.fessional.wings.silencer.spring.bean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.code.Crc8Long;
import pro.fessional.mirana.code.LeapCode;
import pro.fessional.mirana.pain.CodeException;
import pro.fessional.mirana.pain.MessageException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2019-10-05
 */

@SpringBootTest(properties = {"debug = true", "wings.silencer.mirana.debug.stack=true"})
public class SilencerMiranaConfigurationTest {

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
    public void testPain() {
        final MessageException me1 = new MessageException("test message");
        final StackTraceElement[] st1 = me1.getStackTrace();
        Assertions.assertTrue(st1.length > 0);

        CodeException.setGlobalStack(false);

        final MessageException me2 = new MessageException("test message");
        final StackTraceElement[] st2 = me2.getStackTrace();
        Assertions.assertFalse(st2.length > 0);
    }
}
