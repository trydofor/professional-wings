package pro.fessional.wings.tiny.task.service.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Service;
import pro.fessional.mirana.cast.BoxedCastUtil;
import pro.fessional.mirana.pain.ThrowableUtil;
import pro.fessional.mirana.time.DateParser;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.faceless.convention.EmptyValue;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import static pro.fessional.wings.tiny.task.schedule.exec.NoticeExec.WhenDone;
import static pro.fessional.wings.tiny.task.schedule.exec.NoticeExec.WhenExec;
import static pro.fessional.wings.tiny.task.schedule.exec.NoticeExec.WhenFail;

/**
 * @author trydofor
 * @since 2022-12-21
 */
@Service
@Slf4j
public class TinyTaskExecServiceImpl implements TinyTaskExecService {

    protected static final ConcurrentHashMap<Long, ScheduledFuture<?>> Handle = new ConcurrentHashMap<>();

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

    @Override
    public boolean launch(long id) {
        final WinTaskDefine td = winTaskDefineDao.fetchOneById(id);
        if (td == null) return false;

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
        final ScheduledFuture<?> handle = TaskSchedulerHelper.referScheduler(fast).schedule(() -> {
            long execTms = ThreadNow.millis();
            long doneTms = -1;
            long failTms = -1;
            String taskMsg = null;

            final TaskerExec tasker = ExecHolder.getTasker(td.getTaskerBean(), false);

            final NoticeExec<?> notice = ExecHolder.getNotice(td.getNoticeBean(), false);
            final Set<String> ntcWhen = notice == null ? Collections.emptySet() : noticeWhen(td.getNoticeWhen());

            final String taskerName = td.getTaskerName();
            final String noticeConf = td.getNoticeConf();

            postNotice(notice, noticeConf, ntcWhen, taskerName, taskMsg, execTms, WhenExec);
            try {
                log.info("task exec, id={}", id);
                final Object result = tasker.invoke(td.getTaskerPara(), true);
                log.info("task done, id={}", id);
                //
                doneTms = ThreadNow.millis();
                taskMsg = stringResult(result);
                postNotice(notice, noticeConf, ntcWhen, taskerName, taskMsg, doneTms, WhenDone);
            }
            catch (Exception e) {
                log.warn("task fail, id=" + id, e);
                failTms = ThreadNow.millis();
                taskMsg = ThrowableUtil.toString(e);
                postNotice(notice, noticeConf, ntcWhen, taskerName, taskMsg, failTms, WhenFail);
            }
            finally {
                saveResult(id, execTms, failTms, doneTms, taskMsg, td.getCoreFail());
                checkLimit(id, doneTms, failTms, td);

                if (Handle.containsKey(id)) { // 未被取消
                    launch(id); // next
                }
            }
        }, new Date(next));

        //
        Handle.put(id, handle);
        return true;
    }

    @Override
    public boolean cancel(long id) {
        final ScheduledFuture<?> ft = Handle.get(id);
        if (ft == null) return true;
        final boolean r = ft.cancel(false);
        if (r) {
            Handle.remove(id);
        }
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

        for (String s : StringUtils.split(apps, ',')) {
            if (s.trim().equals(appName)) return false;
        }
        log.info("skip task for not apps={}, cur={}, id={}", apps, appName, id);
        return true;
    }

    private boolean notRuns(String runs, long id) {
        if (StringUtils.isEmpty(runs)) return false;

        final String rm = RuntimeMode.getRunMode().name();
        for (String s : StringUtils.split(runs, ',')) {
            if (s.trim().equalsIgnoreCase(rm)) return false;
        }
        log.info("skip task for not runs={}, cur={}, id={}", runs, rm, id);
        return true;
    }

    private Set<String> noticeWhen(String nw) {
        if (nw == null || nw.isEmpty()) return Collections.emptySet();
        Set<String> rs = new HashSet<>();
        for (String s : StringUtils.split(nw, ',')) {
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

    private void postNotice(NoticeExec<?> ntc, String cnf, Set<String> whs, String tn, String msg, long ms, String wh) {
        if (ntc == null || !whs.contains(wh)) return;
        final String zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(ms), ZoneId.systemDefault()).toString();
        ntc.postNotice(cnf, tn + " " + wh.toUpperCase(), msg == null ? zdt : zdt + "\n\n" + msg);
    }

    private void saveNextExec(long next, WinTaskDefine td) {

        journalService.commit(Jane.SaveNextExec, journal -> {
            final WinTaskDefineTable t = winTaskDefineDao.getTable();
            winTaskDefineDao.ctx().update(t)
                            .set(t.CommitId, journal.getCommitId())
                            .set(t.ModifyDt, journal.getCommitDt())
                            .set(t.NextExec, next)
                            .set(t.NextMiss, next + td.getTimingMiss() * 1000L)
                            .set(t.NextLock, next)
                            .where(t.Id.eq(td.getId()))
                            .execute();
        });
    }

    private void saveResult(Long id, long exec, long fail, long done, String msg, int cf) {

        final WinTaskDefineTable td = winTaskDefineDao.getTable();
        Map<Field<?>, Object> setter = new HashMap<>();

        setter.put(td.LastExec, exec);
        setter.put(td.SumsExec, td.SumsExec.add(1));
        if (fail > 0) {
            setter.put(td.LastFail, fail);
            setter.put(td.SumsFail, td.SumsFail.add(1));
        }
        if (done > 0) {
            setter.put(td.LastDone, done);
            setter.put(td.SumsDone, td.SumsDone.add(1));
        }
        if (cf > 0) {
            setter.put(td.CoreFail, fail > 0 ? td.CoreFail.add(1) : 0);
        }

        final WinTaskResult po = new WinTaskResult();
        po.setId(lightIdService.getId(winTaskResultDao.getTable()));
        po.setTaskId(id);
        po.setTaskApp(appName);
        po.setTaskMsg(msg);

        po.setTimeExec(milliLdt(exec));
        po.setTimeFail(milliLdt(fail));
        po.setTimeDone(milliLdt(done));
        po.setTimeCost(Math.max(done, fail) - exec);

        journalService.commit(Jane.SaveResult, journal -> {
            //
            setter.put(td.CommitId, journal.getCommitId());
            setter.put(td.ModifyDt, journal.getCommitDt());
            winTaskDefineDao.ctx().update(td)
                            .set(setter)
                            .where(td.Id.eq(id))
                            .execute();

            // history
            winTaskResultDao.insert(po);
        });
    }

    private LocalDateTime milliLdt(long m) {
        if (m < 0) return EmptyValue.DATE_TIME;
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(m), ZoneId.systemDefault());
    }

    private void checkLimit(long id, long doneTms, long failTms, WinTaskDefine td) {
        final int duringExec = td.getDuringExec();
        final int sumsExec = td.getSumsExec();
        if (duringExec > 0 && duringExec <= sumsExec + 1) {
            log.info("remove task for duringExec={}, sumsExec={}, id={}", duringExec, sumsExec, id);
            Handle.remove(id);
            return;
        }

        final int duringDone = td.getDuringDone();
        final int sumsDone = td.getSumsDone();
        if (duringDone > 0 && duringDone <= (doneTms < 0 ? sumsDone : sumsDone + 1)) {
            log.info("remove task for duringDone={}, sumsDone={}, id={}", duringDone, sumsDone, id);
            Handle.remove(id);
            return;
        }

        final int duringFail = td.getDuringFail();
        final int coreFail = td.getCoreFail();
        if (duringFail > 0 && duringFail <= (failTms < 0 ? coreFail : coreFail + 1)) {
            log.info("remove task for duringFail={}, coreFail={}, id={}", duringFail, coreFail, id);
            Handle.remove(id);
        }
    }

    private long calcNextExec(WinTaskDefine td) {
        final String zid = td.getTimingZone();
        final ZoneId zone = StringUtils.isEmpty(zid) ? ZoneId.systemDefault() : ZoneId.of(zid);

        final long now = ThreadNow.millis();
        if (notRanged(td, zone, now)) return -1;

        final Trigger trigger = makeTrigger(td, zone);
        TriggerContext context = makeContext(td, zone, now);

        final Date next = trigger.nextExecutionTime(context);
        if (next == null) {
            log.info("skip task for trigger not fire, id={}", td.getId());
            return -1;
        }

        final long nxt = next.toInstant().toEpochMilli();
        if (nxt < now && nxt + td.getTimingMiss() * 1000L < now) {
            log.info("skip task for misfire, id={}", td.getId());
            return -1;
        }

        return nxt;
    }

    @SuppressWarnings("all")
    private TriggerContext makeContext(WinTaskDefine td, ZoneId zone, long now) {

        Date lastActual = null;
        final long lastExec = td.getLastExec();
        if (lastExec > 0) {
            lastActual = new Date(lastExec);
        }

        Date lastScheduled = null;
        final long nextExec = td.getNextExec();
        if (nextExec > 0) {
            lastScheduled = new Date(nextExec);
        }

        Date lastCompletion = null;
        final long lastDone = td.getLastDone();
        if (lastDone > 0) {
            lastCompletion = new Date(lastDone);
        }

        return new SimpleTriggerContext(lastScheduled, lastActual, lastCompletion);
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
            PeriodicTrigger trg = new PeriodicTrigger(idle * 1000L);
            trg.setFixedRate(false);
            return trg;
        }

        final int rate = td.getTimingRate();
        if (rate > 0) {
            log.info("use trigger rate={}, id={}", rate, td.getId());
            PeriodicTrigger trg = new PeriodicTrigger(rate * 1000L);
            trg.setFixedRate(true);
            return trg;
        }

        throw new IllegalArgumentException("no cron/idle/rate to make trigger");
    }

    private boolean notRanged(WinTaskDefine td, ZoneId zone, long now) {
        final String duringFrom = td.getDuringFrom();
        if (StringUtils.isNotEmpty(duringFrom)) {
            final LocalDateTime ldt = DateParser.parseDateTime(duringFrom);
            final long ms = ldt.atZone(zone).toInstant().toEpochMilli();
            if (ms > now) {
                log.info("skip task for duringFrom={}, id={}", duringFrom, td.getId());
                return true;
            }
        }

        final String duringStop = td.getDuringStop();
        if (StringUtils.isNotEmpty(duringStop)) {
            final LocalDateTime ldt = DateParser.parseDateTime(duringStop);
            final long ms = ldt.atZone(zone).toInstant().toEpochMilli();
            if (ms < now) {
                log.info("skip task for duringStop={}, id={}", duringStop, td.getId());
                return true;
            }
        }

        if (now < td.getNextLock()) {
            log.info("skip task for nextLock={}, id={}", td.getNextLock(), td.getId());
            return true;
        }

        final int duringExec = td.getDuringExec();
        if (duringExec > 0 && duringExec <= td.getSumsExec()) {
            log.info("skip task for duringExec={}, SumsExec={}", duringExec, td.getSumsExec());
            return true;
        }

        final int duringDone = td.getDuringDone();
        if (duringDone > 0 && duringDone <= td.getSumsDone()) {
            log.info("skip task for duringDone={}, SumsDone={}", duringDone, td.getSumsDone());
            return true;
        }

        final int duringFail = td.getDuringFail();
        if (duringFail > 0 && duringFail <= td.getCoreFail()) {
            log.info("skip task for duringFail={}, CoreFail={}", duringFail, td.getCoreFail());
            return true;
        }

        return false;
    }

    public enum Jane {
        SaveNextExec,
        SaveResult
    }
}
