package pro.fessional.wings.tiny.task.service;

import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.func.Lam;
import pro.fessional.wings.tiny.task.spring.prop.TinyTaskDefineProp;

/**
 * @author trydofor
 * @since 2022-12-13
 */
@SpringBootTest(properties = {
        "debug = true"
})
class TinyTaskServiceTest {

    @Setter(onMethod_ = {@Autowired})
    protected TinyTaskService tinyTaskService;

    @Setter(onMethod_ = {@Autowired})
    protected TinyTaskDefineProp tinyTaskDefineProp;

    @Test
    void schedule() {
//        TableCudListener.WarnVisit =true;

        TestServiceManual bean = new TestServiceManual();
        tinyTaskService.schedule(Lam.ref(bean::strStr), "trydofor test string");
    }

    @Test
    @Disabled
    void sleep180() throws InterruptedException {
        Thread.sleep(180*1000L);
    }
}
