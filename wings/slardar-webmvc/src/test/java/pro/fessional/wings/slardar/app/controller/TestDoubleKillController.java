package pro.fessional.wings.slardar.app.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.time.Sleep;
import pro.fessional.wings.slardar.app.service.TestDoubleKillService;
import pro.fessional.wings.slardar.concur.DoubleKill;

/**
 * @author trydofor
 * @since 2021-02-01
 */
@RestController
@Slf4j
public class TestDoubleKillController {

    @Setter(onMethod_ = {@Autowired})
    private TestDoubleKillService testDoubleKillService;

    @GetMapping("/test/double-kill.json")
    @DoubleKill(expression = "@httpSessionIdResolver.resolveSessionIds(#p0)")
    public R<String> doubleKill(HttpServletRequest requiredBySpel) {
        Sleep.ignoreInterrupt(10_000);
        log.info("just log uri={}", requiredBySpel.getRequestURI());
        return R.ok("login page");
    }

    @GetMapping("/test/double-kill-async.json")
    public R<String> doubleKillAsync() {
        return R.ok(testDoubleKillService.sleepCache("controller", 10));
    }
}
