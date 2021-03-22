package pro.fessional.wings.slardar.controller;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.time.DateFormatter;
import pro.fessional.wings.faceless.enums.autogen.StandardTimezone;
import pro.fessional.wings.slardar.autozone.json.JacksonZonedSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * @author trydofor
 * @since 2019-11-13
 */
@RestController
public class TestDateTimeController {

    @Data
    public static class Xdt {
        @JsonSerialize(using = JacksonZonedSerializer.class)
        public ZonedDateTime zdt;
        public LocalDateTime ldt;
    }

    @RequestMapping(value = "/test/ldt-zdt.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Xdt ldtZdt(@RequestParam("d") LocalDateTime ldt) {
        final Xdt xdt = new Xdt();
        xdt.zdt=ldt.atZone(StandardTimezone.ASIA𓃬SHANGHAI.toZoneId());
        xdt.ldt = ldt;
        System.out.println("ldtZdt>>>"+xdt);
        System.out.println("userTz>>>"+ LocaleContextHolder.getTimeZone());
        return xdt;
    }

    @RequestMapping(value = "/test/zdt-ldt.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Xdt zdtLdt(@RequestParam("d") ZonedDateTime zdt) {
        final Xdt xdt = new Xdt();
        xdt.zdt = zdt;
        xdt.ldt = zdt.withZoneSameInstant(StandardTimezone.ASIA𓃬SHANGHAI.toZoneId()).toLocalDateTime();
        System.out.println("zdtLdt>>>"+xdt);
        return xdt;
    }

    @RequestMapping("/test/datetime-util-date.json")
    public String utilDate(@RequestParam("d") Date date) {
        return DateFormatter.full23(date);
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
