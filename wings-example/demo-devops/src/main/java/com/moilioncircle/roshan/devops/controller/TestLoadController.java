package com.moilioncircle.roshan.devops.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.silencer.datetime.DateTimePattern;
import pro.fessional.wings.slardar.context.TerminalContext;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author trydofor
 * @since 2021-04-05
 */
@RestController
@Slf4j
public class TestLoadController {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JsonIt {
        private boolean boolVal = false;
        private int intVal = Integer.MAX_VALUE - 1;
        private long longVal = Long.MAX_VALUE - 1;
        private float floatVal = 1.1F;
        private double doubleVal = 2.2D;
        private BigDecimal decimalVal = new BigDecimal("3.3");
        private Integer intNull = null;
        private Long longNull = null;
        private LocalDate localDateVal = LocalDate.now();
        private LocalTime localTimeVal = LocalTime.now();
        private LocalDateTime localDateTimeVal = LocalDateTime.now();
        private ZonedDateTime zonedDateTimeVal = ZonedDateTime.now();
        @JsonFormat(pattern = DateTimePattern.PTN_FULL_19V)
        private ZonedDateTime zonedDateTimePtn = ZonedDateTime.now();
        private Instant instantVal = Instant.now();
        private Date utilDateVal = new Date();
        private Calendar calendarVal = Calendar.getInstance();
        private List<String> listVal = Arrays.asList("字符串", "列表");
        private Map<String, Long> mapVal = new HashMap<String, Long>() {{put("Map", 1L);}};
        private String hello;
        private ZoneId systemZoneId;
        private ZoneId userZoneId;
    }

    @GetMapping("/test/load/test.json")
    public R<JsonIt> jsonIt() {
        JsonIt json = new JsonIt();
        ZonedDateTime now = ZonedDateTime.now();
        final TerminalContext.Context ctx = TerminalContext.get();
        final ZoneId zid = ctx.getTimeZone().toZoneId();
        ZonedDateTime userDate = now.withZoneSameInstant(zid);
        json.setZonedDateTimeVal(userDate);
        json.setHello("hello");
        json.setUserZoneId(zid);
        json.setSystemZoneId(ZoneId.systemDefault());
        return R.okData(json);
    }

    private final Random unsafeRandom = new Random();

    @GetMapping("/test/load/sleep.html")
    public String sleep(@RequestParam("ms") long ms) {
        long half = ms / 2;
        long slp = ((long) (unsafeRandom.nextDouble() * half)) + half;
        try {
            Thread.sleep(slp);
        }
        catch (InterruptedException e) {
            // ingore
        }

        return "sleep" + ms;
    }

    @GetMapping({"/test/load/speed.html", "/index.html"})
    public String speed() {
        return "speed";
    }
}
