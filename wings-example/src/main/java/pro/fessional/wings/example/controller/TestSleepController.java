package pro.fessional.wings.example.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author trydofor
 * @since 2019-06-30
 */

@Controller
@AllArgsConstructor
@Slf4j
public class TestSleepController {

    @RequestMapping("/sleep.html")
    public String sleep(@RequestParam("ms") long ms) {
        long to = System.currentTimeMillis() + ms;
        while (System.currentTimeMillis() < to) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                // ignore
            }
        }

        return "index";
    }
}
