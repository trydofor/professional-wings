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
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

/**
 * @author trydofor
 * @since 2019-11-13
 */
@RestController
public class TestDateTimeController {

    @Data
    public static class Xdt {
        public OffsetDateTime odt;
        public ZonedDateTime zdt;
        public LocalDateTime ldt;
    }

    @Data
    public static class Ldt {
        @javax.validation.constraints.NotNull
        public LocalDateTime ldt;
    }

    @Data
    public static class Zdt {
        @javax.validation.constraints.NotNull
        public ZonedDateTime zdt;
    }

    @Data
    public static class Odt {
        @javax.validation.constraints.NotNull
        public OffsetDateTime odt;
    }

    @Data
    public static class Ld {
        @javax.validation.constraints.NotNull
        public LocalDate ld;
    }

    @Data
    public static class Lt {
        @javax.validation.constraints.NotNull
        public LocalTime lt;
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

    @RequestMapping(value = "/test/ldt-odt.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Xdt ldtOdt(@RequestParam("d") LocalDateTime ldt) {
        final Xdt xdt = new Xdt();
        final ZonedDateTime zdt = ldt.atZone(StandardTimezone.ASIA𓃬SHANGHAI.toZoneId());
        xdt.odt = zdt.toOffsetDateTime();
        xdt.ldt = ldt;
        System.out.println("ldtOdt>>>" + xdt);
        System.out.println("userTz>>>" + LocaleContextHolder.getTimeZone());
        return xdt;
    }

    @RequestMapping(value = "/test/ldt-zdt-body.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Xdt ldtZdtBody(@RequestBody Ldt ldt) {
        final Xdt xdt = new Xdt();
        xdt.zdt = ldt.ldt.atZone(StandardTimezone.ASIA𓃬SHANGHAI.toZoneId());
        xdt.ldt = ldt.ldt;
        System.out.println("ldtZdtBody>>>" + xdt);
        System.out.println("userTz>>>" + LocaleContextHolder.getTimeZone());
        return xdt;
    }

    @RequestMapping(value = "/test/ldt-odt-body.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Xdt ldtOdtBody(@RequestBody Ldt ldt) {
        final Xdt xdt = new Xdt();
        xdt.odt = ldt.ldt.atZone(StandardTimezone.ASIA𓃬SHANGHAI.toZoneId()).toOffsetDateTime();
        xdt.ldt = ldt.ldt;
        System.out.println("ldtOdtBody>>>" + xdt);
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

    @RequestMapping(value = "/test/odt-ldt.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Xdt odtLdt(@RequestParam("d") OffsetDateTime odt) {
        final Xdt xdt = new Xdt();
        xdt.odt = odt;
        xdt.ldt = odt.toLocalDateTime();
        System.out.println("zdtLdt>>>" + xdt);
        return xdt;
    }

    @RequestMapping(value = "/test/zdt-ldt-body.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Xdt zdtLdt(@RequestBody Zdt zdt) {
        final Xdt xdt = new Xdt();
        xdt.zdt = zdt.zdt;
        xdt.ldt = zdt.zdt.toLocalDateTime();
        System.out.println("zdtLdtBody>>>" + xdt);
        return xdt;
    }

    @RequestMapping(value = "/test/odt-ldt-body.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Xdt odtLdt(@RequestBody Odt odt) {
        final Xdt xdt = new Xdt();
        xdt.odt = odt.odt;
        xdt.ldt = odt.odt.toLocalDateTime();
        System.out.println("zdtLdtBody>>>" + xdt);
        return xdt;
    }

    @RequestMapping(value = "/test/ld-ld-body.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Ld ldLd(@RequestBody Ld ld) {
        System.out.println("LdBody>>>" + ld);
        return ld;
    }

    @RequestMapping(value = "/test/lt-lt-body.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Lt ltLt(@RequestBody Lt lt) {
        System.out.println("LtBody>>>" + lt);
        return lt;
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
