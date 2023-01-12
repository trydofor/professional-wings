package pro.fessional.wings.tiny.mail.service;

import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author trydofor
 * @since 2023-01-10
 */
@SpringBootTest(properties = {
        "debug = true"
})
@Disabled
class TinyMailServiceTest {

    @Setter(onMethod_ = {@Autowired})
    protected TinyMailService tinyMailService;

    @Test
    void sendOk() {
        TinyMailService.TinyMail message = new TinyMailService.TinyMail();
        message.setSubject("Mail Service Test");
        message.setContextHtml("Nothing");
        tinyMailService.send(message, true);
    }

    // 501 Mail from address must be same as authorization user.
    @Test
    void sendNgFrom() {
        TinyMailService.TinyMail message = new TinyMailService.TinyMail();
        message.setSubject("Mail Service Test");
        message.setContextHtml("Nothing");
        message.setFrom("admin@qq.com");
        tinyMailService.send(message, true);
    }

    @Test
    void sendNgTo() {
        TinyMailService.TinyMail message = new TinyMailService.TinyMail();
        message.setSubject("Mail Service Test");
        message.setContextHtml("Nothing");
        // 501 Mail from address must be same as authorization user.
        message.setFrom("admin@qq.com");
        tinyMailService.send(message, true);
    }

    @Test
    void emit() {
    }
}
