package pro.fessional.wings.tiny.task.service.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Service;
import pro.fessional.mirana.cast.BoxedCastUtil;
import pro.fessional.mirana.lock.JvmStaticGlobalLock;
import pro.fessional.mirana.pain.ThrowableUtil;
import pro.fessional.mirana.stat.JvmStat;
import pro.fessional.mirana.time.DateLocaling;
import pro.fessional.mirana.time.DateParser;
import pro.fessional.mirana.time.Sleep;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.faceless.convention.EmptySugar;
import pro.fessional.wings.faceless.convention.EmptyValue;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.silencer.modulate.RunMode;
import pro.fessional.wings.silencer.modulate.RuntimeMode;
import pro.fessional.wings.slardar.async.TaskSchedulerHelper;
import pro.fessional.wings.slardar.jackson.JacksonHelper;
import pro.fessional.wings.tiny.task.database.autogen.tables.WinTaskDefineTable;
import pro.fessional.wings.tiny.task.database.autogen.tables.daos.WinTaskDefineDao;
import pro.fessional.wings.tiny.task.database.autogen.tables.daos.WinTaskResultDao;
import pro.fessional.wings.tiny.task.database.autogen.tables.pojos.WinTaskDefine;
import pro.fessional.wings.tiny.task.database.autogen.tables.pojos.WinTaskResult;
import pro.fessional.wings.tiny.task.schedule.exec.ExecHolder;
import pro.fessional.wings.tiny.task.schedule.exec.NoticeExec;
import pro.fessional.wings.tiny.task.schedule.exec.TaskerExec;
import pro.fessional.wings.tiny.task.service.TinyTaskExecService;
import pro.fessional.wings.tiny.task.spring.prop.TinyTaskEnabledProp;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;

import static pro.fessional.wings.silencer.spring.help.CommonPropHelper.arrayOrNull;
import static pro.fessional.wings.tiny.task.schedule.exec.NoticeExec.WhenDone;
import static pro.fessional.wings.tiny.task.schedule.exec.NoticeExec.WhenExec;
import static pro.fessional.wings.tiny.task.schedule.exec.NoticeExec.WhenFail;
import static pro.fessional.wings.tiny.task.schedule.exec.NoticeExec.WhenFeed;

/**
 * @author trydofor
 * @since 2022-12-21
 */
@Service
@Slf4j
public class TinyTaskExecServiceImpl implements TinyTaskExecService {

    protected static final ConcurrentHashMap<Long, ScheduledFuture<?>> Handle = new ConcurrentHashMap<>();
    protected static final ConcurrentHashMap<Long, Boolean> Cancel = new ConcurrentHashMap<>();
    protected static final ConcurrentHashMap<Long, Integer> Booted = new ConcurrentHashMap<>();

    @Setter(onMethod_ = {@Value("${spring.application.name}")})
    protected String appName;

    @Setter(onMethod_ = {@Autowired})
    protected WinTaskDefineDao winTaskDefineDao;

    @Setter(onMethod_ = {@Autowired})
    protected WinTaskResultDao winTaskResultDao;

    @Setter(onMethod_ = {@Autowired})
    protected LightIdService lightIdService;

    @Setter(onMethod_ = {@Autowired})
    protected JournalService journalService;

    @Setter(onMethod_ = {@Value("${" + TinyTaskEnabledProp.Key$dryrun + "}")})
    protected boolean dryrun = false;

    @Override
    public boolean launch(long id) {
        Cancel.remove(id);
        return relaunch(id);
    }

    @Override
    public boolean force(long id) {
        final WinTaskDefine td = winTaskDefineDao.fetchOneById(id);
        if (td == null) {
            log.info("skip task for not found, id={}", id);
            return false;
        }

        final boolean fast = BoxedCastUtil.orTrue(td.getTaskerFast());
        TaskSchedulerHelper.referScheduler(fast).schedule(() -> {
            long execTms = ThreadNow.millis();
            long doneTms = -1;
            long failTms = -1;
            final String taskerName = td.getTaskerName() + " force";
            final String noticeConf = td.getNoticeConf();

            String taskMsg = "force task id=" + id;
            NoticeExec<?> notice = null;
            Set<String> ntcWhen = Collections.emptySet();
            try {
                final TaskerExec tasker = ExecHolder.getTasker(td.getTaskerBean(), true);

                notice = ExecHolder.getNotice(td.getNoticeBean(), false);
                if (notice != null) ntcWhen = noticeWhen(td.getNoticeWhen());

                postNotice(notice, noticeConf, ntcWhen, taskerName, taskMsg, execTms, WhenExec);
                log.info("task force exec, id={}", id);

                final Object result;
                if (dryrun) {
                    final int slp = RandomUtils.nextInt(10, 2000);
                    result = "dryrun and sleep " + slp;
                    Sleep.ignoreInterrupt(slp);
                    log.info("task force done, dryrun and sleep {} ms, id={}", slp, id);
                }
                else {
                    result = tasker.invoke(td.getTaskerPara(), true);
                    log.info("task force done, id={}", id);
                }
                //
                doneTms = ThreadNow.millis();
                taskMsg = stringResult(result);
                postNotice(notice, noticeConf, ntcWhen, taskerName, taskMsg, doneTms, WhenFeed, WhenDone);
            }
            catch (Exception e) {
                log.error("task force fail, id=" + id, e);
                failTms = ThreadNow.millis();
                taskMsg = ThrowableUtil.toString(e);
                postNotice(notice, noticeConf, ntcWhen, taskerName, taskMsg, failTms, WhenFail);
            }
            finally {
                try {
                    saveResult(id, execTms, failTms, doneTms, taskMsg, td.getDurFail());
                }
                catch (Exception e) {
                    log.error("failed to save result, id=" + id, e);
                }
            }
        }, Instant.ofEpochMilli(ThreadNow.millis()));
        return true;
    }

    @Override
    public boolean cancel(long id) {
        Cancel.put(id, Boolean.TRUE);
        final ScheduledFuture<?> ft = Handle.get(id);
        if (ft == null) {
            log.info("cancel not found, id={}", id);
            return true;
        }
        final boolean r = ft.cancel(false);
        if (r) {
            Handle.remove(id);
        }
        log.info("cancel success={}, id={}", r, id);
        return r;
    }

    @Override
    public Set<Long> running() {
        final HashSet<Long> set = new HashSet<>();
        final Enumeration<Long> en = Handle.keys();
        while (en.hasMoreElements()) {
            set.add(en.nextElement());
        }
        return set;
    }

    private boolean relaunch(long id) {
        final Lock lock = JvmStaticGlobalLock.get(id);
        try {
            lock.lock();
            if (Handle.containsKey(id)) {
                log.info("skip task for launching, id={}", id);
                return false;
            }

            final WinTaskDefine td = winTaskDefineDao.fetchOneById(id);
            if (td == null) {
                log.info("skip task for not found, id={}", id);
                return false;
            }

            log.info("prepare task name={}, id={}", td.getTaskerName(), td.getId());
            if (notEnable(td.getEnabled(), id)
                || notApps(td.getTaskerApps(), id)
                || notRuns(td.getTaskerRuns(), id)) {
                return false;
            }

            final long next = calcNextExec(td);
            if (next < 0) return false;

            //
            saveNextExec(next, td);

            final boolean fast = BoxedCastUtil.orTrue(td.getTaskerFast());
            final ThreadPoolTaskScheduler taskScheduler = TaskSchedulerHelper.referScheduler(fast);

            if (taskScheduler.getScheduledExecutor().isShutdown()) {
                log.error("TaskScheduler={} is shutdown, name={} id={}", fast, td.getTaskerName(), td.getId());
                return false;
            }

            final ScheduledFuture<?> handle = taskScheduler.schedule(() -> {
                long execTms = ThreadNow.millis();
                try {
                    if (notNextLock(td, execTms)) {
                        log.warn("skip task for Not nextLock, should manually check and launch it, id={}", id);
                        Handle.remove(id);
                        return;
                    }
                }
                catch (Exception e) {
                    log.warn("failed to check nextLock", e);
                    Handle.remove(id);
                    return;
                }

                long doneTms = -1;
                long failTms = -1;
                final String taskerName = td.getTaskerName();
                final String noticeConf = td.getNoticeConf();

                String taskMsg = "relaunch task id=" + id;
                NoticeExec<?> notice = null;
                Set<String> ntcWhen = Collections.emptySet();
                try {
                    final TaskerExec tasker = ExecHolder.getTasker(td.getTaskerBean(), true);

                    notice = ExecHolder.getNotice(td.getNoticeBean(), false);
                    if (notice != null) ntcWhen = noticeWhen(td.getNoticeWhen());

                    postNotice(notice, noticeConf, ntcWhen, taskerName, taskMsg, execTms, WhenExec);
                    log.info("task exec, id={}", id);

                    final Object result;
                    if (dryrun) {
                        final int slp = RandomUtils.nextInt(10, 2000);
                        result = "dryrun and sleep " + slp;
                        Sleep.ignoreInterrupt(slp);
                        log.info("task done, dryrun and sleep {} ms, id={}", slp, id);
                    }
                    else {
                        result = tasker.invoke(td.getTaskerPara(), true);
                        log.info("task done, id={}", id);
                    }
                    //
                    doneTms = ThreadNow.millis();
                    taskMsg = stringResult(result);
                    postNotice(notice, noticeConf, ntcWhen, taskerName, taskMsg, doneTms, WhenFeed, WhenDone);
                }
                catch (Exception e) {
                    log.error("task fail, id=" + id, e);
                    failTms = ThreadNow.millis();
                    taskMsg = ThrowableUtil.toString(e);
                    postNotice(notice, noticeConf, ntcWhen, taskerName, taskMsg, failTms, WhenFail);
                }
                finally {
                    try {
                        Handle.remove(id);
                        saveResult(id, execTms, failTms, doneTms, taskMsg, td.getDurFail());
                    }
                    catch (Exception e) {
                        log.error("failed to save result, id=" + id, e);
                    }

                    if (canRelaunch(id, doneTms, failTms, td)) { // canceled
                        relaunch(id);
                    }
                }
            }, Instant.ofEpochMilli(next));

            //
            Handle.put(id, handle);
            return true;
        }
        finally {
            lock.unlock();
        }
    }

    //
    private boolean notEnable(Boolean b, long id) {
        if (BoxedCastUtil.orTrue(b)) {
            return false;
        }
        log.info("skip task for not enabled, id={}", id);
        return true;
    }

    private boolean notApps(String apps, long id) {
        if (StringUtils.isEmpty(apps)) return false;

        for (String s : arrayOrNull(apps, true)) {
            if (s.trim().equals(appName)) return false;
        }
        log.info("skip task for not apps={}, cur={}, id={}", apps, appName, id);
        return true;
    }

    private boolean notRuns(String runs, long id) {
        if (StringUtils.isEmpty(runs)) return false;

        final RunMode rmd = RuntimeMode.getRunMode();
        if (rmd == RunMode.Nothing) {
            log.info("skip task for not runs={}, cur is null, id={}", runs, id);
            return true;
        }

        if (!RuntimeMode.hasRunMode(arrayOrNull(runs, true))) {
            log.info("skip task for not runs={}, cur={}, id={}", runs, rmd, id);
            return true;
        }

        return false;
    }

    private Set<String> noticeWhen(String nw) {
        if (nw == null || nw.isEmpty()) return Collections.emptySet();
        Set<String> rs = new HashSet<>();
        for (String s : arrayOrNull(nw, true)) {
            rs.add(s.trim().toLowerCase());
        }
        return rs;
    }


    private String stringResult(Object result) {
        if (result == null) return null;
        if (result instanceof CharSequence) {
            return result.toString();
        }
        else {
            return JacksonHelper.string(result);
        }
    }

    private void postNotice(NoticeExec<?> ntc, String cnf, Set<String> whs, String tn, String msg, long ms, String... wh) {
        if (ntc == null) return;
        final String zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(ms), ThreadNow.sysZoneId()).toString();
        for (String w : wh) {
            if (whs.contains(w)) {
                if (w.equals(WhenFeed)) {
                    if (!dryrun && StringUtils.isNotEmpty(msg)) {
                        ntc.postNotice(cnf, tn + " " + w.toUpperCase(), zdt + "\n\n" + msg);
                        return;
                    }
                }
                else {
                    ntc.postNotice(cnf, tn + " " + w.toUpperCase(), msg == null ? zdt : zdt + "\n\n" + msg);
                    return;
                }
            }
        }
    }

    private void saveNextExec(long next, WinTaskDefine td) {
        journalService.commit(Jane.SaveNextExec, journal -> {
            final WinTaskDefineTable t = winTaskDefineDao.getTable();
            winTaskDefineDao.ctx().update(t)
                            .set(t.CommitId, journal.getCommitId())
                            .set(t.ModifyDt, journal.getCommitDt())
                            .set(t.NextExec, milliLdt(next, ThreadNow.sysZoneId()))
                            .where(t.Id.eq(td.getId()))
                            .execute();
        });
    }

    private boolean notNextLock(WinTaskDefine td, long now) {
        final WinTaskDefineTable t = winTaskDefineDao.getTable();
        final int rc = winTaskDefineDao
                .ctx()
                .update(t)
                .set(t.NextLock, t.NextLock.add(1))
                .set(t.LastExec, milliLdt(now, ThreadNow.sysZoneId()))
                .where(t.Id.eq(td.getId()).and(t.NextLock.eq(td.getNextLock())))
                .execute();
        return rc <= 0;
    }

    private void saveResult(Long id, long exec, long fail, long done, String msg, int cf) {

        final WinTaskDefineTable td = winTaskDefineDao.getTable();
        Map<Field<?>, Object> setter = new HashMap<>();

        setter.put(td.LastExec, milliLdt(exec, ThreadNow.sysZoneId()));
        setter.put(td.SumExec, td.SumExec.add(1));
        setter.put(td.NextExec, EmptyValue.DATE_TIME);

        if (fail > 0) {
            setter.put(td.LastFail, milliLdt(fail, ThreadNow.sysZoneId()));
            setter.put(td.LastDone, EmptyValue.DATE_TIME);
            setter.put(td.SumFail, td.SumFail.add(1));
            setter.put(td.DurFail, cf > 0 ? td.DurFail.add(1) : 1);
        }
        else { // done
            setter.put(td.LastFail, EmptyValue.DATE_TIME);
            setter.put(td.LastDone, milliLdt(done, ThreadNow.sysZoneId()));
            setter.put(td.SumDone, td.SumDone.add(1));
            setter.put(td.DurFail, 0);
        }

        final WinTaskResult po = new WinTaskResult();
        po.setId(lightIdService.getId(winTaskResultDao.getTable()));
        po.setTaskId(id);
        po.setTaskApp(appName);
        po.setTaskPid(JvmStat.jvmPid());
        po.setTaskMsg(msg);

        final ZoneId zidSys = ThreadNow.sysZoneId();
        po.setTimeExec(milliLdt(exec, zidSys));
        po.setTimeFail(milliLdt(fail, zidSys));
        po.setTimeDone(milliLdt(done, zidSys));
        po.setTimeCost((int) (Math.max(done, fail) - exec));

        journalService.commit(Jane.SaveResult, journal -> {
            //
            setter.put(td.CommitId, journal.getCommitId());
            setter.put(td.ModifyDt, journal.getCommitDt());
            winTaskDefineDao.ctx()
                            .update(td)
                            .set(setter)
                            .where(td.Id.eq(id))
                            .execute();

            // history
            winTaskResultDao.insert(po);
        });
    }

    private LocalDateTime milliLdt(long m, ZoneId zid) {
        if (m < 0) return EmptyValue.DATE_TIME;
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(m), zid);
    }

    private boolean canRelaunch(long id, long doneTms, long failTms, WinTaskDefine td) {
        final int duringExec = td.getDuringExec();
        final int sumExec = td.getSumExec();
        if (duringExec > 0 && duringExec <= sumExec + 1) {
            log.info("remove task for duringExec={}, sumExec={}, id={}", duringExec, sumExec, id);
            return false;
        }

        final int duringDone = td.getDuringDone();
        final int sumDone = td.getSumDone();
        if (duringDone > 0 && duringDone <= (doneTms < 0 ? sumDone : sumDone + 1)) {
            log.info("remove task for duringDone={}, sumDone={}, id={}", duringDone, sumDone, id);
            return false;
        }

        final int duringFail = td.getDuringFail();
        final int durFail = td.getDurFail();
        if (duringFail > 0 && duringFail <= (failTms < 0 ? durFail : durFail + 1)) {
            log.info("remove task for duringFail={}, durFail={}, id={}", duringFail, durFail, id);
            return false;
        }

        if (Cancel.containsKey(id)) { // canceled
            log.info("remove task for canceled, id={}", id);
            return false;
        }

        final int duringBoot = td.getDuringBoot();
        if (duringBoot > 0) {
            final int bct = Booted.compute(id, (ignored, v) -> v == null ? 1 : v + 1);
            if (bct >= duringBoot) {
                log.info("remove task for duringBoot={}, id={}", bct, id);
                return false;
            }
        }

        return true;
    }

    private long calcNextExec(WinTaskDefine td) {
        final String zid = td.getTimingZone();
        final ZoneId zone = StringUtils.isEmpty(zid) ? ThreadNow.sysZoneId() : ZoneId.of(zid);

        final long now = ThreadNow.millis();
        if (notRanged(td, zone, now)) return -1;

        final Long id = td.getId();

        final long timingMiss = td.getTimingMiss() * 1000L;
        // Planned, program waw killed before execution ends
        final long nextMs = DateLocaling.sysEpoch(td.getNextExec());
        if (nextMs + timingMiss >= now) {
            log.info("launch misfire task, id={}", id);
            return nextMs;
        }

        final Trigger trigger = makeTrigger(td, zone);
        final SimpleTriggerContext context = makeContext(td, zone, now);

        while (true) {
            Instant next = trigger.nextExecution(context);
            if (next == null) {
                log.info("skip task for trigger not fire, id={}", id);
                return -1;
            }

            final long nxt = next.toEpochMilli();
            if (nxt < now) {
                if (timingMiss > 0 && nxt + timingMiss >= now) {
                    log.info("launch task for misfire={}, id={}", next, id);
                    return nxt;
                }
                else {
                    context.update(next, next, next);
                }
            }
            else {
                log.info("launch task for next={}, id={}", next, id);
                return nxt;
            }
        }
    }

    @SuppressWarnings("all")
    private SimpleTriggerContext makeContext(WinTaskDefine td, ZoneId zone, long now) {
        // can Not replace util.Date with Instance
        Instant lastActual = null;
        final LocalDateTime lastExec = td.getLastExec();
        if (!EmptySugar.asEmptyValue(lastExec)) {
            lastActual = Instant.ofEpochMilli(DateLocaling.sysEpoch(lastExec));
        }

        Instant lastCompletion = null;
        final LocalDateTime lastDone = td.getLastDone();
        if (!EmptySugar.asEmptyValue(lastDone)) {
            lastCompletion = Instant.ofEpochMilli(DateLocaling.sysEpoch(lastDone));
        }

        return new SimpleTriggerContext(lastActual, lastActual, lastCompletion);
    }

    private Trigger makeTrigger(WinTaskDefine td, ZoneId zone) {
        final String cron = td.getTimingCron();
        if (StringUtils.isNotEmpty(cron)) {
            log.info("use trigger cron={}, id={}", cron, td.getId());
            return new CronTrigger(cron, zone);
        }

        final int idle = td.getTimingIdle();
        if (idle > 0) {
            log.info("use trigger idle={}, id={}", idle, td.getId());
            PeriodicTrigger trg = new PeriodicTrigger(Duration.ofSeconds(idle));
            trg.setFixedRate(false);
            return trg;
        }

        final int rate = td.getTimingRate();
        if (rate > 0) {
            log.info("use trigger rate={}, id={}", rate, td.getId());
            PeriodicTrigger trg = new PeriodicTrigger(Duration.ofSeconds(rate));
            trg.setFixedRate(true);
            return trg;
        }

        throw new IllegalArgumentException("no cron/idle/rate to make trigger");
    }

    private boolean notRanged(WinTaskDefine td, ZoneId zone, long now) {
        final String duringFrom = td.getDuringFrom();
        if (StringUtils.isNotEmpty(duringFrom)) {
            final LocalDateTime ldt = DateParser.parseDateTime(duringFrom);
            final long ms = DateLocaling.useEpoch(ldt, zone);
            if (ms > now) {
                log.info("skip task for duringFrom={}, id={}", duringFrom, td.getId());
                return true;
            }
        }

        final String duringStop = td.getDuringStop();
        if (StringUtils.isNotEmpty(duringStop)) {
            final LocalDateTime ldt = DateParser.parseDateTime(duringStop);
            final long ms = DateLocaling.useEpoch(ldt, zone);
            if (ms < now) {
                log.info("skip task for duringStop={}, id={}", duringStop, td.getId());
                return true;
            }
        }

        final int duringExec = td.getDuringExec();
        if (duringExec > 0 && duringExec <= td.getSumExec()) {
            log.info("skip task for duringExec={}, sumExec={}", duringExec, td.getSumExec());
            return true;
        }

        final int duringDone = td.getDuringDone();
        if (duringDone > 0 && duringDone <= td.getSumDone()) {
            log.info("skip task for duringDone={}, sumDone={}", duringDone, td.getSumDone());
            return true;
        }

        final int duringFail = td.getDuringFail();
        if (duringFail > 0 && duringFail <= td.getDurFail()) {
            log.info("skip task for duringFail={}, durFail={}", duringFail, td.getDurFail());
            return true;
        }

        return false;
    }

    public enum Jane {
        SaveNextExec,
        SaveResult
    }
}
