package pro.fessional.wings.tiny.mail;

import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.tiny.mail.notice.MailNotice;

/**
 * @author trydofor
 * @since 2022-12-28
 */
@SpringBootTest(properties = {
        "debug = true",
        "wings.tiny.mail.notice.default.mail-file[application.properties]=classpath:./application.properties"
})
@Disabled
public class MailNoticeTest {

    @Setter(onMethod_ = {@Autowired})
    protected MailNotice mailNotice;

    @Setter(onMethod_ = {@Value("${QQ_MAIL_USER}")})
    protected String mailTo;

    @Test
    public void testDefault() {
        mailNotice.send("test tiny mail", "test");
    }

    @Test
    public void testGmail() {
        final MailNotice.Conf gmail = mailNotice.provideConfig("gmail", true);
        mailNotice.send(gmail, "test tiny mail gmail", "test gmail");
    }
}
