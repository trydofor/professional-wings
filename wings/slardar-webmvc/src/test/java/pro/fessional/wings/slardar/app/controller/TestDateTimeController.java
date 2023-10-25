package pro.fessional.wings.slardar.app.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.time.DateFormatter;
import pro.fessional.wings.slardar.autozone.AutoTimeZone;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static pro.fessional.wings.slardar.autozone.AutoZoneType.Auto;

/**
 * @author trydofor
 * @since 2019-11-13
 */
@RestController
@Slf4j
public class TestDateTimeController {

    public static final ZoneId ZONE_CN = ZoneId.of("Asia/Shanghai");

    @Data
    public static class Xdt {
        private OffsetDateTime odt;
        private ZonedDateTime zdt;
        private LocalDateTime ldt;
        private String sdt;
    }

    @Data
    public static class Ldt {
        @jakarta.validation.constraints.NotNull
        private LocalDateTime ldt;
    }

    @Data
    public static class Zdt {
        @jakarta.validation.constraints.NotNull
        private ZonedDateTime zdt;
    }

    @Data
    public static class Odt {
        @jakarta.validation.constraints.NotNull
        private OffsetDateTime odt;
    }

    @Data
    public static class Ld {
        @jakarta.validation.constraints.NotNull
        private LocalDate ld;
    }

    @Data
    public static class Lt {
        @jakarta.validation.constraints.NotNull
        private LocalTime lt;
    }

    @RequestMapping(value = "/test/ldt-zdt.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Xdt ldtZdt(@RequestParam("d") LocalDateTime ldt) {
        final Xdt xdt = new Xdt();
        final ZonedDateTime zdt = ldt.atZone(ZONE_CN);
        xdt.zdt = zdt;
        xdt.ldt = ldt;
        xdt.sdt = DateFormatter.fullTz(zdt);
        log.info("ldtZdt>>>" + xdt);
        log.info("userTz>>>" + LocaleContextHolder.getTimeZone());
        return xdt;
    }

    @RequestMapping(value = "/test/ldt-odt.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Xdt ldtOdt(@RequestParam("d") LocalDateTime ldt) {
        final Xdt xdt = new Xdt();
        final OffsetDateTime odt = ldt.atZone(ZONE_CN).toOffsetDateTime();
        xdt.odt = odt;
        xdt.ldt = ldt;
        xdt.sdt = DateFormatter.fullTz(odt);
        log.info("ldtOdt>>>" + xdt);
        log.info("userTz>>>" + LocaleContextHolder.getTimeZone());
        return xdt;
    }

    @RequestMapping(value = "/test/ldt-zdt-body.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Xdt ldtZdtBody(@RequestBody Ldt ldt) {
        final Xdt xdt = new Xdt();
        final ZonedDateTime zdt = ldt.ldt.atZone(ZONE_CN);
        xdt.zdt = zdt;
        xdt.ldt = ldt.ldt;
        xdt.sdt = DateFormatter.fullTz(zdt);
        log.info("ldtZdtBody>>>" + xdt);
        log.info("userTz>>>" + LocaleContextHolder.getTimeZone());
        return xdt;
    }

    @RequestMapping(value = "/test/ldt-odt-body.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Xdt ldtOdtBody(@RequestBody Ldt ldt) {
        final Xdt xdt = new Xdt();
        final OffsetDateTime odt = ldt.ldt.atZone(ZONE_CN).toOffsetDateTime();
        xdt.odt = odt;
        xdt.ldt = ldt.ldt;
        xdt.sdt = DateFormatter.fullTz(odt);
        log.info("ldtOdtBody>>>" + xdt);
        log.info("userTz>>>" + LocaleContextHolder.getTimeZone());
        return xdt;
    }

    @RequestMapping(value = "/test/zdt-ldt.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Xdt zdtLdt(@RequestParam("d") ZonedDateTime zdt) {
        final Xdt xdt = new Xdt();
        xdt.zdt = zdt;
        xdt.ldt = zdt.toLocalDateTime();
        xdt.sdt = DateFormatter.fullTz(zdt);
        log.info("zdtLdt>>>" + xdt);
        return xdt;
    }

    @RequestMapping(value = "/test/odt-ldt.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Xdt odtLdt(@RequestParam("d") OffsetDateTime odt) {
        final Xdt xdt = new Xdt();
        xdt.odt = odt;
        xdt.ldt = odt.toLocalDateTime();
        xdt.sdt = DateFormatter.fullTz(odt);
        log.info("zdtLdt>>>" + xdt);
        return xdt;
    }

    @RequestMapping(value = "/test/zdt-ldt-body.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Xdt zdtLdt(@RequestBody Zdt zdt) {
        final Xdt xdt = new Xdt();
        xdt.zdt = zdt.zdt;
        xdt.ldt = zdt.zdt.toLocalDateTime();
        xdt.sdt = DateFormatter.fullTz(zdt.zdt);
        log.info("zdtLdtBody>>>" + xdt);
        return xdt;
    }

    @RequestMapping(value = "/test/odt-ldt-body.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Xdt odtLdt(@RequestBody Odt odt) {
        final Xdt xdt = new Xdt();
        xdt.odt = odt.odt;
        xdt.ldt = odt.odt.toLocalDateTime();
        xdt.sdt = DateFormatter.fullTz(odt.odt);
        log.info("zdtLdtBody>>>" + xdt);
        return xdt;
    }

    @RequestMapping(value = "/test/ld-ld-body.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Ld ldLd(@RequestBody Ld ld) {
        log.info("LdBody>>>" + ld);
        return ld;
    }

    @RequestMapping(value = "/test/lt-lt-body.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Lt ltLt(@RequestBody Lt lt) {
        log.info("LtBody>>>" + lt);
        return lt;
    }

    @Data
    public static class Ldx {
        @jakarta.validation.constraints.NotNull
        @AutoTimeZone(Auto)
        private LocalDateTime ldt;
    }

    @RequestMapping(value = "/test/ldx-body-req.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public String ldxBodyReq(@RequestBody Ldx ldx) {
        log.info("ldx>>>" + ldx);
        return ldx.ldt.toString();
    }

    @RequestMapping(value = "/test/ldx-body-res.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Ldx ldxBodyRes(@RequestParam("d") String ldt) {
        log.info("ldt>>>" + ldt);
        Ldx ldx = new Ldx();
        ldx.setLdt(LocalDateTime.parse(ldt));
        return ldx;
    }

    @RequestMapping(value = "/test/ldt-para-req.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public String ldtParaReq(@AutoTimeZone @RequestParam("d") LocalDateTime ldt) {
        log.info("ldt>>>" + ldt);
        return ldt.toString();
    }

    @RequestMapping(value = "/test/ldt-para-res.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public LocalDateTime ldtParaRes(@RequestParam("d") String ldt) {
        log.info("ldx>>>" + ldt);
        return LocalDateTime.parse(ldt);
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
