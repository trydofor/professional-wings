package pro.fessional.wings.tiny.mail.sender;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.time.StopWatch;
import pro.fessional.wings.testing.silencer.TestingLoggerAssert;
import pro.fessional.wings.tiny.mail.TestingMailUtil;

import java.util.Collections;

/**
 * @author trydofor
 * @since 2022-12-28
 */
@SpringBootTest(properties = {
        "wings.tiny.mail.service.boot-scan=0",
        "logging.level.root=INFO",
})
@Slf4j
public class MailNoticeTest {

    @Setter(onMethod_ = {@Autowired})
    protected MailNotice mailNotice;

    @Setter(onMethod_ = {@Autowired})
    protected MailConfigProvider mailConfigProvider;

    @Setter(onMethod_ = {@Autowired})
    protected MailProperties mailProperties;

    @Setter(onMethod_ = {@Value("${GMAIL_USER:}")})
    protected String gmailUser;

    @Setter(onMethod_ = {@Value("${GMAIL_PASS:}")})
    protected String gmailPass;

    @Test
    @TmsLink("C15001")
    public void postMailNotice() {
        String subject = TestingMailUtil.dryrun("test tiny mail send", mailProperties);
        final boolean snd = mailNotice.post(subject, "test send");
        Assertions.assertTrue(snd, "need env MAIL_USER, MAIL_PASS, current user=" + mailProperties.getUsername());
    }

    @Test
    @TmsLink("C15015")
    public void postMailNoticeDryrun() {
        TestingLoggerAssert al = TestingLoggerAssert.install();
        al.rule("single dryrun", it -> it.getFormattedMessage().contains("single mail dryrun and sleep"));
        al.start();

        mailNotice.post(TestingMailUtil.dryrun("test tiny mail send"), "test send");

        al.assertCount(1);
        al.stop();
        al.uninstall();
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
