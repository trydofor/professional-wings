package pro.fessional.wings.slardar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.fessional.mirana.time.DateFormatter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * @author trydofor
 * @since 2019-11-13
 */
@Controller
public class TestDateTimeController {

    @RequestMapping({"/test/datetime-util-date.html"})
    @ResponseBody
    public String utilDate(@RequestParam("d") Date date) {
        return DateFormatter.full23(date);
    }

    @RequestMapping({"/test/datetime-full-date.html"})
    @ResponseBody
    public String fullDate(@RequestParam("d") LocalDateTime date) {
        return DateFormatter.full23(date);
    }

    @RequestMapping({"/test/datetime-local-date.html"})
    @ResponseBody
    public String localDate(@RequestParam("d") LocalDate date) {
        return DateFormatter.date10(date);
    }

    @RequestMapping({"/test/datetime-local-time.html"})
    @ResponseBody
    public String localTime(@RequestParam("d") LocalTime date) {
        return DateFormatter.time12(date);
    }
}
