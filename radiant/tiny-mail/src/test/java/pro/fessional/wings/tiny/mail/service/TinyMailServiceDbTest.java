package pro.fessional.wings.tiny.mail.service;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.time.Sleep;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.faceless.convention.EmptySugar;
import pro.fessional.wings.tiny.mail.TestingMailUtil;
import pro.fessional.wings.tiny.mail.database.autogen.tables.daos.WinMailSenderDao;
import pro.fessional.wings.tiny.mail.database.autogen.tables.pojos.WinMailSender;
import pro.fessional.wings.tiny.mail.sender.MailRetryException;
import pro.fessional.wings.tiny.mail.service.impl.TinyMailServiceImpl;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailServiceProp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ScheduledFuture;

/**
 * @author trydofor
 * @since 2023-01-10
 */
@SpringBootTest(properties = {
    "wings.tiny.mail.service.boot-scan=0",
    "wings.tiny.mail.service.scan-idle=0",
    "wings.tiny.mail.service.try-next=3s",
    "wings.tiny.mail.service.batch-size=2",
    "wings.tiny.mail.service.warn-size=5",
    "logging.level.pro.fessional.wings.tiny.mail=debug",
})
@Slf4j
class TinyMailServiceDbTest {

    @Setter(onMethod_ = { @Autowired })
    protected TinyMailServiceImpl tinyMailService;

    @Setter(onMethod_ = { @Autowired })
    protected MailProperties mailProperties;

    @Setter(onMethod_ = { @Autowired })
    protected TinyMailServiceProp tinyMailServiceProp;

    @Setter(onMethod_ = { @Autowired })
    protected WinMailSenderDao winMailSenderDao;

    @Setter(onMethod_ = { @Autowired })
    protected TinyMailLazy tinyMailLazy;

    @Test
    @TmsLink("C15006")
    void mockData() {
        bootScan();
        sendError();
    }

    void bootScan() {
        String subject = TestingMailUtil.dryrun("TinyMailService bootScan", mailProperties);
        TinyMailPlain mail0 = new TinyMailPlain();
        mail0.setSubject(subject);
        mail0.setContent("bootScan");
        long id = tinyMailService.save(mail0);
        Assertions.assertTrue(id > 0);
        // before boot scan
        WinMailSender po0 = winMailSenderDao.fetchOneById(id);
        Assertions.assertTrue(EmptySugar.nonEmptyValue(po0.getNextSend()));
        Assertions.assertTrue(EmptySugar.asEmptyValue(po0.getLastSend()));
        Assertions.assertTrue(EmptySugar.asEmptyValue(po0.getLastDone()));
        Assertions.assertTrue(StringUtils.isBlank(po0.getLastFail()));
        Assertions.assertEquals(0, po0.getSumFail());
        Assertions.assertEquals(0, po0.getSumSend());
        Assertions.assertEquals(0, po0.getSumDone());

        // wait boot scan and send
        tinyMailService.scan(); // scan now
        long ms = 5_000;
        Sleep.ignoreInterrupt(ms);
        log.info("after boot scan, slept={}", ms);
        WinMailSender po1 = winMailSenderDao.fetchOneById(id);
        Assertions.assertTrue(EmptySugar.asEmptyValue(po1.getNextSend()));
        Assertions.assertTrue(EmptySugar.nonEmptyValue(po1.getLastSend()));
        Assertions.assertTrue(EmptySugar.nonEmptyValue(po1.getLastDone()));
        Assertions.assertTrue(StringUtils.isBlank(po0.getLastFail()));
        Assertions.assertEquals(0, po1.getSumFail());
        Assertions.assertEquals(1, po1.getSumSend());
        Assertions.assertEquals(1, po1.getSumDone());
    }

    void sendError() {
        int time = 10;
        String etk = "RuntimeException";
        String atk = "AlwaysRuntimeException";
        Set<Long> ids = new HashSet<>();
        String subj = TestingMailUtil.dryrun("TinyMailService", mailProperties);

        for (int i = 0; i < time; i++) {
            TinyMailPlain ml = new TinyMailPlain();
            boolean err = i % 3 == 0;
            String tkn = err ? " " + etk + " " : " ";

            if (i == 0) {
                ml.setSubject(subj + " " + atk + " " + i);
            }
            else {
                ml.setSubject(subj + tkn + i);
            }

            if (i % 2 == 0) {
                ml.setContent("mail text " + i);
            }
            else {
                ml.setMailLazy(tinyMailLazy, "lazy" + tkn + i);
            }

            long id = tinyMailService.save(ml);
            ids.add(id);

            try {
                tinyMailService.send(id, true, true);
                if (err) Assertions.fail();
            }
            catch (MailRetryException e) {
                Assertions.assertTrue(e.getCause().getMessage().contains("Mock"));
            }

            WinMailSender po = winMailSenderDao.fetchOneById(id);
            if (err) {
                Assertions.assertTrue(po.getNextSend().isAfter(ThreadNow.localDateTime()));
                Assertions.assertTrue(StringUtils.isNotBlank(po.getLastFail()));
                Assertions.assertEquals(1, po.getSumFail());
                Assertions.assertEquals(1, po.getSumSend());
                Assertions.assertEquals(0, po.getSumDone());
            }
            else {
                Assertions.assertTrue(EmptySugar.asEmptyValue(po.getNextSend()));
                Assertions.assertTrue(StringUtils.isBlank(po.getLastFail()));
                Assertions.assertEquals(0, po.getSumFail());
                Assertions.assertEquals(1, po.getSumSend());
                Assertions.assertEquals(1, po.getSumDone());
            }
        }

        long ms = tinyMailServiceProp.getTryNext().toMillis() * 4;
        Sleep.ignoreInterrupt(ms);
        log.info("after try next, slept={}", ms);

        List<WinMailSender> pos = winMailSenderDao.fetchById(ids);
        for (WinMailSender po : pos) {
            Assertions.assertTrue(EmptySugar.asEmptyValue(po.getNextSend()));
            Assertions.assertTrue(EmptySugar.nonEmptyValue(po.getLastSend()));
            int sumFail = 0;

            String sub = po.getMailSubj();
            if (sub.contains(etk) || sub.contains(atk)) {
                sumFail++;
            }
            else if (po.getLazyPara() != null && po.getLazyPara().contains(etk)) {
                sumFail++;
            }

            if (sub.contains(atk)) {
                int maxFail = tinyMailServiceProp.getMaxFail();
                Assertions.assertEquals(maxFail, po.getSumSend());
                Assertions.assertEquals(maxFail, po.getSumFail());
                Assertions.assertEquals(0, po.getSumDone());
                Assertions.assertTrue(StringUtils.isNotBlank(po.getLastFail()));
            }
            else {
                Assertions.assertEquals(sumFail + 1, po.getSumSend());
                Assertions.assertEquals(sumFail, po.getSumFail());
                Assertions.assertEquals(1, po.getSumDone());
                Assertions.assertTrue(StringUtils.isBlank(po.getLastFail()));
            }
        }

        Sleep.ignoreInterrupt(2_000L);
        ArrayList<TinyMailServiceImpl.AsyncMail> queue = tinyMailService.listAsyncMailQueue();
        Assertions.assertTrue(queue.isEmpty());
        TreeMap<Long, ScheduledFuture<?>> sched = tinyMailService.listAsyncMailSched();
        Assertions.assertTrue(sched.isEmpty());
    }
}
