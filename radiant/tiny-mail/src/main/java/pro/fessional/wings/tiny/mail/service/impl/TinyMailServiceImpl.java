package pro.fessional.wings.tiny.mail.service.impl;

import jakarta.mail.MessagingException;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties;
import org.springframework.boot.task.ThreadPoolTaskSchedulerBuilder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import pro.fessional.mirana.best.AssertArgs;
import pro.fessional.mirana.cast.BoxedCastUtil;
import pro.fessional.mirana.cond.IfSetter;
import pro.fessional.mirana.pain.ThrowableUtil;
import pro.fessional.mirana.time.DateLocaling;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.faceless.convention.EmptySugar;
import pro.fessional.wings.faceless.convention.EmptyValue;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.silencer.modulate.RunMode;
import pro.fessional.wings.silencer.modulate.RuntimeMode;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.silencer.support.PropHelper;
import pro.fessional.wings.slardar.async.TaskSchedulerHelper;
import pro.fessional.wings.slardar.fastjson.FastJsonHelper;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static pro.fessional.wings.silencer.support.PropHelper.commaArray;
import static pro.fessional.wings.silencer.support.PropHelper.invalid;

/**
 * @author trydofor
 * @since 2023-01-06
 */
@Service
@ConditionalWingsEnabled
@Slf4j
public class TinyMailServiceImpl implements TinyMailService, InitializingBean {

    @Setter(onMethod_ = { @Value("${spring.application.name}") })
    protected String appName;
    @Setter(onMethod_ = { @Autowired })
    protected LightIdService lightIdService;
    @Setter(onMethod_ = { @Autowired })
    protected JournalService journalService;
    @Setter(onMethod_ = { @Autowired })
    protected WinMailSenderDao winMailSenderDao;
    @Setter(onMethod_ = { @Autowired })
    protected MailConfigProvider mailConfigProvider;
    @Setter(onMethod_ = { @Autowired })
    protected MailSenderManager mailSenderManager;
    @Setter(onMethod_ = { @Autowired })
    protected TinyMailServiceProp tinyMailServiceProp;
    @Setter(onMethod_ = { @Autowired })
    protected ResourceLoader resourceLoader;
    @Setter(onMethod_ = { @Autowired(required = false) })
    protected List<StatusHook> statusHooks;

    // init by afterPropertiesSet
    protected ThreadPoolTaskScheduler taskScheduler;

    private final PriorityQueue<AsyncMail> asyncMailQueue = new PriorityQueue<>();
    private final TreeMap<Long, ScheduledFuture<?>> asyncMailSched = new TreeMap<>();

    @Override
    public boolean send(@NotNull TinyMail message, boolean retry) {
        final String conf = message.getConf();
        final TinyMailConfig config = mailConfigProvider.bynamedConfig(conf);
        AssertArgs.notNull(config, "tiny-mail conf={} not found", conf);

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
            log.error("fail to post tiny-mail, subject=" + message.getSubject(), e);
            return false;
        }
    }

    @Override
    public long emit(@NotNull TinyMail message, boolean retry) {
        final String conf = message.getConf();
        final TinyMailConfig config = mailConfigProvider.bynamedConfig(conf);
        AssertArgs.notNull(config, "tiny-mail conf={} not found", conf);

        final WinMailSender po = saveMailSender(config, message);
        final TinyMailMessage mailMessage = makeMailMessage(config, po, message);
        return doAsyncSend(po, mailMessage, retry, true);
    }

    @Override
    public boolean send(long id, boolean retry, boolean check) {
        final WinMailSender po = winMailSenderDao.fetchOneById(id);
        if (po == null) {
            log.warn("tiny-mail not found by id={}, skip send", id);
            return false;
        }
        final String conf = po.getMailConf();
        final TinyMailConfig config = mailConfigProvider.bynamedConfig(conf);
        if (config == null) {
            log.warn("tiny-mail conf={} not found", conf);
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
            log.error("fail to post tiny-mail, id=" + id, e);
            return false;
        }
    }

    @Override
    public long emit(long id, boolean retry, boolean check) {
        final WinMailSender po = winMailSenderDao.fetchOneById(id);
        if (po == null) {
            log.warn("tiny-mail not found by id={}, skip emit", id);
            return -1;
        }
        return doAsyncSend(po, null, retry, check);
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public long save(@NotNull TinyMailPlain msg) {
        final String conf = msg.getConf();
        final TinyMailConfig config = mailConfigProvider.bynamedConfig(conf);
        AssertArgs.notNull(config, "tiny-mail conf={} not found", conf);

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
            IfSetter.nonnull(po::setNextSend, msg.getNextSend());
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
        po.setMailFile(toString(msg.getAttachment()));
        po.setMailMark(msg.getMark());
        po.setMailDate(md);

        // PropertyMapper
        IfSetter.nonnull(po::setMaxFail, msg.getMaxFail());
        IfSetter.nonnull(po::setMaxDone, msg.getMaxDone());

        IfSetter.nonnull(po::setRefType, msg.getRefType());
        IfSetter.nonnull(po::setRefKey1, msg.getRefKey1());
        IfSetter.nonnull(po::setRefKey2, msg.getRefKey2());

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
    public int scan(Long idle) {
        if (idle == null) return scanSync();

        idleScanMills.set(idle);
        final ScheduledFuture<?> task = idleScanTask.get();
        if (task != null) task.cancel(false);

        return scanIdle();
    }

    @NotNull
    public ArrayList<AsyncMail> listAsyncMailQueue() {
        synchronized (asyncMailQueue) {
            return new ArrayList<>(asyncMailQueue);
        }
    }

    @NotNull
    public TreeMap<Long, ScheduledFuture<?>> listAsyncMailSched() {
        synchronized (asyncMailSched) {
            return new TreeMap(asyncMailSched);
        }
    }

    protected final AtomicLong idleScanMills = new AtomicLong(-1);
    protected final AtomicReference<ScheduledFuture<?>> idleScanTask = new AtomicReference<>();

    protected int scanIdle() {
        int size = -1;
        try {
            size = scanSync();
        }
        catch (Exception e) {
            log.error("fail to scanSync", e);
        }

        long idle = idleScanMills.get();

        if (idle < 0) { // reset to prop
            idle = tinyMailServiceProp.getScanIdle().toMillis();
            idleScanMills.set(idle);
            log.info("schedule reset tiny-mail scan, idle={} ms", idle);
        }

        if (idle > 0) {
            var task = taskScheduler.schedule(this::scanIdle, Instant.ofEpochMilli(ThreadNow.millis() + idle));
            idleScanTask.set(task);
            log.info("schedule next tiny-mail scan, idle={} ms", idle);
        }
        else {
            idleScanTask.set(null);
            log.info("schedule stop tiny-mail scan, idle={} ms", idle);
        }

        return size;
    }

    protected int scanSync() {
        final long now = ThreadNow.millis();
        final LocalDateTime min = DateLocaling.sysLdt(now - tinyMailServiceProp.getMaxNext().toMillis());
        final LocalDateTime max = DateLocaling.sysLdt(now + tinyMailServiceProp.getTryNext().toMillis());
        log.info("scan tiny-mail to queue, min={}, max={}", min, max);

        final WinMailSenderTable t = winMailSenderDao.getTable();
        final List<AsyncMail> mails = winMailSenderDao
            .ctx()
            .selectFrom(t)
            .where(t.NextSend.ge(min).and(t.NextSend.lt(max)))
            .fetch()
            .into(WinMailSender.class)
            .stream()
            .filter(po -> !notMatchProp(po))
            .map(it -> new AsyncMail(it.getId(), DateLocaling.sysEpoch(it.getNextSend()), true, true, it, null))
            .toList();

        //
        final int size = mails.size();
        if (size > 0) {
            planAsyncMail(mails);
        }
        return size;
    }

    @Override
    public void afterPropertiesSet() {
        ThreadPoolTaskSchedulerBuilder builder = new ThreadPoolTaskSchedulerBuilder();
        TaskSchedulingProperties scheduler = tinyMailServiceProp.getScheduler();
        builder = builder.poolSize(scheduler.getPool().getSize());
        builder = builder.threadNamePrefix(scheduler.getThreadNamePrefix());
        TaskSchedulingProperties.Shutdown shutdown = scheduler.getShutdown();
        builder = builder.awaitTermination(shutdown.isAwaitTermination());
        builder = builder.awaitTerminationPeriod(shutdown.getAwaitTerminationPeriod());

        taskScheduler = TaskSchedulerHelper.Ttl(builder);
        taskScheduler.initialize();
        log.info("tiny-mail mailScheduler, prefix=" + taskScheduler.getThreadNamePrefix());

        final long idle = tinyMailServiceProp.getBootScan().toMillis();
        if (idle > 0) {
            log.info("tiny-mail schedule boot-scan after={} ms", idle);
            taskScheduler.schedule(this::scanIdle, Instant.ofEpochMilli(ThreadNow.millis() + idle));
        }
    }

    protected TinyMailMessage makeMailMessage(@NotNull TinyMailConfig config, @NotNull WinMailSender po, @Nullable TinyMail msg) {
        final TinyMailMessage message = new TinyMailMessage();
        TinyMailConfig.ConfSetter.toAny(message, config);
        message.setBizId(po.getId());

        if (msg == null) {
            message.setFrom(po.getMailFrom());
            message.setTo(commaArray(po.getMailTo()));
            message.setCc(commaArray(po.getMailCc()));
            message.setBcc(commaArray(po.getMailBcc()));
            message.setReply(EmptySugar.emptyToNull(po.getMailReply()));
            message.setHtml(po.getMailHtml());
            message.setSubject(po.getMailSubj());
            message.setContent(po.getMailText());
            final Map<String, Resource> files = resourceString(po.getMailFile());
            if (!files.isEmpty()) {
                message.setAttachment(files);
            }
            message.setBizMark(EmptySugar.emptyToNull(po.getMailMark()));
        }
        else {
            IfSetter.nonnull(message::setFrom, msg.getFrom());
            IfSetter.nonnull(message::setTo, msg.getTo());
            IfSetter.nonnull(message::setCc, msg.getCc());
            IfSetter.nonnull(message::setReply, msg.getReply());
            IfSetter.nonnull(message::setHtml, msg.getHtml());
            message.setSubject(msg.getSubject());
            message.setContent(msg.getContent());
            message.setAttachment(msg.getAttachment());
            message.setBizMark(msg.getMark());
        }

        return message;
    }

    protected WinMailSender saveMailSender(@NotNull TinyMailConfig config, @NotNull TinyMail msg) {
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
        po.setMailFile(stringResource(msg.getAttachment()));
        po.setMailMark(msg.getMark());

        final LocalDateTime md = msg.getDate();
        po.setMailDate(md);

        po.setMaxFail(BoxedCastUtil.orElse(msg.getMaxFail(), tinyMailServiceProp.getMaxFail()));
        po.setMaxDone(BoxedCastUtil.orElse(msg.getMaxDone(), tinyMailServiceProp.getMaxDone()));

        IfSetter.nonnull(po::setRefType, msg.getRefType());
        IfSetter.nonnull(po::setRefKey1, msg.getRefKey1());
        IfSetter.nonnull(po::setRefKey2, msg.getRefKey2());

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

    /**
     * should skip sending, condition not match
     */
    protected boolean notMatchProp(@NotNull WinMailSender po) {
        if (tinyMailServiceProp.isOnlyApp()) {
            final String ma = po.getMailApps();
            if (StringUtils.isNotEmpty(ma) && !appName.equalsIgnoreCase(ma)) {
                log.debug("skip only send app tiny-mail app={}, id={}", appName, po.getId());
                return true;
            }
        }
        if (tinyMailServiceProp.isOnlyRun()) {
            final String mrs = po.getMailRuns();
            if (StringUtils.isNotEmpty(mrs)) {
                final RunMode rmd = RuntimeMode.getRunMode();
                if (rmd == RunMode.Nothing) {
                    log.debug("skip only send run tiny-mail, run={}, id={}", mrs, po.getId());
                    return true;
                }
                if (!RuntimeMode.voteRunMode(mrs)) {
                    log.debug("skip only send run tiny-mail, run={}, cur={}, id={}", mrs, rmd, po.getId());
                    return true;
                }
            }
        }

        final int poDone = BoxedCastUtil.orZero(po.getMaxDone());
        final int maxDone = poDone > 0 ? poDone : tinyMailServiceProp.getMaxDone();
        final int sumDone = BoxedCastUtil.orZero(po.getSumDone());
        if (sumDone >= maxDone) {
            log.debug("skip max-send tiny-mail, max={}, sum={}, id={}", maxDone, sumDone, po.getId());
            return true;
        }

        final int poFail = BoxedCastUtil.orZero(po.getMaxFail());
        final int maxFail = poFail > 0 ? poFail : tinyMailServiceProp.getMaxFail();
        final int sumFail = BoxedCastUtil.orZero(po.getSumFail());
        if (sumFail >= maxFail) {
            log.debug("skip max-fail tiny-mail, max={}, sum={}, id={}", maxFail, sumFail, po.getId());
            return true;
        }

        return false;
    }

    /**
     * should skip sending, others do it
     */
    protected boolean notNextLock(@NotNull WinMailSender po, long now) {
        final WinMailSenderTable t = winMailSenderDao.getTable();
        final int rc = winMailSenderDao
            .ctx()
            .update(t)
            .set(t.NextLock, t.NextLock.add(1))
            .set(t.LastSend, DateLocaling.sysLdt(now))
            .where(t.Id.eq(po.getId()).and(t.NextLock.eq(po.getNextLock())))
            .execute();

        if (rc <= 0) {
            log.debug("skip not-next-lock tiny-mail, id={}", po.getId());
            return true;
        }

        return false;
    }

    protected long saveSendResult(@NotNull WinMailSender po, @NotNull TinyMailMessage message, long cost, long exit, Exception exception, boolean retry) {
        long nextSend = -1;
        try {
            final WinMailSenderTable t = winMailSenderDao.getTable();
            final Map<Object, Object> setter = new HashMap<>();
            if (exception == null) {
                setter.put(t.LastFail, null);
                setter.put(t.LastDone, DateLocaling.sysLdt(exit));
                setter.put(t.LastCost, cost);

                final int poDone = BoxedCastUtil.orZero(po.getMaxDone());
                final int maxDone = poDone > 0 ? poDone : tinyMailServiceProp.getMaxDone();
                if (po.getSumDone() + 1 >= maxDone) {
                    log.debug("done tiny-mail by max-send id={}, subject={}", po.getId(), po.getMailSubj());
                }
                else {
                    nextSend = exit + tinyMailServiceProp.getTryNext().toMillis();
                    log.debug("next done tiny-mail id={}, subject={}", po.getId(), po.getMailSubj());
                }

                setter.put(t.SumSend, t.SumSend.add(1));
                setter.put(t.SumDone, t.SumDone.add(1));
            }
            else {
                setter.put(t.LastFail, ThrowableUtil.toString(exception));
                setter.put(t.LastDone, EmptyValue.DATE_TIME);
                setter.put(t.LastCost, cost);

                final int poFail = BoxedCastUtil.orZero(po.getMaxFail());
                final int maxFail = poFail > 0 ? poFail : tinyMailServiceProp.getMaxFail();
                if (po.getSumFail() + 1 >= maxFail) {
                    log.debug("done tiny-mail by max-fail id={}, subject={}", po.getId(), po.getMailSubj());
                }
                else if (retry) {
                    if (exception instanceof MailWaitException mwe) {
                        if (mwe.isStopRetry()) {
                            log.error("stop stop-retry tiny-mail, id=" + po.getId(), exception);
                        }
                        else {
                            nextSend = mwe.getWaitEpoch();
                        }
                    }
                    else if (exception instanceof MailParseException || exception instanceof MessagingException) {
                        log.error("failed to parse, stop tiny-mail, id=" + po.getId(), exception);
                    }
                    else {
                        nextSend = exit + tinyMailServiceProp.getTryNext().toMillis();
                    }
                }
                else {
                    log.error("stop not-retry tiny-mail, id=" + po.getId(), exception);
                }
                setter.put(t.SumSend, t.SumSend.add(1));
                setter.put(t.SumFail, t.SumFail.add(1));
            }

            boolean hookStop = false;
            if (statusHooks != null) {
                for (StatusHook sh : statusHooks) {
                    try {
                        if (sh.stop(po, cost, exception)) {
                            hookStop = true;
                        }
                    }
                    catch (Exception e) {
                        log.error("should NOT throw in hook, hook-class=" + sh.getClass().getName(), e);
                    }
                }
            }

            if (hookStop) {
                log.debug("hook stop tiny-mail, id={}", po.getId());
            }

            if (nextSend > 0) {
                setter.put(t.NextSend, DateLocaling.sysLdt(nextSend));
                log.debug("next tiny-mail id={}, subject={}", po.getId(), po.getMailSubj());
            }
            else {
                setter.put(t.NextSend, EmptyValue.DATE_TIME);
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
            log.error("failed to save tiny-mail status, id=" + po.getId() + ", subject=" + po.getMailSubj(), e);
            nextSend = exit + tinyMailServiceProp.getTryNext().toMillis();
        }

        return nextSend;
    }

    private boolean doSyncSend(@NotNull WinMailSender po, @NotNull TinyMailMessage message, boolean retry, boolean check) {
        final long start;
        if (check) {
            // condition not match
            if (notMatchProp(po)) return false;

            start = ThreadNow.millis();
            // others do it
            if (notNextLock(po, start)) return false;
        }
        else {
            start = ThreadNow.millis();
        }

        Exception exception = null;
        try {
            mailSenderManager.singleSend(message);
        }
        catch (Exception e) {
            exception = e;
        }
        finally {
            final long end = ThreadNow.millis();
            long nxt = saveSendResult(po, message, end - start, end, exception, retry);
            if (nxt > 0) {
                planAsyncMail(new AsyncMail(po.getId(), nxt, retry, check, null, message));
                log.debug("schedule tiny-mail next-send, err={}, id={}, subject={}", exception == null, po.getId(), po.getMailSubj());
            }
        }

        if (exception != null) {
            if (exception instanceof RuntimeException) {
                throw (RuntimeException) exception;
            }
            else {
                throw new MailSendException("failed tiny-mail, id=" + po.getId() + ", subject=" + po.getMailSubj(), exception);
            }
        }

        return true;
    }

    private long doAsyncSend(@NotNull WinMailSender po, @NotNull TinyMailMessage message, boolean retry, boolean check) {
        // condition not match
        if (check && notMatchProp(po)) return -1;

        final LocalDateTime ns = po.getNextSend();
        final long nsm = ns == null ? 0 : DateLocaling.sysEpoch(ns);
        final long now = ThreadNow.millis();
        final Long id = po.getId();
        final long nxt;
        if (nsm > now) {
            nxt = nsm;
            log.debug("plan async tiny-mail date={} id={}", ns, id);
        }
        else {
            nxt = now;
            log.debug("plan async tiny-mail date=now id={}", id);
        }

        // check format, may fail
        try {
            mailSenderManager.checkMessage(message);
        }
        catch (Exception e) {
            log.error("tiny-mail format error", e);
            // save format error without retry
            saveSendResult(po, message, 0, now, e, false);
            return -1;
        }

        planAsyncMail(new AsyncMail(id, nxt, retry, check, po, message));
        return nxt;
    }

    private void planAsyncMail(@NotNull AsyncMail mail) {
        log.info("planAsyncMail tiny-mail, id={}", mail.id);
        synchronized (asyncMailQueue) {
            asyncMailQueue.removeIf(it -> it.id == mail.id);
            asyncMailQueue.add(mail);
        }
        planAsyncMail(mail.next);
    }

    private void planAsyncMail(@NotNull Collection<AsyncMail> mails) {
        if (mails.isEmpty()) return;

        final Set<Long> ids = new HashSet<>();
        long next = -1;
        for (AsyncMail m : mails) {
            ids.add(m.id);
            if (next <= 0) {
                next = m.next;
            }
            else if (m.next < next) {
                next = m.next;
            }
        }

        log.info("planAsyncMail tiny-mail, size={}", ids.size());
        synchronized (asyncMailQueue) {
            asyncMailQueue.removeIf(it -> ids.contains(it.id));
            asyncMailQueue.addAll(mails);
        }

        planAsyncMail(next);
    }

    private void planAsyncMail(long next) {
        if (next <= 0) {
            log.debug("planAsyncMail tiny-mail, skip={}", next);
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("planAsyncMail tiny-mail, next={}", DateLocaling.sysLdt(next));
        }

        final long nxt = (next / 1_000L + 1) * 1_000L; // ceiling to second
        synchronized (asyncMailSched) {
            asyncMailSched.computeIfAbsent(nxt, k ->
                taskScheduler.schedule(this::sendAsyncMail, Instant.ofEpochMilli(nxt))
            );
        }
    }

    private void sendAsyncMail() {
        final long start = ThreadNow.millis();
        final int count = Math.max(tinyMailServiceProp.getBatchSize(), 1);
        final HashMap<Long, AsyncMail> mails = new HashMap<>(count);
        final HashMap<Long, WinMailSender> freshPo = new HashMap<>(count);
        final ArrayList<Long> dirtyIds = new ArrayList<>(count);
        final ArrayList<TinyMailMessage> messages = new ArrayList<>(count);
        final ArrayList<AsyncMail> nexts = new ArrayList<>(count);

        try {
            while (true) {
                mails.clear();
                freshPo.clear();
                dirtyIds.clear();
                messages.clear();
                nexts.clear();

                // get TopN by prioriy, remove dupli
                synchronized (asyncMailQueue) {
                    asyncMailQueue.removeIf(it -> {
                        if (it.next > start) return false;
                        if (mails.size() >= count) return mails.containsKey(it.id);

                        mails.put(it.id, it);
                        return true;
                    });
                }

                if (mails.isEmpty()) return;

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
                    // mails >= freshPo + dirtyIds(db may less)
                    winMailSenderDao
                        .ctx()
                        .selectFrom(t)
                        .where(t.Id.in(dirtyIds))
                        .fetchInto(WinMailSender.class)
                        .forEach(po -> freshPo.put(po.getId(), po));
                }

                for (WinMailSender po : freshPo.values()) {
                    final AsyncMail am = mails.get(po.getId());
                    if (am == null) continue; // never null

                    if (am.check && (notMatchProp(po) || notNextLock(po, start))) {
                        continue;
                    }

                    if (am.message != null) {
                        messages.add(am.message);
                    }
                    else {
                        final String conf = po.getMailConf();
                        final TinyMailConfig config = mailConfigProvider.bynamedConfig(conf);
                        if (config == null) {
                            log.warn("tiny-mail conf={} not found", conf);
                        }
                        else {
                            messages.add(makeMailMessage(config, po, null));
                        }
                    }
                }

                final List<BatchResult> brs = mailSenderManager.batchSend(messages);

                for (BatchResult br : brs) {
                    final TinyMailMessage msg = br.getTinyMessage();
                    final WinMailSender po = freshPo.get(msg.getBizId()); // must not null
                    final AsyncMail am = mails.get(po.getId());
                    if (am == null) continue; // never null

                    long nxt = saveSendResult(po, msg, br.getCostMillis(), br.getExitMillis(), br.getException(), am.retry);
                    if (nxt > 0) {
                        nexts.add(new AsyncMail(po.getId(), nxt, am.retry, am.check, null, msg));
                    }
                }

                planAsyncMail(nexts);
            }
        }
        finally {
            final int ws = tinyMailServiceProp.getWarnSize();
            final int qs;
            synchronized (asyncMailQueue) {
                qs = asyncMailQueue.size();
            }
            if (qs > 0) {
                planAsyncMail(start + tinyMailServiceProp.getTryNext().toMillis());
                if (qs > ws) {
                    log.warn("plan tiny-mail queue-size={}, idle={}", qs, tinyMailServiceProp.getTryNext());
                }
                else {
                    log.debug("plan tiny-mail queue-size={}, idle={}", qs, tinyMailServiceProp.getTryNext());
                }
            }

            trimAsyncMailSched();
        }
    }

    private final AtomicInteger trimSchedConter = new AtomicInteger(0);
    @Setter @Getter
    private int maxAsyncSched = 1;

    private void trimAsyncMailSched() {
        synchronized (trimSchedConter) {
            if (trimSchedConter.get() > maxAsyncSched) return;
            trimSchedConter.incrementAndGet();
        }

        taskScheduler.schedule(() -> {
                trimSchedConter.decrementAndGet();

                final int ts;
                synchronized (asyncMailSched) {
                    asyncMailSched.entrySet().removeIf(it -> it.getValue().isDone());
                    ts = asyncMailSched.size();
                }

                final int ws = tinyMailServiceProp.getWarnSize();
                if (ts > ws) {
                    log.warn("plan tiny-mail sched-size={}", ts);
                }
                else {
                    log.debug("plan tiny-mail sched-size={}", ts);
                }
            }
            , Instant.ofEpochMilli(ThreadNow.millis() + 1_000)
        );
    }

    @Nullable
    private String toString(String[] arr, String[] elz) {
        if (arr == null) arr = elz;
        return arr == null ? null : String.join(",", arr);
    }

    @Nullable
    private String toString(String str, String[] elz) {
        return invalid(str)
            ? (elz == null || elz.length == 0
                   ? null
                   : String.join(",", elz))
            : str;
    }

    @Nullable
    private String toString(String str, String elz) {
        return invalid(str) ? elz : str;
    }

    @Nullable
    private String toString(Map<String, String> file) {
        if (file == null || file.isEmpty()) return null;
        return FastJsonHelper.string(file);
    }

    @Nullable
    private String stringResource(Map<String, Resource> file) {
        if (file == null || file.isEmpty()) return null;

        Map<String, String> nameUrl = new LinkedHashMap<>();
        for (Map.Entry<String, Resource> en : file.entrySet()) {
            nameUrl.put(en.getKey(), PropHelper.stringResource(en.getValue()));
        }
        return toString(nameUrl);
    }

    @NotNull
    private Map<String, Resource> resourceString(String jsonMap) {
        if (EmptySugar.asEmptyValue(jsonMap)) return Collections.emptyMap();

        final Map<String, Resource> rst = new LinkedHashMap<>();
        Map<String, String> map = FastJsonHelper.object(jsonMap, Map.class, String.class, String.class);
        for (Map.Entry<String, String> en : map.entrySet()) {
            rst.put(en.getKey(), PropHelper.resourceString(en.getValue(), resourceLoader));
        }
        return rst;
    }

    @Data
    public static class AsyncMail implements Comparable<AsyncMail> {
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
