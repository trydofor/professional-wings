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

    @TinyTasker
    public String strStr(String msg) {
        log.info("TestServiceAuto.strStr {}", msg);
        return msg;
    }

    @TinyTasker
    public void strVoid(String msg) {
        log.info("TestServiceAuto.strVoid {}", msg);
    }

    @TinyTasker("voidStrAuto")
    public void voidStr(String msg) {
        log.info("TestServiceAuto.voidStr {}", msg);
    }

    @TinyTasker("voidVoidAuto")
    public void voidVoid() {
        log.info("TestServiceAuto.voidVoid");
    }
}
