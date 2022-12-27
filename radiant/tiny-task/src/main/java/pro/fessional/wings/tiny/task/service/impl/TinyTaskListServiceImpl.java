package pro.fessional.wings.tiny.task.service.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record17;
import org.jooq.RecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.fessional.mirana.page.PageQuery;
import pro.fessional.mirana.page.PageResult;
import pro.fessional.mirana.time.DateFormatter;
import pro.fessional.wings.faceless.database.jooq.helper.PageJooqHelper;
import pro.fessional.wings.tiny.task.database.autogen.tables.WinTaskDefineTable;
import pro.fessional.wings.tiny.task.database.autogen.tables.WinTaskResultTable;
import pro.fessional.wings.tiny.task.database.autogen.tables.daos.WinTaskDefineDao;
import pro.fessional.wings.tiny.task.database.autogen.tables.daos.WinTaskResultDao;
import pro.fessional.wings.tiny.task.database.autogen.tables.pojos.WinTaskResult;
import pro.fessional.wings.tiny.task.service.TinyTaskExecService;
import pro.fessional.wings.tiny.task.service.TinyTaskListService;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

/**
 * @author trydofor
 * @since 2022-12-26
 */
@Service
@Slf4j
public class TinyTaskListServiceImpl implements TinyTaskListService {

    @Setter(onMethod_ = {@Autowired})
    protected TinyTaskExecService tinyTaskExecService;

    @Setter(onMethod_ = {@Autowired})
    protected WinTaskDefineDao winTaskDefineDao;

    @Setter(onMethod_ = {@Autowired})
    protected WinTaskResultDao winTaskResultDao;

    @Override
    @NotNull
    public PageResult<Item> listRunning(PageQuery pq) {
        final Set<Long> ids = tinyTaskExecService.running();
        final WinTaskDefineTable t = winTaskDefineDao.getTable();
        return PageJooqHelper
                .use(winTaskDefineDao, pq)
                .count()
                .from(t)
                .where(t.Id.in(ids))
                .order(t.Id)
                .fetch(t.Id, t.Enabled, t.Autorun, t.Version,
                        t.TaskerName, t.TaskerApps, t.TaskerRuns,
                        t.TimingCron, t.TimingIdle, t.TimingRate, t.TimingZone,
                        t.LastExec, t.LastFail, t.LastDone,
                        t.SumsExec, t.SumsFail, t.SumsDone)
                .into(r17Item(t));
    }

    @Override
    @NotNull
    public PageResult<Item> listDefined(PageQuery pq) {
        final WinTaskDefineTable t = winTaskDefineDao.getTable();
        return PageJooqHelper
                .use(winTaskDefineDao, pq)
                .count()
                .from(t)
                .whereTrue()
                .order(t.Id)
                .fetch(t.Id, t.Enabled, t.Autorun, t.Version,
                        t.TaskerName, t.TaskerApps, t.TaskerRuns,
                        t.TimingCron, t.TimingIdle, t.TimingRate, t.TimingZone,
                        t.LastExec, t.LastFail, t.LastDone,
                        t.SumsExec, t.SumsFail, t.SumsDone)
                .into(r17Item(t));
    }

    @NotNull
    private RecordMapper<Record17<Long, Boolean, Boolean, Integer, String, String, String, String, Integer, Integer, String, Long, Long, Long, Integer, Integer, Integer>, Item> r17Item(WinTaskDefineTable t) {
        return it -> {
            Item tm = new Item();
            tm.setId(it.get(t.Id));
            tm.setEnabled(it.get(t.Enabled));
            tm.setAutorun(it.get(t.Enabled));
            tm.setVersion(it.get(t.Version));
            tm.setTaskerName(it.get(t.TaskerName));
            tm.setTaskerApps(it.get(t.TaskerApps));
            tm.setTaskerRuns(it.get(t.TaskerRuns));
            tm.setTimingCron(it.get(t.TimingCron));
            tm.setTimingIdle(it.get(t.TimingIdle));
            tm.setTimingRate(it.get(t.TimingRate));
            tm.setSumsExec(it.get(t.SumsExec));
            tm.setSumsFail(it.get(t.SumsFail));
            tm.setSumsDone(it.get(t.SumsDone));
            final String tz = it.get(t.TimingZone);

            final ZoneId zid = tz.isEmpty() ? ZoneId.systemDefault() : ZoneId.of(tz);

            ZonedDateTime.ofInstant(Instant.ofEpochMilli(it.get(t.LastExec)), zid);
            final ZonedDateTime lastExec = ZonedDateTime.ofInstant(Instant.ofEpochMilli(it.get(t.LastExec)), zid);
            tm.setLastExec(DateFormatter.fullTz(lastExec));
            final ZonedDateTime lastFail = ZonedDateTime.ofInstant(Instant.ofEpochMilli(it.get(t.LastFail)), zid);
            tm.setLastFail(DateFormatter.fullTz(lastFail));
            final ZonedDateTime lastDone = ZonedDateTime.ofInstant(Instant.ofEpochMilli(it.get(t.LastDone)), zid);
            tm.setLastDone(DateFormatter.fullTz(lastDone));
            return tm;
        };
    }

    @Override
    @NotNull
    public PageResult<WinTaskResult> listResult(long id, PageQuery pq) {
        final WinTaskResultTable t = winTaskResultDao.getTable();
        return PageJooqHelper
                .use(winTaskResultDao, pq)
                .count()
                .from(t)
                .where(t.TaskId.eq(id))
                .order(t.Id)
                .fetch()
                .into(WinTaskResult.class);
    }
}
