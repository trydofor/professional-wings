package pro.fessional.wings.warlock.controller.other;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author trydofor
 * @since 2022-10-31
 */
@RestController
@Slf4j
public class TestParam1Controller {

    @PostMapping("/test/param1/str.json")
    public String str(@RequestBody String data) {
        return data;
    }

    @PostMapping("/test/param1/int.json")
    public Integer str(@RequestBody Integer data) {
        return data;
    }

    @PostMapping("/test/param1/bol.json")
    public Boolean str(@RequestBody Boolean data) {
        return data;
    }

    @PostMapping("/test/param1/ldt.json")
    public LocalDateTime str(@RequestBody LocalDateTime data) {
        return data;
    }

    @PostMapping("/test/param1/enu.json")
    public LogLevel str(@RequestBody LogLevel data) {
        return data;
    }

    @PostMapping("/test/param1/dec.json")
    public BigDecimal str(@RequestBody BigDecimal data) {
        return data;
    }
}
