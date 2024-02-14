package pro.fessional.wings.tiny.mail.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.mail.MessagingException;
import lombok.Data;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import pro.fessional.mirana.best.AssertArgs;
import pro.fessional.mirana.cast.BoxedCastUtil;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.pain.ThrowableUtil;
import pro.fessional.mirana.time.DateLocaling;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.faceless.convention.EmptyValue;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.silencer.modulate.RunMode;
import pro.fessional.wings.silencer.modulate.RuntimeMode;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.silencer.spring.help.CommonPropHelper;
import pro.fessional.wings.slardar.jackson.JacksonHelper;
import pro.fessional.wings.tiny.mail.database.autogen.tables.WinMailSenderTable;
import pro.fessional.wings.tiny.mail.database.autogen.tables.daos.WinMailSenderDao;
import pro.fessional.wings.tiny.mail.database.autogen.tables.pojos.WinMailSender;
import pro.fessional.wings.tiny.mail.sender.MailConfigProvider;
import pro.fessional.wings.tiny.mail.sender.MailSenderManager;
import pro.fessional.wings.tiny.mail.sender.MailSenderManager.BatchResult;
import pro.fessional.wings.tiny.mail.sender.MailWaitException;
import pro.fessional.wings.tiny.mail.sender.TinyMailConfig;
import pro.fessional.wings.tiny.mail.sender.TinyMailMessage;
import pro.fessional.wings.tiny.mail.service.TinyMail;
import pro.fessional.wings.tiny.mail.service.TinyMailPlain;
import pro.fessional.wings.tiny.mail.service.TinyMailService;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailServiceProp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor.DEFAULT_TASK_SCHEDULER_BEAN_NAME;
import static pro.fessional.wings.silencer.spring.help.CommonPropHelper.arrayOrNull;
import static pro.fessional.wings.silencer.spring.help.CommonPropHelper.notValue;

/**
 * @author trydofor
 * @since 2023-01-06
 */
@Service
@ConditionalWingsEnabled
@Slf4j
public class TinyMailServiceImpl implements TinyMailService, InitializingBean {

    @Setter(onMethod_ = {@Value("${spring.application.name}")})
    protected String appName;
    @Setter(onMethod_ = {@Autowired})
    protected LightIdService lightIdService;
    @Setter(onMethod_ = {@Autowired})
    protected JournalService journalService;
    @Setter(onMethod_ = {@Autowired})
    protected WinMailSenderDao winMailSenderDao;
    @Setter(onMethod_ = {@Autowired})
    protected MailConfigProvider mailConfigProvider;
    @Setter(onMethod_ = {@Autowired})
    protected MailSenderManager mailSenderManager;
    @Setter(onMethod_ = {@Autowired})
    protected TinyMailServiceProp tinyMailServiceProp;
    @Setter(onMethod_ = {@Autowired})
    protected ResourceLoader resourceLoader;
    @Setter(onMethod_ = {@Autowired(required = false)})
    protected List<StatusHook> statusHooks;

    @Setter(onMethod_ = {@Autowired, @Qualifier(DEFAULT_TASK_SCHEDULER_BEAN_NAME)})
    private ThreadPoolTaskScheduler taskScheduler;

    @SuppressWarnings("all")
    private final PriorityBlockingQueue<AsyncMail> asyncMails = new PriorityBlockingQueue<>();

    @Override
    public boolean send(@NotNull TinyMail message, boolean retry) {
        final String conf = message.getConf();
        final TinyMailConfig config = mailConfigProvider.bynamedConfig(conf);
        AssertArgs.notNull(config, "mail conf={} not found", conf);

        final WinMailSender po = saveMailSender(config, message);
        final TinyMailMessage mailMessage = makeMailMessage(config, po, message);
        return doSyncSend(po, mailMessage, retry, true);
    }

    @Override
    public boolean post(@NotNull TinyMail message, boolean retry) {
        try {
            return send(message, retry);
        }
        catch (Exception e) {
            log.error("fail to post mail, subject=" + message.getSubject(), e);
            return false;
        }
    }

    @Override
    public long emit(@NotNull TinyMail message, boolean retry) {
        final String conf = message.getConf();
        final TinyMailConfig config = mailConfigProvider.bynamedConfig(conf);
        AssertArgs.notNull(config, "mail conf={} not found", conf);
        final WinMailSender po = saveMailSender(config, message);
        final TinyMailMessage mailMessage = makeMailMessage(config, po, message);
        return doAsyncFreshSend(po, mailMessage, retry, true);
    }

    @Override
    public boolean send(long id, boolean retry, boolean check) {
        final WinMailSender po = winMailSenderDao.fetchOneById(id);
        if (po == null) {
            log.warn("mail not found by id={}, skip send", id);
            return false;
        }
        final String conf = po.getMailConf();
        final TinyMailConfig config = mailConfigProvider.bynamedConfig(conf);
        if (config == null) {
            log.warn("mail conf={} not found", conf);
            return false;
        }

        final TinyMailMessage mailMessage = makeMailMessage(config, po, null);
        return doSyncSend(po, mailMessage, retry, check);
    }

    @Override
    public boolean post(long id, boolean retry, boolean check) {
        try {
            return send(id, retry, check);
        }
        catch (Exception e) {
            log.error("fail to post mail, id=" + id, e);
            return false;
        }
    }

    @Override
    public long emit(long id, boolean retry, boolean check) {
        final WinMailSender po = winMailSenderDao.fetchOneById(id);
        if (po == null) {
            log.warn("mail not found by id={}, skip emit", id);
            return -1;
        }
        return doAsyncFreshSend(po, null, retry, check);
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public long save(@NotNull TinyMailPlain msg) {
        final String conf = msg.getConf();
        final TinyMailConfig config = mailConfigProvider.bynamedConfig(conf);
        AssertArgs.notNull(config, "mail conf={} not found", conf);

        final WinMailSender po = new WinMailSender();
        final boolean isNew = msg.getId() == null || msg.getId() <= 0;
        final long id;
        final RunMode rm = RuntimeMode.getRunMode();
        final String crm = rm == RunMode.Nothing ? "" : rm.name().toLowerCase();

        final LocalDateTime md = msg.getDate();
        if (isNew) {
            id = lightIdService.getId(winMailSenderDao.getTable());
            // Optimist Lock
            po.setNextLock(0);
            po.setNextSend(md != null ? md : ThreadNow.localDateTime());
            // Count the result
            po.setSumSend(0);
            po.setSumFail(0);
            po.setSumDone(0);
        }
        else {
            id = msg.getId();
            Null.notNull(msg.getNextSend(), po::setNextSend);
        }

        po.setId(id);
        po.setMailApps(toString(msg.getApps(), appName));
        po.setMailRuns(toString(msg.getRuns(), crm));
        po.setMailConf(msg.getConf());
        po.setMailFrom(toString(msg.getFrom(), config.getFrom()));
        po.setMailTo(toString(msg.getTo(), config.getTo()));
        po.setMailCc(toString(msg.getCc(), config.getCc()));
        po.setMailBcc(toString(msg.getBcc(), config.getBcc()));
        po.setMailReply(toString(msg.getReply(), config.getReply()));
        po.setMailSubj(msg.getSubject());
        po.setMailText(msg.getContent());
        po.setMailHtml(BoxedCastUtil.orElse(msg.getHtml(), config.getHtml()));
        po.setMailFile(toStringMap(msg.getAttachment()));
        po.setMailMark(msg.getMark());
        po.setMailDate(md);

        Null.notNull(msg.getMaxFail(), po::setMaxFail);
        Null.notNull(msg.getMaxDone(), po::setMaxDone);

        Null.notNull(msg.getRefType(), po::setRefType);
        Null.notNull(msg.getRefKey1(), po::setRefKey1);
        Null.notNull(msg.getRefKey2(), po::setRefKey2);

        // try to check message format
        final TinyMailMessage tms = makeMailMessage(config, po, null);
        mailSenderManager.checkMessage(tms);

        journalService.commit(Jane.Insert, journal -> {
            if (isNew) {
                journal.create(po);
                winMailSenderDao.insert(po);
            }
            else {
                journal.modify(po);
                winMailSenderDao.update(po, false);
            }
        });

        return id;
    }

    @Override
    public int scan() {
        final long now = ThreadNow.millis();
        final LocalDateTime min = DateLocaling.sysLdt(now - tinyMailServiceProp.getMaxNext().toMillis());
        final LocalDateTime max = DateLocaling.sysLdt(now + tinyMailServiceProp.getTryNext().toMillis());
        log.info("scan misfire-mail to queue, min={}, max={}", min, max);

        final WinMailSenderTable t = winMailSenderDao.getTable();
        final List<AsyncMail> pos = winMailSenderDao
                .ctx()
                .selectFrom(t)
                .where(t.NextSend.gt(min).and(t.NextSend.lt(max)))
                .fetch()
                .into(WinMailSender.class)
                .stream()
                .filter(po -> !notMatchProp(po))
                .map(it -> new AsyncMail(it.getId(), DateLocaling.sysEpoch(it.getNextSend()), true, true, it, null))
                .toList();

        //
        final int size = pos.size();
        log.info("plan misfire-mail, size={}", size);
        if (size > 0) {
            asyncMails.addAll(pos);
            taskScheduler.schedule(this::doAsyncBatchSend, Instant.ofEpochMilli(now));
        }
        return size;
    }

    @Override
    public void afterPropertiesSet() {
        final long bms = tinyMailServiceProp.getBootScan().toMillis();
        if (bms > 0) {
            taskScheduler.schedule(this::scan, Instant.ofEpochMilli(ThreadNow.millis() + bms));
        }
    }

    private TinyMailMessage makeMailMessage(@NotNull TinyMailConfig config, @NotNull WinMailSender po, @Nullable TinyMail msg) {
        final TinyMailMessage message = new TinyMailMessage();
        message.adopt(config);
        message.setBizId(po.getId());

        if (msg == null) {
            message.setFrom(po.getMailFrom());
            message.setTo(arrayOrNull(po.getMailTo(), true));
            message.setCc(arrayOrNull(po.getMailCc(), true));
            message.setBcc(arrayOrNull(po.getMailBcc(), true));
            message.setReply(toStrOrNull(po.getMailReply()));
            message.setHtml(po.getMailHtml());
            message.setSubject(po.getMailSubj());
            message.setContent(po.getMailText());
            final Map<String, Resource> files = toResource(po.getMailFile());
            if (!files.isEmpty()) {
                message.setAttachment(files);
            }
            message.setBizMark(toStrOrNull(po.getMailMark()));
        }
        else {
            if (msg.getFrom() != null) message.setFrom(msg.getFrom());
            if (msg.getTo() != null) message.setTo(msg.getTo());
            if (msg.getCc() != null) message.setCc(msg.getCc());
            if (msg.getBcc() != null) message.setBcc(msg.getBcc());
            if (msg.getReply() != null) message.setReply(msg.getReply());
            if (msg.getHtml() != null) message.setHtml(msg.getHtml());
            message.setSubject(msg.getSubject());
            message.setContent(msg.getContent());
            message.setAttachment(msg.getAttachment());
            message.setBizMark(msg.getMark());
        }

        return message;
    }

    private WinMailSender saveMailSender(@NotNull TinyMailConfig config, @NotNull TinyMail msg) {
        final WinMailSender po = new WinMailSender();
        final long id = lightIdService.getId(winMailSenderDao.getTable());
        po.setId(id);
        po.setMailApps(appName);
        final RunMode rm = RuntimeMode.getRunMode();
        po.setMailRuns(rm == RunMode.Nothing ? "" : rm.name().toLowerCase());

        po.setMailConf(config.getName());
        po.setMailFrom(toString(msg.getFrom(), config.getFrom()));
        po.setMailTo(toString(msg.getTo(), config.getTo()));
        po.setMailCc(toString(msg.getCc(), config.getCc()));
        po.setMailBcc(toString(msg.getBcc(), config.getBcc()));
        po.setMailReply(toString(msg.getReply(), config.getReply()));
        po.setMailSubj(msg.getSubject());
        po.setMailText(msg.getContent());
        po.setMailHtml(BoxedCastUtil.orElse(msg.getHtml(), config.getHtml()));
        po.setMailFile(toString(msg.getAttachment()));
        po.setMailMark(msg.getMark());

        final LocalDateTime md = msg.getDate();
        po.setMailDate(md);

        po.setMaxFail(BoxedCastUtil.orElse(msg.getMaxFail(), tinyMailServiceProp.getMaxFail()));
        po.setMaxDone(BoxedCastUtil.orElse(msg.getMaxDone(), tinyMailServiceProp.getMaxDone()));

        Null.notNull(msg.getRefType(), po::setRefType);
        Null.notNull(msg.getRefKey1(), po::setRefKey1);
        Null.notNull(msg.getRefKey2(), po::setRefKey2);

        // Optimist lock
        po.setNextLock(0);
        po.setNextSend(md != null ? md : ThreadNow.localDateTime());

        // Count the result
        po.setSumSend(0);
        po.setSumFail(0);
        po.setSumDone(0);

        journalService.commit(Jane.Insert, journal -> {
            journal.create(po);
            winMailSenderDao.insert(po);
        });
        return po;
    }

    private boolean notMatchProp(WinMailSender po) {
        if (tinyMailServiceProp.isOnlyApp()) {
            final String ma = po.getMailApps();
            if (StringUtils.isNotEmpty(ma) && !appName.equalsIgnoreCase(ma)) {
                log.debug("skip only send app-mail app={}, id={}", appName, po.getId());
                return true;
            }
        }
        if (tinyMailServiceProp.isOnlyRun()) {
            final String mrs = po.getMailRuns();
            if (StringUtils.isNotEmpty(mrs)) {
                final RunMode rmd = RuntimeMode.getRunMode();
                if (rmd == RunMode.Nothing) {
                    log.debug("skip only send run-mail, run={}, id={}", mrs, po.getId());
                    return true;
                }
                if (!RuntimeMode.hasRunMode(arrayOrNull(mrs, true))) {
                    log.debug("skip only send run-mail, run={}, cur={}, id={}", mrs, rmd, po.getId());
                    return true;
                }
            }
        }

        final int maxDone = BoxedCastUtil.orElse(po.getMaxDone(), 0) > 0 ? po.getMaxDone() : tinyMailServiceProp.getMaxDone();
        final int sumDone = BoxedCastUtil.orElse(po.getSumDone(), 0);
        if (sumDone >= maxDone) {
            log.debug("skip max-send, max={}, sum={}, id={}", maxDone, sumDone, po.getId());
            return true;
        }

        final int maxFail = BoxedCastUtil.orElse(po.getMaxFail(), 0) > 0 ? po.getMaxFail() : tinyMailServiceProp.getMaxFail();
        final int sumFail = BoxedCastUtil.orElse(po.getSumFail(), 0);
        if (sumFail >= maxFail) {
            log.debug("skip max-fail, max={}, sum={}, id={}", maxFail, sumFail, po.getId());
            return true;
        }

        return false;
    }

    private boolean notNextLock(WinMailSender po, long now) {
        final WinMailSenderTable t = winMailSenderDao.getTable();
        final int rc = winMailSenderDao
                .ctx()
                .update(t)
                .set(t.NextLock, t.NextLock.add(1))
                .set(t.LastSend, DateLocaling.sysLdt(now))
                .where(t.Id.eq(po.getId()).and(t.NextLock.eq(po.getNextLock())))
                .execute();

        if (rc <= 0) {
            log.debug("skip not-next-lock mail, id={}", po.getId());
            return true;
        }

        return false;
    }

    private void saveStatusAndRetry(@NotNull WinMailSender po, TinyMailMessage message, long cost, long now, Exception exception, boolean retry, boolean check, boolean rethrow) {
        long nextSend = -1;
        boolean notHookStop = true;
        try {
            final WinMailSenderTable t = winMailSenderDao.getTable();
            final Map<Object, Object> setter = new HashMap<>();
            if (exception == null) {
                setter.put(t.LastFail, null);
                setter.put(t.LastDone, DateLocaling.sysLdt(now));
                setter.put(t.LastCost, cost);

                if (po.getSumDone() + 1 >= tinyMailServiceProp.getMaxDone()) {
                    setter.put(t.NextSend, EmptyValue.DATE_TIME);
                    log.debug("done mail by max-send id={}, subject={}", po.getId(), po.getMailSubj());
                }
                else {
                    nextSend = now + tinyMailServiceProp.getTryNext().toMillis();
                    setter.put(t.NextSend, DateLocaling.sysLdt(nextSend));
                    log.debug("next done-mail id={}, subject={}", po.getId(), po.getMailSubj());
                }

                setter.put(t.SumSend, t.SumSend.add(1));
                setter.put(t.SumDone, t.SumDone.add(1));
            }
            else {
                setter.put(t.LastFail, ThrowableUtil.toString(exception));
                setter.put(t.LastDone, EmptyValue.DATE_TIME);
                setter.put(t.LastCost, cost);

                final int maxFail = BoxedCastUtil.orElse(po.getMaxFail(), 0) > 0 ? po.getMaxFail() : tinyMailServiceProp.getMaxFail();
                if (po.getSumFail() + 1 >= maxFail) {
                    setter.put(t.NextSend, EmptyValue.DATE_TIME);
                    log.debug("done mail by max-fail id={}, subject={}", po.getId(), po.getMailSubj());
                }
                else if (retry) {
                    if (exception instanceof MailWaitException mwe) {
                        if (mwe.isStopRetry()) {
                            setter.put(t.NextSend, EmptyValue.DATE_TIME);
                            log.error("stop stop-retry mail, id=" + po.getId(), exception);
                        }
                        else {
                            nextSend = mwe.getWaitEpoch();
                        }
                    }
                    else if (exception instanceof MailParseException || exception instanceof MessagingException) {
                        setter.put(t.NextSend, EmptyValue.DATE_TIME);
                        log.error("failed to parse, stop mail, id=" + po.getId(), exception);
                    }
                    else {
                        nextSend = now + tinyMailServiceProp.getTryNext().toMillis();
                    }

                    if (nextSend > 0) {
                        setter.put(t.NextSend, DateLocaling.sysLdt(nextSend));
                        log.debug("next fail-mail id={}, subject={}", po.getId(), po.getMailSubj());
                    }
                }
                else {
                    setter.put(t.NextSend, EmptyValue.DATE_TIME);
                    log.error("stop not-retry mail, id=" + po.getId(), exception);
                }
                setter.put(t.SumSend, t.SumSend.add(1));
                setter.put(t.SumFail, t.SumFail.add(1));
            }

            if (statusHooks != null) {
                for (StatusHook sh : statusHooks) {
                    try {
                        if (sh.stop(po, cost, exception)) {
                            notHookStop = false;
                        }
                    }
                    catch (Exception e) {
                        log.error("should NOT throw in hook, hook-class=" + sh.getClass().getName(), e);
                    }
                }
            }

            if (!notHookStop) {
                setter.put(t.NextSend, EmptyValue.DATE_TIME);
                log.debug("hook stop mail, id={}", po.getId());
            }

            journalService.commit(Jane.Update, journal -> {
                setter.put(t.CommitId, journal.getCommitId());
                setter.put(t.ModifyDt, journal.getCommitDt());
                winMailSenderDao.ctx()
                                .update(t)
                                .set(setter)
                                .where(t.Id.eq(po.getId()))
                                .execute();
            });
        }
        catch (Exception e) {
            log.error("failed to save mail status, id=" + po.getId() + ", subject=" + po.getMailSubj(), e);
            nextSend = now + tinyMailServiceProp.getTryNext().toMillis();
        }

        if (exception == null) {
            if (notHookStop && nextSend > 0) {
                asyncMails.add(new AsyncMail(po.getId(), nextSend, retry, check, null, message));
                taskScheduler.schedule(this::doAsyncBatchSend, Instant.ofEpochMilli(nextSend));
                log.debug("schedule done-mail send, id={}, subject={}", po.getId(), po.getMailSubj());
            }
        }
        else {
            if (notHookStop && retry && nextSend > 0 && nextSend - now < tinyMailServiceProp.getMaxNext().toMillis()) {
                asyncMails.add(new AsyncMail(po.getId(), nextSend, retry, check, null, message));
                taskScheduler.schedule(this::doAsyncBatchSend, Instant.ofEpochMilli(nextSend));
                log.debug("schedule fail-mail send, id=" + po.getId() + ", subject=" + po.getMailSubj());
            }
            else {
                if (rethrow) {
                    if (exception instanceof RuntimeException) {
                        throw (RuntimeException) exception;
                    }
                    else {
                        throw new MailSendException("failed mail, id=" + po.getId() + ", subject=" + po.getMailSubj(), exception);
                    }
                }
                else {
                    log.debug("no rethrow or retry mail, id=" + po.getId() + ", subject=" + po.getMailSubj());
                }
            }
        }
    }

    private boolean doSyncSend(@NotNull WinMailSender po, TinyMailMessage mailMessage, boolean retry, boolean check) {
        final long start;
        if (check) {
            if (notMatchProp(po)) {
                return false;
            }

            start = ThreadNow.millis();
            if (notNextLock(po, start)) {
                return false;
            }
        }
        else {
            start = ThreadNow.millis();
        }

        Exception exception = null;
        try {
            mailSenderManager.singleSend(mailMessage);
        }
        catch (Exception e) {
            exception = e;
        }
        finally {
            final long now = ThreadNow.millis();
            saveStatusAndRetry(po, mailMessage, now - start, now, exception, retry, check, true);
        }

        return exception == null;
    }

    private long doAsyncFreshSend(@NotNull WinMailSender po, TinyMailMessage message, boolean retry, boolean check) {
        if (check && notMatchProp(po)) return -1;

        final LocalDateTime md = po.getNextSend();
        final long mds = md == null ? 0 : DateLocaling.sysEpoch(md);
        final long now = ThreadNow.millis();
        final Long id = po.getId();
        final long nxt;
        if (mds > now) {
            nxt = mds;
            log.debug("plan async date={} id={}", md, id);
        }
        else {
            nxt = now;
            log.debug("plan async date=now id={}", id);
        }

        // check format
        mailSenderManager.checkMessage(message);

        asyncMails.add(new AsyncMail(id, nxt, retry, check, po, message));
        taskScheduler.schedule(this::doAsyncBatchSend, Instant.ofEpochMilli(nxt));
        return nxt;
    }

    private void doAsyncBatchSend() {
        final long start = ThreadNow.millis();
        final int bz = tinyMailServiceProp.getBatchSize();

        try {
            final AtomicInteger count = new AtomicInteger(bz > 0 ? bz : 1);
            final HashMap<Long, AsyncMail> mails = new HashMap<>(count.get());
            asyncMails.removeIf(it -> {
                if (count.get() <= 0) return false;
                if (it.next > start) {
                    return false;
                }
                else {
                    mails.put(it.id, it);
                    count.decrementAndGet();
                    return true;
                }
            });

            if (mails.isEmpty()) return;

            final Map<Long, WinMailSender> freshPo = new HashMap<>();
            final List<Long> dirtyIds = new ArrayList<>();
            for (AsyncMail am : mails.values()) {
                if (am.fresher == null) {
                    dirtyIds.add(am.id);
                }
                else {
                    freshPo.put(am.id, am.fresher);
                }
            }

            if (!dirtyIds.isEmpty()) {
                final WinMailSenderTable t = winMailSenderDao.getTable();
                winMailSenderDao
                        .ctx()
                        .selectFrom(t)
                        .where(t.Id.in(dirtyIds))
                        .fetchInto(WinMailSender.class)
                        .forEach(po -> freshPo.put(po.getId(), po));
            }

            final List<TinyMailMessage> messages = new ArrayList<>(freshPo.size());
            for (WinMailSender po : freshPo.values()) {
                final AsyncMail am = mails.get(po.getId());

                if (am != null && am.check && (notMatchProp(po) || notNextLock(po, start))) {
                    continue;
                }

                if (am != null && am.message != null) {
                    messages.add(am.message);
                }
                else {
                    final String conf = po.getMailConf();
                    final TinyMailConfig config = mailConfigProvider.bynamedConfig(conf);
                    if (config == null) {
                        log.warn("mail conf={} not found", conf);
                    }
                    else {
                        messages.add(makeMailMessage(config, po, null));
                    }
                }
            }

            final List<BatchResult> results = mailSenderManager.batchSend(messages);
            for (BatchResult result : results) {
                final TinyMailMessage msg = result.getTinyMessage();
                final WinMailSender po = freshPo.get(msg.getBizId()); // must not null
                final AsyncMail am = mails.get(po.getId());
                saveStatusAndRetry(po, msg, result.getCostMillis(), result.getDoneMillis(), result.getException(), am != null && am.retry, am != null && am.check, false);
            }
        }
        finally {
            final int size = asyncMails.size();
            if (size > 0) {
                final long next = start + tinyMailServiceProp.getTryNext().toMillis();
                taskScheduler.schedule(this::doAsyncBatchSend, Instant.ofEpochMilli(next));
                if (size > tinyMailServiceProp.getWarnSize()) {
                    log.warn("plan next war-size={}, idle={}", size, tinyMailServiceProp.getTryNext());
                }
                else {
                    log.debug("plan next size={}, idle={}", size, tinyMailServiceProp.getTryNext());
                }
            }
        }
    }

    @Nullable
    private String toString(String[] arr, String[] elz) {
        if (arr == null) arr = elz;
        return arr == null ? null : String.join(",", arr);
    }

    @Nullable
    private String toString(String str, String[] elz) {
        return notValue(str)
               ? (elz == null || elz.length == 0)
                 ? null
                 : String.join(",", elz)
               : str;
    }

    @Nullable
    private String toString(String str, String elz) {
        return notValue(str) ? elz : str;
    }

    @SneakyThrows
    @NotNull
    private String toString(Map<String, Resource> file) {
        if (file == null || file.isEmpty()) return Null.Str;
        Map<String, String> nameUrl = new LinkedHashMap<>(file.size());
        for (Map.Entry<String, Resource> en : file.entrySet()) {
            nameUrl.put(en.getKey(), CommonPropHelper.toString(en.getValue()));
        }
        return JacksonHelper.string(nameUrl, true);
    }

    @SneakyThrows
    @NotNull
    private String toStringMap(Map<String, String> file) {
        if (file == null || file.isEmpty()) return Null.Str;
        return JacksonHelper.string(file, true);
    }

    @Nullable
    private String toStrOrNull(String str) {
        return (str == null || str.isEmpty()) ? null : str;
    }

    @NotNull
    private Map<String, Resource> toResource(String map) {
        if (map == null || map.isEmpty()) return Collections.emptyMap();
        final Map<String, Resource> rst = new LinkedHashMap<>();
        final Iterator<Map.Entry<String, JsonNode>> node = JacksonHelper.object(map).fields();
        while (node.hasNext()) {
            final Map.Entry<String, JsonNode> en = node.next();
            rst.put(en.getKey(), resourceLoader.getResource(en.getValue().asText()));
        }
        return rst;
    }

    @Data
    private static class AsyncMail implements Comparable<AsyncMail> {
        private final long id;
        private final long next;
        private final boolean retry;
        private final boolean check;
        @Nullable
        private final WinMailSender fresher;
        @Nullable
        private final TinyMailMessage message;

        @Override
        public int compareTo(@NotNull TinyMailServiceImpl.AsyncMail o) {
            return Long.compare(next, o.next);
        }
    }

    public enum Jane {
        Insert,
        Update
    }
}
