package pro.fessional.wings.silencer.spring.help;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Locale;

/**
 * @author trydofor
 * @since 2019-09-16
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CombinableMessageSourceTest {

    private MessageSource message;
    private CombinableMessageSource combinableMessageSource;

    @Autowired
    public void setMessage(MessageSource message) {
        this.message = message;
    }

    @Autowired
    public void setCombinableMessageSource(CombinableMessageSource combinableMessageSource) {
        this.combinableMessageSource = combinableMessageSource;
    }

    @Test
    public void combine() {

        Object[] args = {};
        String m1 = message.getMessage("test.我的测试", args, Locale.CHINA);
        combinableMessageSource.addMessage("test.我的测试", Locale.CHINA, "啥都好用");
        String m2 = message.getMessage("test.我的测试", args, Locale.CHINA);

        StaticMessageSource sms = new StaticMessageSource();
        sms.addMessage("test.mytest", Locale.CHINA, "又一个测试");
        combinableMessageSource.addMessages(sms, 1);
        String m3 = message.getMessage("test.mytest", args, Locale.CHINA);

        Assert.assertEquals("test.我的测试", m1);// code
        Assert.assertEquals("啥都好用", m2);
        Assert.assertEquals("又一个测试", m3);
    }
}