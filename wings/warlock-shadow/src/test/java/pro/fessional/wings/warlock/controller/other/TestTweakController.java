package pro.fessional.wings.warlock.controller.other;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.pain.CodeException;
import pro.fessional.wings.slardar.context.Now;

import java.time.LocalDateTime;

/**
 * @author trydofor
 * @since 2022-10-31
 */
@RestController
@Slf4j
public class TestTweakController {

    @GetMapping("/test/tweak/logger-level.json")
    public R<Void> loggerLevel() {
        log.trace("loggerLevel >>>>> trace");
        log.debug("loggerLevel >>>>> debug");
        log.info("loggerLevel >>>>> info");
        log.warn("loggerLevel >>>>> warn");
        log.error("loggerLevel >>>>> error");
        return R.OK;
    }

    @GetMapping("/test/tweak/code-stack.json")
    public R<Void> codeStack() {
        log.error("codeStack >>>>>", new CodeException("test code"));
        return R.OK;
    }

    @GetMapping("/test/tweak/clock-now.json")
    public R<LocalDateTime> clockNow() {
        final LocalDateTime ldt = Now.localDateTime();
        log.warn("clockNow >>>>> ldt={}", ldt);
        return R.okData(ldt);
    }
}
