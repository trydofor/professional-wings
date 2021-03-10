package pro.fessional.wings.slardar.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.slardar.concur.DoubleKill;

import javax.servlet.http.HttpServletRequest;

/**
 * @author trydofor
 * @since 2021-02-01
 */
@RestController
public class TestDoubleKillController {

    @GetMapping("/test/double-kill.json")
    @DoubleKill(expression = "@httpSessionIdResolver.resolveSessionIds(#p0)")
    public R<String> login(HttpServletRequest request) throws InterruptedException {
        Thread.sleep(10_000);
        return R.ok("login page");
    }
}
