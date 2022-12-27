package pro.fessional.wings.tiny.task.service.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.tiny.task.database.autogen.tables.WinTaskDefineTable;
import pro.fessional.wings.tiny.task.database.autogen.tables.WinTaskResultTable;
import pro.fessional.wings.tiny.task.database.autogen.tables.daos.WinTaskDefineDao;
import pro.fessional.wings.tiny.task.database.autogen.tables.daos.WinTaskResultDao;
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
        final long now = ThreadNow.millis() - 120_000L;
        final WinTaskDefineTable td = winTaskDefineDao.getTable();
        final Result<Record3<Long, String, Long>> r3 = winTaskDefineDao
                .ctx()
                .select(td.Id, td.TaskerName, td.NextExec)
                .from(td)
                .where(td.Enabled.eq(Boolean.TRUE))
                .fetch();

        StringBuilder msg1 = new StringBuilder("misfired:"); //
        StringBuilder msg2 = new StringBuilder(); // 0
        StringBuilder msg3 = new StringBuilder(); // > now

        for (Record3<Long, String, Long> r : r3) {
            final long nxt = r.value3();
            if (nxt > now) {
                msg3.append('\n').append(r.value1()).append('@').append(r.value2());
            }
            else if (nxt <= 0) {
                msg2.append('\n').append(r.value1()).append('@').append(r.value2());
            }
            else {
                msg1.append('\n').append(r.value1()).append('@').append(r.value2());
            }
        }

        if (msg2.length() > 0) {
            msg1.append("\n\nfinished:").append(msg2);
        }
        if (msg3.length() > 0) {
            msg1.append("\n\nplanning:").append(msg3);
        }
        return msg1.toString();
    }
}
