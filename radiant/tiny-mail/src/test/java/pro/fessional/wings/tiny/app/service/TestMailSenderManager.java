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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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

        String subj = message.getSubject();
        if (subj.contains("AlwaysRuntimeException")) {
            throw new RuntimeException("Mock " + subj);
        }

        if (exception1st.add(message.getBizId())) {
            if (subj.contains("MailWaitException")) {
                throw new MailWaitException(ThreadNow.millis() + 5_000, false, false, new IllegalStateException("Mock " + subj));
            }
            if (subj.contains("MailParseException")) {
                throw new MailParseException("Mock " + subj);
            }
            if (subj.contains("RuntimeException")) {
                throw new RuntimeException("Mock " + subj);
            }
        }
    }

    @Override
    public List<BatchResult> batchSend(Collection<? extends TinyMailMessage> messages, long maxWait, @Nullable MimeMessagePrepareHelper preparer) {
        List<BatchResult> results = super.batchSend(messages, maxWait, preparer);

        for (BatchResult result : results) {
            String subj = result.getTinyMessage().getSubject();
            if (subj.contains("AlwaysRuntimeException")) {
                result.setException(new RuntimeException("Mock " + subj));
            }
        }
        return results;
    }
}
