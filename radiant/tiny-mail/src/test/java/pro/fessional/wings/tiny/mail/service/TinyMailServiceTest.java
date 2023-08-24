package pro.fessional.wings.tiny.mail.service;

import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.time.Sleep;
import pro.fessional.wings.slardar.context.Now;

/**
 * @author trydofor
 * @since 2023-01-10
 */
@SpringBootTest(properties = {
        "wings.tiny.mail.service.boot-scan=0",
})
@Disabled("Mail test, manual")
class TinyMailServiceTest {

    @Setter(onMethod_ = {@Autowired})
    protected TinyMailService tinyMailService;

    @Test
    void sendOk() {
        TinyMail message = new TinyMail();
        message.setSubject("Mail Service Test");
        message.setContentHtml("Nothing");
        message.setMark("wings tiny mail");
        tinyMailService.send(message, true);
    }

    @Test
    void sendNxt() {
        TinyMail message = new TinyMail();
        message.setSubject("Mail Service Test");
        message.setContentHtml("Nothing");
        message.setMark("wings tiny mail");
        message.setDate(Now.localDateTime().plusSeconds(60));
        tinyMailService.emit(message, true);
        Sleep.ignoreInterrupt(70_000L);
    }

    // 501 Mail from address must be same as authorization user.
    @Test
    void sendNgFrom() {
        TinyMail message = new TinyMail();
        message.setSubject("Mail Service Test");
        message.setContentHtml("Nothing");
        message.setFrom("admin@qq.com");
        tinyMailService.send(message, true);
    }

    // javax.mail.internet.AddressException: Local address contains dot-dot
    @Test
    void sendBadTo() {
        TinyMail message = new TinyMail();
        message.setSubject("Mail Service Test");
        message.setContentHtml("Nothing");
        message.setTo("t.r.y...d.o...f.o.r@qq.com");
        tinyMailService.send(message, true);
    }
}
