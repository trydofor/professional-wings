package pro.fessional.wings.tiny.task.service.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronExpression;
import pro.fessional.mirana.time.DateLocaling;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.faceless.convention.EmptySugar;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.tiny.task.database.autogen.tables.WinTaskDefineTable;
import pro.fessional.wings.tiny.task.database.autogen.tables.WinTaskResultTable;
import pro.fessional.wings.tiny.task.database.autogen.tables.daos.WinTaskDefineDao;
import pro.fessional.wings.tiny.task.database.autogen.tables.daos.WinTaskResultDao;
import pro.fessional.wings.tiny.task.database.autogen.tables.pojos.WinTaskDefine;
import pro.fessional.wings.tiny.task.schedule.TinyTasker;
import pro.fessional.wings.tiny.task.service.TinyTaskBeatService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author trydofor
 * @since 2022-12-26
 */
@ConditionalWingsEnabled
@TinyTasker.Auto
@Slf4j
public class TinyTaskBeatServiceImpl implements TinyTaskBeatService {

    @Setter(onMethod_ = { @Autowired })
    protected WinTaskDefineDao winTaskDefineDao;

    @Setter(onMethod_ = { @Autowired })
    protected WinTaskResultDao winTaskResultDao;

    @Setter @Getter
    protected int beatTimes = 2;

    private volatile boolean warmed = false;
    private final HashMap<Long, LocalDateTime> nonLastExec = new HashMap<>();

    @Override
    @TinyTasker("TinyTaskCleanResult")
    public int cleanResult() {
        final WinTaskResultTable tr = winTaskResultDao.getTable();

        final List<Long> tid = winTaskResultDao
            .ctx()
            .selectDistinct(tr.TaskId)
            .from(tr)
            .fetchInto(Long.class);
        if (tid.isEmpty()) {
            log.debug("no task result to clean");
            return 0;
        }

        final WinTaskDefineTable td = winTaskDefineDao.getTable();
        final Result<Record2<Long, Integer>> hst = winTaskDefineDao
            .ctx()
            .select(td.Id, td.ResultKeep)
            .from(td)
            .where(td.Id.in(tid))
            .fetch();

        final LocalDateTime now = ThreadNow.localDate().atStartOfDay();
        final List<Condition> cond = hst
            .stream()
            .map(it -> tr.TaskId.eq(it.value1()).and(tr.TimeExec.le(now.minusDays(it.value2()))))
            .collect(Collectors.toList());

        if (cond.isEmpty()) {
            log.debug("no task condition to clean");
            return 0;
        }

        final int rc = winTaskResultDao
            .ctx()
            .delete(tr)
            .where(DSL.or(cond))
            .execute();
        log.info("clean task result, count={}", rc);

        return rc;
    }

    @Override
    @TinyTasker("TinyTaskCheckHealth")
    public String checkHealth() {
        final long now = ThreadNow.millis();
        final WinTaskDefineTable td = winTaskDefineDao.getTable();
        List<WinTaskDefine> tks = winTaskDefineDao
            .ctx()
            .select(td.Id, td.TaskerName, td.LastExec,
                td.TimingBeat, td.TimingRate, td.TimingIdle, td.TimingTune, td.TimingCron, td.TimingZone)
            .from(td)
            .where(td.Enabled.eq(Boolean.TRUE).and(td.TimingBeat.ge(0)))
            .fetch()
            .into(WinTaskDefine.class);

        final StringBuilder mis = warmed ? new StringBuilder() : null;
        for (WinTaskDefine r : tks) {
            log.debug("check health task id={}, name={}", r.getId(), r.getTaskerName());
            // coordinate to system timezone
            long beat = calcBeatMills(r, now);
            if (beat <= 0) continue;

            if (beat < now) {
                log.info("misfired task id={}, name={}", r.getId(), r.getTaskerName());
                if (mis != null) {
                    mis.append(r.getId()).append('@').append(r.getTaskerName()).append('\n');
                }
            }
        }

        warmed = true;
        return mis == null || mis.isEmpty() ? null : "misfired task id@name\n" + mis;
    }

    private long calcBeatMills(WinTaskDefine td, long now) {
        // no previous
        LocalDateTime lastExec = td.getLastExec();
        if (EmptySugar.asEmptyValue(lastExec)) {
            lastExec = nonLastExec.computeIfAbsent(td.getId(), ignore -> DateLocaling.sysLdt(now));
        }

        final long beat = td.getTimingBeat();
        if (beat < 0) return beat;

        final long lastExecSys = DateLocaling.sysEpoch(lastExec);
        if (beat > 0) return lastExecSys + beat * 1000L;

        final String cron = td.getTimingCron();
        if (StringUtils.isEmpty(cron)) {
            int max = Math.max(td.getTimingRate(), td.getTimingIdle());
            return lastExecSys + max * 1000L * beatTimes;
        }

        // cron at specified zone
        final String zid = td.getTimingZone();
        final ZoneId zone = StringUtils.isEmpty(zid) ? ThreadNow.sysZoneId() : ZoneId.of(zid);
        ZonedDateTime beatZdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(lastExecSys), zone);

        final CronExpression cronExpr = CronExpression.parse(cron);
        for (int i = 0; i < beatTimes; i++) {
            beatZdt = cronExpr.next(beatZdt);
        }

        // then convert to instance
        return beatZdt.toInstant().toEpochMilli();
    }
}
