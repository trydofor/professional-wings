package pro.fessional.wings.tiny.task.service;

import lombok.extern.slf4j.Slf4j;
import pro.fessional.wings.tiny.task.schedule.TinyTasker;

/**
 * @author trydofor
 * @since 2022-12-14
 */
@Slf4j
public class TestServiceManual {
    @TinyTasker
    public String strStr(String msg) {
        log.info("TestServiceManual.strStr {}", msg);
        return msg;
    }

    @TinyTasker
    public void strVoid(String msg) {
        log.info("TestServiceManual.strVoid {}", msg);
    }

    @TinyTasker("voidStrManual")
    public void voidStr(String msg) {
        log.info("TestServiceManual.voidStr {}", msg);
    }

    @TinyTasker("voidVoidManual")
    public void voidVoid() {
        log.info("TestServiceManual.voidVoid");
    }
}
