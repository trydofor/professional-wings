package com.moilioncircle.wings.devops.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.R;

/**
 * @author trydofor
 * @since 2021-04-05
 */
@RestController
@Slf4j
public class TestLoadController {

    @Data
    public static class Jn {
        private long busyMs = 0;
        private long sleepMs = 0;
        private long loopCnt = 0;
    }

    @Operation(summary = "test works. ms - response time; rt - block ratio[0-10], default 9")
    @GetMapping("/test/load/works.html")
    public R<Jn> sleep(@RequestParam("ms") int ms, @RequestParam(value = "br", required = false) Integer br) {
        final long per = ms / 10;
        final long biz = (br == null ? 1 : 10 - br) * per;
        final long end = System.currentTimeMillis() + biz;
        long cnt = 0;
        do {
            cnt++;
        } while (System.currentTimeMillis() < end);

        try {
            Thread.sleep(ms - biz);
        }
        catch (InterruptedException e) {
            cnt++;
        }

        final Jn jn = new Jn();
        jn.busyMs = biz;
        jn.sleepMs = ms - biz;
        jn.loopCnt = cnt;
        return R.ok(jn);
    }

    @Operation(summary = "test speed")
    @GetMapping({ "/test/load/speed.html", "/index.html" })
    public String speed() {
        return "speed";
    }
}
