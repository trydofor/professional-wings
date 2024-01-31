package pro.fessional.wings.tiny.mail.sender;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.time.StopWatch;
import pro.fessional.wings.testing.silencer.TestingLoggerAssert;
import pro.fessional.wings.tiny.mail.TestingMailUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author trydofor
 * @since 2023-01-06
 */
@SpringBootTest(properties = {
        "wings.tiny.mail.service.boot-scan=0",
        "logging.level.root=INFO",
})
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
    public void timeLoopAndBatch() {

        final TinyMailConfig config = mailConfigProvider.defaultConfig();
        final boolean dryrun = TestingMailUtil.isDryrun(config);
        // Too many emails per second. Please upgrade your plan
        final int size = dryrun ? 1 : 5;


        final StopWatch stopWatch = new StopWatch();
        try (final StopWatch.Watch ignored = stopWatch.start("single")) {
            for (int i = 0; i < size; i++) {
                TinyMailMessage message = new TinyMailMessage();
                message.adopt(config);
                String text = "test single tiny mail " + i;
                message.setSubject(TestingMailUtil.dryrun(text, dryrun));
                message.setContent(text);
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
                String text = "test batch tiny mail " + i;
                message.setSubject(TestingMailUtil.dryrun(text, dryrun));
                message.setContent(text);
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

    @Test
    @TmsLink("C15016")
    public void batchMailDryrun() {
        final TinyMailConfig config = mailConfigProvider.defaultConfig();
        List<TinyMailMessage> messages = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            TinyMailMessage message = new TinyMailMessage();
            message.adopt(config);
            message.setSubject(TestingMailUtil.dryrun("test batch tiny mail " + i));
            message.setContent("test batch tiny mail " + i);
            messages.add(message);
        }
        TestingLoggerAssert al = TestingLoggerAssert.install();
        al.rule("batch dryrun", it -> it.getFormattedMessage().contains("batch mail dryrun and sleep"));
        al.start();

        mailSenderManager.batchSend(messages);

        al.assertCount(1);
        al.stop();
        al.uninstall();
    }
}
