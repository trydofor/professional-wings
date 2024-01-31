package pro.fessional.wings.tiny.mail.service;

import io.qameta.allure.TmsLink;
import jakarta.mail.internet.AddressException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.pain.ThrowableUtil;
import pro.fessional.mirana.time.Sleep;
import pro.fessional.wings.slardar.context.Now;
import pro.fessional.wings.tiny.mail.TestingMailUtil;

/**
 * @author trydofor
 * @since 2023-01-10
 */
@SpringBootTest(properties = {
        "wings.tiny.mail.service.boot-scan=0",
})
@Slf4j
class TinyMailServiceTest {

    @Setter(onMethod_ = {@Autowired})
    protected TinyMailService tinyMailService;

    @Setter(onMethod_ = {@Autowired})
    protected MailProperties mailProperties;

    @Test
    @TmsLink("C15006")
    void sendMailOk() {
        TinyMail message = new TinyMail();
        String subject = TestingMailUtil.dryrun("Mail Service Send Test", mailProperties);
        message.setSubject(subject);
        message.setContentHtml("Nothing");
        message.setMark("wings tiny mail");
        boolean ok = tinyMailService.send(message, true);
        Assertions.assertTrue(ok);
    }

    @Test
    @TmsLink("C15007")
    void emitMailAfter5s() {
        TinyMail message = new TinyMail();
        String subject = TestingMailUtil.dryrun("Mail Service Emit Test", mailProperties);
        message.setSubject(subject);
        message.setContentHtml("Nothing");
        message.setMark("wings tiny mail");

        int after = 5;
        message.setDate(Now.localDateTime().plusSeconds(after));
        long at = System.currentTimeMillis() + after * 1_000L;
        long nt = tinyMailService.emit(message, true);
        long of = Math.abs(nt - at);
        Assertions.assertTrue(of < 500L);
        Sleep.ignoreInterrupt(after * 1_500L);
    }

    /**
     * 501 Mail from address must be same as authorization user.
     */
    @Test
    @TmsLink("C15008")
    @Disabled("not for mock server, need real server auth")
    void sendMailBadFrom() {
        try {
            TinyMail message = new TinyMail();
            message.setSubject("Mail Service Test");
            message.setContentHtml("Nothing");
            message.setFrom("admin@qq.com"); // diff from mail user
            tinyMailService.send(message, true);
        }
        catch (Exception e) {
            log.error("501 Mail from address must be same as authorization user", e);
        }
    }

    /**
     * javax.mail.internet.AddressException: Local address contains dot-dot
     */
    @Test
    @TmsLink("C15009")
    void sendMailBadTo() {
        try {
            TinyMail message = new TinyMail();
            message.setSubject("Mail Service Test");
            message.setContentHtml("Nothing");
            message.setTo("t.r.y...d.o...f.o.r@qq.com");
            tinyMailService.send(message, true);
            Assertions.fail();
        }
        catch (Exception e) {
            var rt = ThrowableUtil.root(e);
            Assertions.assertInstanceOf(AddressException.class, rt);
        }
    }
}
