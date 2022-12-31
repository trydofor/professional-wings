package pro.fessional.wings.tiny.mail;

import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author trydofor
 * @since 2022-12-28
 */
@SpringBootTest(properties = {
        "debug = true"
})public class JavaMailSenderTest {

    @Setter(onMethod_ = {@Autowired})
    protected JavaMailSender javaMailSender;

    @Test
    public void test(){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("trydofor@qq.com");
        message.setTo("2569855@qq.com");
        message.setSubject("test tiny mail");
        message.setText("test");
        javaMailSender.send(message);
    }
}
