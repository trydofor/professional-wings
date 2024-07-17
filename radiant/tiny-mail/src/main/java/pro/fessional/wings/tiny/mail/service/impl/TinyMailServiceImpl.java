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
import org.springframework.beans.factory.ObjectProvider;
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
import org.springframework.util.function.SingletonSupplier;
import pro.fessional.mirana.best.AssertArgs;
import pro.fessional.mirana.best.Param;
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
import pro.fessional.wings.tiny.mail.sender.MailRetryException;
import pro.fessional.wings.tiny.mail.sender.MailSenderManager;
import pro.fessional.wings.tiny.mail.sender.MailSenderManager.BatchResult;
import pro.fessional.wings.tiny.mail.sender.MailStopException;
import pro.fessional.wings.tiny.mail.sender.MailWaitException;
import pro.fessional.wings.tiny.mail.sender.TinyMailConfig;
import pro.fessional.wings.tiny.mail.sender.TinyMailMessage;
import pro.fessional.wings.tiny.mail.service.TinyMail;
import pro.fessional.wings.tiny.mail.service.TinyMailLazy;
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
    @Setter(onMethod_ = { @Autowired })
    protected ObjectProvider<StatusHook> statusHookProvider;
    @Setter(onMethod_ = { @Autowired })
    protected ObjectProvider<TinyMailLazy> lazyBeanProvider;

    // init by afterPropertiesSet
    protected ThreadPoolTaskScheduler taskScheduler;
    protected SingletonSupplier<Map<String, TinyMailLazy>> lazyBeanHolder;
    protected SingletonSupplier<List<StatusHook>> statusHookHolder;

    protected final PriorityQueue<AsyncMail> asyncMailQueue = new PriorityQueue<>();
    protected final TreeMap<Long, ScheduledFuture<?>> asyncMailSched = new TreeMap<>();

    @Override
    public boolean send(@NotNull TinyMail message, boolean retry) {
        return doSend(true, message, retry) == Success;
    }

    @Override
    public long post(@NotNull TinyMail message, boolean retry) {
        try {
            return doSend(true, message, retry);
        }
        catch (MailRetryException e) {
            log.warn("fail to post tiny-mail, next-retry=" + e.getNextEpoch() + ", subject=" + message.getSubject(), e.getCause());
            return e.getNextEpoch();
        }
        catch (Exception e) {
            log.error("fail to post tiny-mail, retry=" + retry + ", subject=" + message.getSubject(), e);
            return ErrOther;
        }
    }

    @Override
    public long emit(@NotNull TinyMail message, boolean retry) {
        try {
            return doSend(false, message, retry);
        }
        catch (MailRetryException e) {
            log.warn("fail to emit tiny-mail, next-retry=" + e.getNextEpoch() + ", subject=" + message.getSubject(), e.getCause());
            return e.getNextEpoch();
        }
        catch (Exception e) {
            log.error("fail to emit tiny-mail, retry=" + retry + ", subject=" + message.getSubject(), e);
            return ErrOther;
        }
    }

    @Override
    public boolean send(long id, boolean retry, boolean check) {
        return doSend(true, id, retry, check) == Success;
    }

    @Override
    public long post(long id, boolean retry, boolean check) {
        try {
            return doSend(true, id, retry, check);
        }
        catch (MailRetryException e) {
            log.warn("fail to post tiny-mail, next-retry=" + e.getNextEpoch() + ", id=" + id, e.getCause());
            return e.getNextEpoch();
        }
        catch (Exception e) {
            log.error("fail to post tiny-mail, retry=" + retry + ", id=" + id, e);
            return ErrOther;
        }
    }

    @Override
    public long emit(long id, boolean retry, boolean check) {
        try {
            return doSend(false, id, retry, check);
        }
        catch (MailRetryException e) {
            log.warn("fail to emit tiny-mail, next-retry=" + e.getNextEpoch() + ", id=" + id, e.getCause());
            return e.getNextEpoch();
        }
        catch (Exception e) {
            log.error("fail to emit tiny-mail, retry=" + retry + ", id=" + id, e);
            return ErrOther;
        }
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
        log.info("tiny-mail taskScheduler, prefix=" + taskScheduler.getThreadNamePrefix());

        final long idle = tinyMailServiceProp.getBootScan().toMillis();
        if (idle > 0) {
            log.info("tiny-mail schedule boot-scan after={} ms", idle);
            taskScheduler.schedule(this::scanIdle, Instant.ofEpochMilli(ThreadNow.millis() + idle));
        }

        statusHookHolder = SingletonSupplier.of(() -> statusHookProvider.orderedStream().toList());
        lazyBeanHolder = SingletonSupplier.of(() -> {
            Map<String, TinyMailLazy> map = new HashMap<>();
            for (TinyMailLazy bean : lazyBeanProvider) {
                TinyMailLazy old = map.put(bean.lazyBean(), bean);
                if (old != null) {
                    log.error("lazy bean name existed, name={}, new-bean={}", old.lazyBean(), bean.getClass());
                }
            }
            return map.isEmpty() ? Collections.emptyMap() : map;
        });
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public long save(@NotNull TinyMailPlain msg, boolean check) {
        final String conf = msg.getConf();
        final TinyMailConfig config = mailConfigProvider.bynamedConfig(conf);
        AssertArgs.notNull(config, "skip tiny-mail conf={} not found", conf);

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
        po.setLazyBean(msg.getLazyBean());
        po.setLazyPara(msg.getLazyPara());

        // PropertyMapper
        IfSetter.nonnull(po::setMaxFail, msg.getMaxFail());
        IfSetter.nonnull(po::setMaxDone, msg.getMaxDone());

        IfSetter.nonnull(po::setRefType, msg.getRefType());
        IfSetter.nonnull(po::setRefKey1, msg.getRefKey1());
        IfSetter.nonnull(po::setRefKey2, msg.getRefKey2());

        if (check) {
            // try to check message format
            final TinyMailMessage tms = makeMailMessage(config, po, null);
            mailSenderManager.checkMessage(tms);
        }

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
            log.info("reset tiny-mail scan idle to prop={} ms", idle);
        }

        if (idle > 0) {
            var task = taskScheduler.schedule(this::scanIdle, Instant.ofEpochMilli(ThreadNow.millis() + idle));
            idleScanTask.set(task);
            log.info("plan tiny-mail scan, idle={} ms", idle);
        }
        else {
            idleScanTask.set(null);
            log.info("stop tiny-mail scan, idle={} ms", idle);
        }

        return size;
    }

    protected int scanSync() {
        final long now = ThreadNow.millis();
        final LocalDateTime min = DateLocaling.sysLdt(now - tinyMailServiceProp.getMaxNext().toMillis());
        final LocalDateTime max = DateLocaling.sysLdt(now + tinyMailServiceProp.getTryNext().toMillis());
        log.info("scan tiny-mail to queue, next-send min={}, max={}", min, max);

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
        po.setLazyBean(msg.getLazyBean());
        po.setLazyPara(msg.getLazyPara());

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

        if (po.getMailText() == null) {
            var bean = lazyBeanHolder.obtain().get(po.getLazyBean());
            if (bean == null) {
                log.error("stop lazy tiny-mail, not found bean={}, id={}", bean, po.getId());
                return true;
            }
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

    protected long doSend(boolean sync, @NotNull TinyMail message, boolean retry) {
        final String conf = message.getConf();
        final TinyMailConfig config = mailConfigProvider.bynamedConfig(conf);
        AssertArgs.notNull(config, "skip tiny-mail conf={} not found", conf);

        final WinMailSender po = saveMailSender(config, message);

        final TinyMailMessage mailMessage = makeMailMessage(config, po, message);
        if (sync) {
            return doSyncSend(po, mailMessage, retry, true);
        }
        else {
            return doAsyncSend(po, mailMessage, retry, true);
        }
    }

    protected long doSend(boolean sync, long id, boolean retry, boolean check) {
        final WinMailSender po = winMailSenderDao.fetchOneById(id);
        AssertArgs.notNull(po, "skip tiny-mail not found by id={}", id);

        final String conf = po.getMailConf();
        final TinyMailConfig config = mailConfigProvider.bynamedConfig(conf);
        AssertArgs.notNull(config, "skip tiny-mail conf={} not found, id={}", conf, id);

        final TinyMailMessage mailMessage = makeMailMessage(config, po, null);
        if (sync) {
            return doSyncSend(po, mailMessage, retry, check);
        }
        else {
            return doAsyncSend(po, mailMessage, retry, check);
        }
    }

    /**
     * next if done lt max or exception and retry
     */
    protected long saveSendResult(@NotNull WinMailSender po, long cost, long exit, Exception exception, boolean retry) {
        long nextSend = -1;
        final Long id = po.getId();
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
                    log.debug("done tiny-mail by max-send id={}, subject={}", id, po.getMailSubj());
                }
                else {
                    nextSend = exit + tinyMailServiceProp.getTryNext().toMillis();
                    log.debug("next done tiny-mail id={}, subject={}", id, po.getMailSubj());
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
                    log.debug("done tiny-mail by max-fail id={}, subject={}", id, po.getMailSubj());
                }
                else if (retry) {
                    if (exception instanceof MailStopException) {
                        log.warn("stop tiny-mail by stop-exception, id=" + id, exception);
                    }
                    else if (exception instanceof MailWaitException mwe) {
                        if (mwe.isStopRetry()) {
                            log.error("stop tiny-mail by stop-retry, id=" + id, exception);
                        }
                        else {
                            nextSend = mwe.getWaitEpoch();
                        }
                    }
                    else if (exception instanceof MailParseException || exception instanceof MessagingException) {
                        log.error("stop tiny-mail by failed parse, id=" + id, exception);
                    }
                    else {
                        nextSend = exit + tinyMailServiceProp.getTryNext().toMillis();
                    }
                }
                else {
                    log.error("stop tiny-mail by not-retry, id=" + id, exception);
                }

                setter.put(t.SumSend, t.SumSend.add(1));
                setter.put(t.SumFail, t.SumFail.add(1));
            }

            boolean hookStop = false;

            for (StatusHook sh : statusHookHolder.obtain()) {
                try {
                    if (sh.stop(po, cost, exception)) {
                        hookStop = true;
                    }
                }
                catch (Exception e) {
                    log.error("should NOT throw in hook, hook-class=" + sh.getClass().getName(), e);
                }
            }

            if (hookStop) {
                log.debug("stop tiny-mail by hook, id={}", id);
            }

            if (nextSend > 0) {
                setter.put(t.NextSend, DateLocaling.sysLdt(nextSend));
                log.debug("next tiny-mail id={}, subject={}", id, po.getMailSubj());
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
                                .where(t.Id.eq(id))
                                .execute();
            });
        }
        catch (Exception e) {
            log.error("failed to save tiny-mail status, id=" + id + ", subject=" + po.getMailSubj(), e);
            nextSend = exit + tinyMailServiceProp.getTryNext().toMillis();
        }

        return nextSend;
    }

    protected void editLazyMail(@NotNull WinMailSender po, @NotNull @Param.Out TinyMailMessage message) {
        // nonnull means no need to edit
        if (message.getContent() != null) return;

        final Long id = po.getId();
        final String bn = po.getLazyBean();
        var bean = lazyBeanHolder.obtain().get(bn);
        if (bean == null) {
            throw new MailStopException("tiny-mail lazy-edit, not-found bean=" + bn + ", id=" + id);
        }

        var edit = bean.lazyEdit(po.getLazyPara());
        if (edit == null) {
            throw new MailStopException("tiny-mail lazy-edit, edit is null, id=" + id);
        }

        final WinMailSenderTable t = winMailSenderDao.getTable();
        final Map<Object, Object> setter = new HashMap<>();

        Boolean html = edit.getHtml();
        if (html != null) {
            setter.put(t.MailHtml, html);
            message.setHtml(html);
        }

        String subj = edit.getSubject();
        if (subj != null) {
            setter.put(t.MailSubj, subj);
            message.setSubject(subj);
        }

        String text = edit.getContent();
        if (text != null) {
            setter.put(t.MailText, text);
            message.setContent(text);
        }

        Map<String, Resource> file = edit.getAttachment();
        if (file != null && !file.isEmpty()) {
            setter.put(t.MailFile, stringResource(file));
            message.setAttachment(file);
        }

        if (setter.isEmpty()) {
            throw new MailStopException("tiny-mail lazy-edit, edit is empty, id=" + id);
        }

        log.debug("lazy-edit tiny-mail, id={}", id);

        journalService.commit(Jane.Lazify, journal -> {
            setter.put(t.CommitId, journal.getCommitId());
            setter.put(t.ModifyDt, journal.getCommitDt());
            winMailSenderDao.ctx()
                            .update(t)
                            .set(setter)
                            .where(t.Id.eq(id))
                            .execute();
        });
    }

    private long doSyncSend(@NotNull WinMailSender po, @NotNull TinyMailMessage message, boolean retry, boolean check) {
        final long start;
        if (check) {
            // condition not match
            if (notMatchProp(po)) return ErrCheck;

            start = ThreadNow.millis();
            // others do it
            if (notNextLock(po, start)) return ErrCheck;
        }
        else {
            start = ThreadNow.millis();
        }

        final Long id = po.getId();
        try {
            editLazyMail(po, message);
        }
        catch (Exception e) {
            log.error("stop tiny-mail for lazy-edit error, id=" + id, e);
            saveSendResult(po, 0, start, e, false); // no retry is lazy fail
            return ErrCheck;
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
            long nxt = saveSendResult(po, end - start, end, exception, retry);
            if (nxt > 0) {
                planAsyncMail(new AsyncMail(id, nxt, retry, check, null, message));
                log.debug("plan tiny-mail next-send, err={}, id={}, subject={}", exception == null, id, po.getMailSubj());
                if (exception != null) {
                    exception = new MailRetryException(nxt, exception); // runtime
                }
            }
        }

        if (exception == null) return Success;

        // rethrow exception
        if (exception instanceof RuntimeException) {
            throw (RuntimeException) exception;
        }
        else {
            throw new MailSendException("failed tiny-mail, id=" + id + ", subject=" + po.getMailSubj(), exception);
        }
    }

    private long doAsyncSend(@NotNull WinMailSender po, @NotNull TinyMailMessage message, boolean retry, boolean check) {
        // condition not match
        if (check && notMatchProp(po)) return ErrCheck;

        final long start = ThreadNow.millis();
        final Long id = po.getId();
        try {
            editLazyMail(po, message);
        }
        catch (Exception e) {
            log.error("stop tiny-mail for lazy-edit error, id=" + id, e);
            saveSendResult(po, 0, start, e, false); // no retry is lazy fail
            return ErrCheck;
        }

        final LocalDateTime ns = po.getNextSend();
        final long nsm = ns == null ? 0 : DateLocaling.sysEpoch(ns);

        long next;
        if (nsm > start) {
            next = nsm;
            log.debug("plan async tiny-mail date={} id={}", ns, id);
        }
        else {
            next = start;
            log.debug("plan async tiny-mail date=now id={}", id);
        }

        planAsyncMail(new AsyncMail(id, next, retry, check, po, message));
        return next;
    }

    private void planAsyncMail(@NotNull AsyncMail mail) {
        log.debug("plan async tiny-mail, id={}", mail.id);
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

        log.debug("plan async tiny-mail, size={}", ids.size());
        synchronized (asyncMailQueue) {
            asyncMailQueue.removeIf(it -> ids.contains(it.id));
            asyncMailQueue.addAll(mails);
        }

        planAsyncMail(next);
    }

    private void planAsyncMail(long next) {
        if (next <= 0) {
            log.debug("plan async tiny-mail, skip={}", next);
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("plan async tiny-mail, next={}", DateLocaling.sysLdt(next));
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
                    final Long id = po.getId();
                    final AsyncMail am = mails.get(id);
                    if (am == null) continue; // never null

                    if (am.check && (notMatchProp(po) || notNextLock(po, start))) {
                        continue;
                    }

                    final TinyMailMessage msg;
                    if (am.message != null) {
                        msg = am.message;
                    }
                    else {
                        final String conf = po.getMailConf();
                        final TinyMailConfig config = mailConfigProvider.bynamedConfig(conf);
                        if (config == null) {
                            log.warn("tiny-mail conf={} not found, id={}", conf, id);
                            continue;
                        }
                        else {
                            msg = makeMailMessage(config, po, null);
                        }
                    }

                    try {
                        editLazyMail(po, msg);
                        messages.add(msg);
                    }
                    catch (Exception e) {
                        log.error("stop tiny-mail for lazy-edit error, id=" + id, e);
                        saveSendResult(po, 0, start, e, false); // no retry is lazy fail
                    }
                }

                final List<BatchResult> brs = mailSenderManager.batchSend(messages);

                for (BatchResult br : brs) {
                    final TinyMailMessage msg = br.getTinyMessage();
                    final WinMailSender po = freshPo.get(msg.getBizId()); // must not null
                    final AsyncMail am = mails.get(po.getId());
                    if (am == null) continue; // never null

                    long nxt = saveSendResult(po, br.getCostMillis(), br.getExitMillis(), br.getException(), am.retry);
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
    @Setter
    @Getter
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
        Update,
        Lazify
    }
}
