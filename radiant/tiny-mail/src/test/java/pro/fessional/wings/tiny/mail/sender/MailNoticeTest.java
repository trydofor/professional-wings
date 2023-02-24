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

/**
 * @author trydofor
 * @since 2022-12-28
 */
@SpringBootTest(properties = {
        "debug = true",
        "wings.tiny.mail.service.boot-scan=0",
//        "wings.tiny.mail.config.gmail.host=smtp.gmail.com",
        "wings.tiny.mail.config.gmail.username=${GMAIL_USER:}",
        "wings.tiny.mail.config.gmail.password=${GMAIL_PASS:}",
//        "wings.tiny.mail.config.gmail.port=587",
//        "wings.tiny.mail.notice.default.file[application.properties]=classpath:./application.properties"
})
@Slf4j
public class MailNoticeTest {

    @Setter(onMethod_ = {@Autowired})
    protected MailNotice mailNotice;

    @Setter(onMethod_ = {@Autowired})
    protected MailConfigProvider mailConfigProvider;

    @Setter(onMethod_ = {@Value("${QQ_MAIL_USER}")})
    protected String mailTo;

    @Setter(onMethod_ = {@Value("${QQ_MAIL_PASS}")})
    protected String mailPass;

    @Test
    public void testPost() {
        final boolean snd = mailNotice.post("test tiny mail send", "test send");
        Assertions.assertTrue(snd, "need env QQ_MAIL_USER, QQ_MAIL_PASS, current user=" + mailTo + ", pass=" + mailPass);
    }

    @Test
    @Disabled("统计耗时")
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
        final String name = "gmail";
        TinyMailConfig conf = new TinyMailConfig();
        conf.setName(name);
        conf.setHost("smtp.gmail.com");
        conf.setPort(587);
        mailConfigProvider.putMailConfig(conf);

        final TinyMailConfig gmail = mailNotice.provideConfig(name, true);
        mailNotice.send(gmail, "test tiny mail gmail", "test gmail");
    }
}
