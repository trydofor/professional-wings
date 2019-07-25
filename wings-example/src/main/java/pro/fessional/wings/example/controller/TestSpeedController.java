package pro.fessional.wings.example.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author trydofor
 * @since 2019-06-30
 */

@Controller
@AllArgsConstructor
@Slf4j
public class TestSpeedController {

    @RequestMapping("/speed.html")
    @ResponseBody
    public String speed() {
        return "speed";
    }
}
