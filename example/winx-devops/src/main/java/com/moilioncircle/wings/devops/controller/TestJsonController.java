package com.moilioncircle.wings.devops.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author trydofor
 * @since 2021-04-05
 */
@RestController
@Slf4j
public class TestJsonController {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "TestJson")
    public static class Jn {
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
        @JsonFormat(pattern = DateTimePattern.PTN_FULL_19V, shape = JsonFormat.Shape.STRING)
        private ZonedDateTime zonedDateTimePtn = ZonedDateTime.now();
        private Instant instantVal = Instant.now();
        private Date utilDateVal = new Date();
        @Schema(type = "Long", example = "1234567890")
        private Calendar calendarVal = Calendar.getInstance();
        private List<Instant> listVal = Collections.singletonList(Instant.now());
        private Map<LocalDate, LocalDateTime> mapVal = Collections.singletonMap(LocalDate.now(), LocalDateTime.now());
        private String hello;
        private ZoneId systemZoneId;
        private ZoneId userZoneId;
    }

    @Operation(summary = "test json")
    @GetMapping("/test/json/types.json")
    public R<Jn> jsonIt() {
        Jn json = new Jn();
        ZonedDateTime now = ZonedDateTime.now();
        final TerminalContext.Context ctx = TerminalContext.get(false);
        final ZoneId zid = ctx.getTimeZone().toZoneId();
        ZonedDateTime userDate = now.withZoneSameInstant(zid);
        json.setZonedDateTimeVal(userDate);
        json.setHello("hello");
        json.setUserZoneId(zid);
        json.setSystemZoneId(ZoneId.systemDefault());
        return R.okData(json);
    }
}
