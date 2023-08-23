package pro.fessional.wings.tiny.mail.sender;

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

    @Setter(onMethod_ = {@Value("${QQ_MAIL_USER}")})
    protected String mailUser;

    @Setter(onMethod_ = {@Value("${QQ_MAIL_PASS}")})
    protected String mailPass;

    @Setter(onMethod_ = {@Value("${GMAIL_USER}")})
    protected String gmailUser;

    @Setter(onMethod_ = {@Value("${GMAIL_PASS}")})
    protected String gmailPass;

    @Test
    public void testPost() {
        final boolean snd = mailNotice.post("test tiny mail send", "test send");
        Assertions.assertTrue(snd, "need env QQ_MAIL_USER, QQ_MAIL_PASS, current user=" + mailUser + ", pass=" + mailPass);
    }

    @Test
    @Disabled("Statistics time cost")
    public void testDefault() {
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
    @Disabled("gmail")
    public void testGmail() {
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
