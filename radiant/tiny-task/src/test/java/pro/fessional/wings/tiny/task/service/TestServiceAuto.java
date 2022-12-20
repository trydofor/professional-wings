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

    @TinyTasker(cron = "0 0 * * * *")
    public String strStr(String msg) {
        log.info("TestServiceAuto.strStr {}", msg);
        return msg;
    }

    @TinyTasker(rate = 60)
    public void strVoid(String msg) {
        log.info("TestServiceAuto.strVoid {}", msg);
    }

    @TinyTasker(value = "voidStrAuto", idle = 60)
    public void voidStr(String msg) {
        log.info("TestServiceAuto.voidStr {}", msg);
    }

    @TinyTasker(value = "voidVoidAuto", cron = "0 0 * * * *")
    public void voidVoid() {
        log.info("TestServiceAuto.voidVoid");
    }
}
