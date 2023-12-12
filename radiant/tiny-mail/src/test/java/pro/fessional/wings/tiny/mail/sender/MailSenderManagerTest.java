package pro.fessional.wings.tiny.mail.sender;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.time.StopWatch;

import java.util.ArrayList;
import java.util.List;

/**
 * @author trydofor
 * @since 2023-01-06
 */
@SpringBootTest(properties = {
        "wings.tiny.mail.service.boot-scan=0",
})
@Disabled("Batch send mails, manual")
@Slf4j
public class MailSenderManagerTest {


    @Setter(onMethod_ = {@Autowired})
    protected MailSenderManager mailSenderManager;

    @Setter(onMethod_ = {@Autowired})
    protected MailConfigProvider mailConfigProvider;

    /**
     * <pre>
     * send 10 mails, javax.mail.AuthenticationFailedException: 535 Login Fail. Please enter your authorization code to login
     * 1-single vs batch-5
     * +--s--ms------ns-+---%-+--------+---------------
     * |  5,403,675,738 | 100 | thread | task and timing
     * |  1,450,801,085 |  26 | main   | single
     * |  3,952,874,653 |  73 | main   | batch
     *
     * 5-single vs batch-5
     * +--s--ms------ns-+---%-+--------+---------------
     * | 11,430,259,363 | 100 | thread | task and timing
     * |  7,503,525,394 |  65 | main   | single
     * |  3,926,733,969 |  34 | main   | batch
     * </pre>
     */
    @Test
    @TmsLink("C15004")
    public void testBatch() {
        final TinyMailConfig config = mailConfigProvider.defaultConfig();
        final StopWatch stopWatch = new StopWatch();
        int size = 5;

        try (final StopWatch.Watch ignored = stopWatch.start("single")) {
            for (int i = 0; i < size; i++) {
                TinyMailMessage message = new TinyMailMessage();
                message.adopt(config);
                message.setSubject("test single tiny mail " + i);
                message.setContent("test single tiny mail " + i);
                log.info("single {} send start ====", i);
                mailSenderManager.singleSend(message);
                log.info("single {} send done ====", i);
            }
        }

        List<MailSenderManager.BatchResult> results;
        try (final StopWatch.Watch ignored = stopWatch.start("batch")) {
            List<TinyMailMessage> messages = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                TinyMailMessage message = new TinyMailMessage();
                message.adopt(config);
                message.setSubject("test batch tiny mail " + i);
                message.setContent("test batch tiny mail " + i);
                messages.add(message);
            }
            log.info("batch {} send start ====", size);
            results = mailSenderManager.batchSend(messages);
            log.info("batch {} send done ====", size);
        }

        if (results != null) {
            for (MailSenderManager.BatchResult result : results) {
                final Exception ex = result.getException();
                if (ex != null) {
                    log.warn("error, cost={}, message={}", result.getCostMillis(), result.getTinyMessage().toMainString(), ex);
                }
                else {
                    log.info("success, cost={}, message={}", result.getCostMillis(), result.getTinyMessage().toMainString());
                }
            }
        }

        log.info(stopWatch.toString());
    }

}
