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
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.faceless.database.jooq.helper.PageJooqHelper;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.tiny.task.database.autogen.tables.WinTaskDefineTable;
import pro.fessional.wings.tiny.task.database.autogen.tables.WinTaskResultTable;
import pro.fessional.wings.tiny.task.database.autogen.tables.daos.WinTaskDefineDao;
import pro.fessional.wings.tiny.task.database.autogen.tables.daos.WinTaskResultDao;
import pro.fessional.wings.tiny.task.database.autogen.tables.pojos.WinTaskResult;
import pro.fessional.wings.tiny.task.service.TinyTaskExecService;
import pro.fessional.wings.tiny.task.service.TinyTaskListService;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;

/**
 * @author trydofor
 * @since 2022-12-26
 */
@Service
@ConditionalWingsEnabled
@Slf4j
public class TinyTaskListServiceImpl implements TinyTaskListService {

    @Setter(onMethod_ = { @Autowired })
    protected TinyTaskExecService tinyTaskExecService;

    @Setter(onMethod_ = { @Autowired })
    protected WinTaskDefineDao winTaskDefineDao;

    @Setter(onMethod_ = { @Autowired })
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
            .order(Map.of("exec", t.LastExec, "exit", t.LastExit), t.LastExit.desc())
            .fetch(t.Id, t.Enabled, t.Autorun, t.Version,
                t.TaskerName, t.TaskerApps, t.TaskerRuns,
                t.TimingCron, t.TimingIdle, t.TimingRate, t.TimingTune,
                t.LastExec, t.LastExit, t.LastFail,
                t.SumExec, t.SumFail, t.SumDone)
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
            .order(Map.of("exec", t.LastExec, "exit", t.LastExit), t.LastExit.desc())
            .fetch(t.Id, t.Enabled, t.Autorun, t.Version,
                t.TaskerName, t.TaskerApps, t.TaskerRuns,
                t.TimingCron, t.TimingIdle, t.TimingRate, t.TimingTune,
                t.LastExec, t.LastExit, t.LastFail,
                t.SumExec, t.SumFail, t.SumDone)
            .into(r17Item(t));
    }

    @NotNull
    private RecordMapper<Record17<Long, Boolean, Boolean, Integer, String, String, String, String, Integer, Integer, Integer, LocalDateTime, LocalDateTime, Boolean, Integer, Integer, Integer>, Item> r17Item(WinTaskDefineTable t) {
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
            tm.setTimingTune(it.get(t.TimingTune));

            tm.setSumExec(it.get(t.SumExec));
            tm.setSumFail(it.get(t.SumFail));
            tm.setSumDone(it.get(t.SumDone));


            final ZonedDateTime lastExec = it.get(t.LastExec).atZone(ThreadNow.sysZoneId());
            tm.setLastExec(DateFormatter.fullTz(lastExec));
            final ZonedDateTime lastExit = it.get(t.LastExit).atZone(ThreadNow.sysZoneId());
            tm.setLastExit(DateFormatter.fullTz(lastExit));
            tm.setLastFail(it.get(t.LastFail));
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
            .order(Map.of("id", t.Id), t.Id.desc())
            .fetch()
            .into(WinTaskResult.class);
    }
}
