package pro.fessional.wings.tiny.task.service.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.fessional.mirana.time.DateLocaling;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.tiny.task.database.autogen.tables.WinTaskDefineTable;
import pro.fessional.wings.tiny.task.database.autogen.tables.WinTaskResultTable;
import pro.fessional.wings.tiny.task.database.autogen.tables.daos.WinTaskDefineDao;
import pro.fessional.wings.tiny.task.database.autogen.tables.daos.WinTaskResultDao;
import pro.fessional.wings.tiny.task.database.autogen.tables.pojos.WinTaskDefine;
import pro.fessional.wings.tiny.task.schedule.TinyTasker;
import pro.fessional.wings.tiny.task.service.TinyTaskBeatService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author trydofor
 * @since 2022-12-26
 */
@Service
@TinyTasker.Auto
@Slf4j
public class TinyTaskBeatServiceImpl implements TinyTaskBeatService {

    @Setter(onMethod_ = {@Autowired})
    protected WinTaskDefineDao winTaskDefineDao;

    @Setter(onMethod_ = {@Autowired})
    protected WinTaskResultDao winTaskResultDao;

    @Setter @Getter
    protected int beatTimes = 2;

    private volatile boolean warmed = false;

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
            log.info("no task result to clean");
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
            log.info("no task condition to clean");
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
                        td.TimingBeat, td.TimingRate, td.TimingIdle)
                .from(td)
                .where(td.Enabled.eq(Boolean.TRUE))
                .fetch()
                .into(WinTaskDefine.class);

        final StringBuilder mis = new StringBuilder();

        for (WinTaskDefine r : tks) {
            log.info("check health task id={}, name={}", r.getId(), r.getTaskerName());
            int beat = r.getTimingBeat();
            if (beat <= 0) {
                beat = Math.max(r.getTimingRate(), r.getTimingIdle());
            }
            if (beat <= 0) continue;

            final long last = DateLocaling.sysEpoch(r.getLastExec());
            if (warmed && last + 1000L * beat * beatTimes  < now) {
                log.info("misfired task id={}, name={}", r.getId(), r.getTaskerName());
                mis.append(r.getId()).append('@').append(r.getTaskerName()).append('\n');
            }
        }

        warmed = true;
        return mis.isEmpty() ? null : "misfired task id@name\n" + mis;
    }
}
