package pro.fessional.wings.tiny.task.service;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.func.Lam;
import pro.fessional.mirana.time.Sleep;
import pro.fessional.wings.tiny.app.service.TestServiceManual;
import pro.fessional.wings.tiny.task.schedule.conf.TaskerProp;
import pro.fessional.wings.tiny.task.spring.prop.TinyTaskDefineProp;

import static pro.fessional.wings.tiny.task.service.TinyTaskServiceTest.autorun;
import static pro.fessional.wings.tiny.task.service.TinyTaskServiceTest.enabled;
import static pro.fessional.wings.tiny.task.service.TinyTaskServiceTest.timingCron;

/**
 * @author trydofor
 * @since 2022-12-13
 */
@SpringBootTest(properties = {
        "wings.tiny.task.define[pro.fessional.wings.tiny.app.service.TestServiceManual#strStr].enabled=" + enabled,
        "wings.tiny.task.define[pro.fessional.wings.tiny.app.service.TestServiceManual#strStr].autorun=" + autorun,
        "wings.tiny.task.define[pro.fessional.wings.tiny.app.service.TestServiceManual#strStr].timing-cron=" + timingCron,
        "wings.tiny.task.define[pro.fessional.wings.tiny.app.service.TestServiceManual#strStr].version=1",
//        "wings.tiny.task.enabled.dryrun=true",
        "wings.slardar.ding-notice.default.access-token="
})
@DependsOnDatabaseInitialization
class TinyTaskServiceTest {

    public static final boolean enabled = false;
    public static final boolean autorun = false;
    public static final String timingCron = "0 1 2 3 4 5";

    @Setter(onMethod_ = {@Autowired})
    protected TinyTaskService tinyTaskService;

    @Setter(onMethod_ = {@Autowired})
    protected TinyTaskConfService tinyTaskConfService;

    @Setter(onMethod_ = {@Autowired})
    protected TinyTaskDefineProp tinyTaskDefineProp;

    @Test
    @TmsLink("C15013")
    void scheduleLambdaRef() {
        TestServiceManual bean = new TestServiceManual();
        final TinyTaskService.Task task = tinyTaskService.schedule(Lam.ref(bean::strStr), "trydofor test string");
        final TaskerProp prop = tinyTaskConfService.database(task.getId(), true);
        Assertions.assertEquals("\"trydofor test string\"", prop.getTaskerPara());
        Assertions.assertEquals("TestServiceManual#strStr", prop.getTaskerName());
        Assertions.assertEquals("pro.fessional.wings.tiny.app.service.TestServiceManual#strStr", prop.getTaskerBean());
        Assertions.assertEquals(timingCron, prop.getTimingCron());
        Assertions.assertEquals(enabled, prop.isEnabled(), "should use higher version, conf=1,db=0");
        Assertions.assertEquals(autorun, prop.isAutorun());
    }
}
