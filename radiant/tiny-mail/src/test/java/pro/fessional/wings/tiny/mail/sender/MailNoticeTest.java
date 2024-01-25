package pro.fessional.wings.tiny.mail.sender;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.time.StopWatch;

import java.util.Collections;

/**
 * @author trydofor
 * @since 2022-12-28
 */
@SpringBootTest(properties = {
        "wings.tiny.mail.service.boot-scan=0",
})
@Slf4j
public class MailNoticeTest {

    @Setter(onMethod_ = {@Autowired})
    protected MailNotice mailNotice;

    @Setter(onMethod_ = {@Autowired})
    protected MailConfigProvider mailConfigProvider;

    @Setter(onMethod_ = {@Value("${spring.mail.username:}")})
    protected String mailUser;

    @Setter(onMethod_ = {@Value("${spring.mail.password:}")})
    protected String mailPass;

    @Setter(onMethod_ = {@Value("${GMAIL_USER:}")})
    protected String gmailUser;

    @Setter(onMethod_ = {@Value("${GMAIL_PASS:}")})
    protected String gmailPass;

    @Test
    @TmsLink("C15001")
    public void postMailNotice() {
        final boolean snd = mailNotice.post("test tiny mail send", "test send");
        Assertions.assertTrue(snd, "need env MAIL_USER, MAIL_PASS, current user=" + mailUser);
    }

    @Test
    @Disabled("Statis: time cost")
    @TmsLink("C15002")
    public void timeEmitPostSend() {
        final StopWatch stopWatch = new StopWatch();
        try (final StopWatch.Watch ignored = stopWatch.start("emit")) {
            mailNotice.emit("test tiny mail emit", "test emit");
        }
        try (final StopWatch.Watch ignored = stopWatch.start("post")) {
            mailNotice.post("test tiny mail post", "test post");
        }
        try (final StopWatch.Watch ignored = stopWatch.start("send")) {
            mailNotice.send("test tiny mail send", "test send");
        }
        log.info(stopWatch.toString());
    }

    @Test
    @Disabled("3rdService: gmail")
    @TmsLink("C15003")
    public void sendGmail() {
        // dynamic config
        final String name = "gmailx";
        TinyMailConfig conf = new TinyMailConfig();
        conf.setName(name);
        conf.setHost("smtp.gmail.com");
        conf.setPort(587);
        conf.setUsername(gmailUser);
        conf.setPassword(gmailPass);
        conf.setTo(gmailUser);

        conf.getProperties().put("mail.smtp.auth", "true");
        conf.getProperties().put("mail.smtp.starttls.enable", "true");
        conf.getProperties().put("mail.smtp.socks.host", "127.0.0.1");
        conf.getProperties().put("mail.smtp.socks.port", "1081");

        final TinyMailConfig.Loader loader = n -> name.equals(n) ? conf : null;
        mailConfigProvider.setConfigLoader(Collections.singletonList(loader));
        final TinyMailConfig gmail = mailNotice.provideConfig(name, true);
        mailNotice.send(gmail, "test tiny mail gmail", "test gmail");
    }
}
