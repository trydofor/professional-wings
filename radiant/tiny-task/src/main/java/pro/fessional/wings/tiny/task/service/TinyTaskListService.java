package pro.fessional.wings.tiny.task.service;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.page.PageQuery;
import pro.fessional.mirana.page.PageResult;
import pro.fessional.wings.tiny.task.database.autogen.tables.pojos.WinTaskResult;

/**
 * @author trydofor
 * @since 2022-12-26
 */
public interface TinyTaskListService {

    @NotNull
    PageResult<Item> listRunning(PageQuery pq);

    @NotNull
    PageResult<Item> listDefined(PageQuery pq);

    @NotNull
    PageResult<WinTaskResult> listResult(long id, PageQuery pq);

    @Data
    class Item {
        private long id;
        private boolean enabled;
        private boolean autorun;
        private int version;
        private String taskerName;
        private String taskerApps;
        private String taskerRuns;
        private String timingCron;
        private int timingIdle;
        private int timingRate;
        private String lastExec;
        private String lastFail;
        private String lastDone;
        private int sumExec;
        private int sumFail;
        private int sumDone;
    }
}
