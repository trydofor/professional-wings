package pro.fessional.wings.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

/**
 * @author trydofor
 * @since 2019-06-30
 */

@Controller
@Slf4j
@RequiredArgsConstructor
public class TestSleepController {

    private final Random unsafeRandom = new Random();

    @RequestMapping("/test/sleep.html")
    public String sleep(@RequestParam("ms") long ms) {
        long half = ms / 2;
        long slp = ((long) (unsafeRandom.nextDouble() * half)) + half;
        try {
            Thread.sleep(slp);
        } catch (InterruptedException e) {
            // ingore
        }

        return "index";
    }
}
