package pro.fessional.wings.slardar.controller;

import lombok.Data;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.time.DateFormatter;
import pro.fessional.wings.faceless.enums.autogen.StandardTimezone;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

/**
 * @author trydofor
 * @since 2019-11-13
 */
@RestController
public class TestDateTimeController {

    @Data
    public static class Xdt {
        public ZonedDateTime zdt;
        public LocalDateTime ldt;
    }

    @Data
    public static class Ldt {
        public LocalDateTime ldt;
    }

    @Data
    public static class Ld {
        public LocalDate ld;
    }

    @RequestMapping(value = "/test/ldt-zdt.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Xdt ldtZdt(@RequestParam("d") LocalDateTime ldt) {
        final Xdt xdt = new Xdt();
        xdt.zdt = ldt.atZone(StandardTimezone.ASIA𓃬SHANGHAI.toZoneId());
        xdt.ldt = ldt;
        System.out.println("ldtZdt>>>" + xdt);
        System.out.println("userTz>>>" + LocaleContextHolder.getTimeZone());
        return xdt;
    }

    @RequestMapping(value = "/test/zdt-ldt.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Xdt zdtLdt(@RequestParam("d") ZonedDateTime zdt) {
        final Xdt xdt = new Xdt();
        xdt.zdt = zdt;
        xdt.ldt = zdt.toLocalDateTime();
        System.out.println("zdtLdt>>>" + xdt);
        return xdt;
    }

    @RequestMapping(value = "/test/zdt-ldt-body.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Xdt zdtLdt(@RequestBody Xdt xdt) {
        System.out.println("zdtLdt>>>" + xdt);
        return xdt;
    }

    @RequestMapping(value = "/test/ldt-ldt-body.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Ldt ldtLdt(@RequestBody Ldt ldt) {
        System.out.println("ldt>>>" + ldt);
        return ldt;
    }

    @RequestMapping(value = "/test/ld-ld-body.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Ld ldtLdt(@RequestBody Ld ld) {
        System.out.println("Ld>>>" + ld);
        return ld;
    }

    @RequestMapping(value = "/test/datetime-fmt-date.json")
    public String fmtDate(@RequestParam("d") @DateTimeFormat(pattern = "MMM_d_[uuuu][uu]") LocalDate date) {
        return DateFormatter.date10(date);
    }

    @RequestMapping("/test/datetime-full-date.json")
    public String fullDate(@RequestParam("d") LocalDateTime date) {
        return DateFormatter.full23(date);
    }

    @RequestMapping("/test/datetime-local-date.json")
    public String localDate(@RequestParam("d") LocalDate date) {
        return DateFormatter.date10(date);
    }

    @RequestMapping("/test/datetime-local-time.json")
    public String localTime(@RequestParam("d") LocalTime date) {
        return DateFormatter.time12(date);
    }
}
