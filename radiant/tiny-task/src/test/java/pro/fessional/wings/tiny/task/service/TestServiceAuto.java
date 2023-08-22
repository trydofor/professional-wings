package pro.fessional.wings.tiny.task.service;

import lombok.extern.slf4j.Slf4j;
import pro.fessional.wings.tiny.task.schedule.TinyTasker;

/**
 * @author trydofor
 * @since 2022-12-14
 */
@TinyTasker.Auto
@Slf4j
public class TestServiceAuto {

    @TinyTasker(cron = "0 * * * * *")
    public String strStr(String msg) throws Exception {
        log.info("TestServiceAuto.strStr {}", msg);
        Thread.sleep(1500);
        return msg;
    }

    @TinyTasker(rate = 60)// use prop value, 30
    public void strVoid(String msg) throws Exception {
        log.info("TestServiceAuto.strVoid {}", msg);
        Thread.sleep(1500);
    }

    @TinyTasker(value = "voidStrAuto", idle = 60) // use annotation value
    public void voidStr(String msg) throws Exception {
        log.info("TestServiceAuto.voidStr {}", msg);
        Thread.sleep(1500);
    }

    @TinyTasker(value = "voidVoidAuto", cron = "0 * * * * *") // use prop value, enabled=false
    public void voidVoid() throws Exception {
        log.info("TestServiceAuto.voidVoid");
        Thread.sleep(1500);
    }
}
