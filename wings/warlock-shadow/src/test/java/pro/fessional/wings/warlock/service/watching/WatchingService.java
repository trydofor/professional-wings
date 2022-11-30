package pro.fessional.wings.warlock.service.watching;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pro.fessional.mirana.best.DummyBlock;
import pro.fessional.mirana.time.StopWatch;
import pro.fessional.mirana.time.StopWatch.Watch;
import pro.fessional.wings.silencer.watch.Watches;
import pro.fessional.wings.silencer.watch.Watching;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.slardar.context.TerminalContext.Context;
import pro.fessional.wings.warlock.database.autogen.tables.WinConfRuntimeTable;
import pro.fessional.wings.warlock.database.autogen.tables.daos.WinConfRuntimeDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author trydofor
 * @since 2022-11-22
 */
@Service
@Slf4j
public class WatchingService {

    public static List<Watch> AsyncWatch = Collections.emptyList();
    public static StopWatch WatchOwner = null;
    public static Context AsyncContext = null;


    @Setter(onMethod_ = {@Autowired})
    protected WinConfRuntimeDao winConfRuntimeDao;

    @Watching
    public void normalFetch() {
        winConfRuntimeDao.fetchByKey("");
    }

    @Watching
    public void errorFetch() {
        final WinConfRuntimeTable t = winConfRuntimeDao.getTable();
        winConfRuntimeDao.ctx()
                         .selectFrom(t)
                         .where("aaa=bbb")
                         .fetch();
    }

    @Async
    public void asyncWatch() {
        final StopWatch stopWatch = Watches.acquire();
        try (Watch watch = stopWatch.start("AsyncWatch.BadSelect")) {
            WatchOwner = watch.owner;
            AsyncWatch = new ArrayList<>(WatchOwner.getWatches());
            log.warn("AsyncWatch={}", WatchOwner);
            try (Watch w0 = stopWatch.start("AsyncWatch.BadSelect.sleep")) {
                Thread.sleep(500);
            }
            catch (InterruptedException e) {
                DummyBlock.ignore(e);
            }
        }
    }

    @Async
    public void asyncTerminal() {
        AsyncContext = TerminalContext.get();
        log.warn("asyncTerminal={}", AsyncContext);
    }

    public void logTerminal() {
        log.warn("logTerminal={}", TerminalContext.get());
    }
}
