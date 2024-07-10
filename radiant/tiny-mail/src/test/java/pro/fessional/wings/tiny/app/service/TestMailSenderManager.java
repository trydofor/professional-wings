package pro.fessional.wings.tiny.app.service;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.mail.MailParseException;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.tiny.mail.sender.MailSenderManager;
import pro.fessional.wings.tiny.mail.sender.MailSenderProvider;
import pro.fessional.wings.tiny.mail.sender.MailWaitException;
import pro.fessional.wings.tiny.mail.sender.TinyMailMessage;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailSenderProp;

import java.util.HashSet;

/**
 * @author trydofor
 * @since 2024-07-11
 */
@Slf4j
public class TestMailSenderManager extends MailSenderManager {

    private final HashSet<Long> exception1st = new HashSet<>();

    public TestMailSenderManager(TinyMailSenderProp senderProp, MailSenderProvider senderProvider) {
        super(senderProp, senderProvider);
    }

    @Override
    public void singleSend(@NotNull TinyMailMessage message, long maxWait, @Nullable MimeMessagePrepareHelper preparer) {
        super.singleSend(message, maxWait, preparer);

        if (exception1st.add(message.getBizId())) {
            String text = message.getSubject();
            if (text.contains("MailWaitException")) {
                throw new MailWaitException(ThreadNow.millis() + 5_000, false, false, new IllegalStateException("Mock"));
            }
            if (text.contains("MailParseException")) {
                throw new MailParseException("Mock");
            }
            if (text.contains("RuntimeException")) {
                throw new RuntimeException("Mock");
            }
        }
    }
}
