package pro.fessional.wings.tiny.app.service;

import lombok.extern.slf4j.Slf4j;
import pro.fessional.mirana.time.Sleep;
import pro.fessional.wings.tiny.task.schedule.TinyTasker;

/**
 * @author trydofor
 * @since 2022-12-14
 */
@TinyTasker.Auto
@Slf4j
public class TestServiceAuto {

    @TinyTasker(cron = "0 * * * * *")
    public String strStr(String msg) {
        log.info("TestServiceAuto.strStr {}", msg);
        Sleep.ignoreInterrupt(1500);
        return msg;
    }

    @TinyTasker(rate = 60)// use prop value, 30
    public void strVoid(String msg) {
        log.info("TestServiceAuto.strVoid {}", msg);
        Sleep.ignoreInterrupt(1500);
    }

    @TinyTasker(value = "voidStrAuto", idle = 60) // use annotation value
    public void voidStr(String msg) {
        log.info("TestServiceAuto.voidStr {}", msg);
        Sleep.ignoreInterrupt(1500);
    }

    @TinyTasker(value = "voidVoidAuto", cron = "0 * * * * *") // use prop value, enabled=false
    public void voidVoid() {
        log.info("TestServiceAuto.voidVoid");
        Sleep.ignoreInterrupt(1500);
    }
}
