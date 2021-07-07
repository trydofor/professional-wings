package com.moilioncircle.roshan.devops.controller;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.R;

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
    @ApiModel("DumyJson")
    public static class Jn {
        @ApiModelProperty("整数值")
        private int intVal = Integer.MAX_VALUE - 1;
        @ApiModelProperty("整数类")
        private Integer intNull = null;
    }

    private final Random unsafeRandom = new Random();

    @ApiOperation("test sleep")
    @GetMapping("/test/load/sleep.html")
    public R<Jn> sleep(@RequestParam("ms") long ms) {
        long half = ms / 2;
        long slp = ((long) (unsafeRandom.nextDouble() * half)) + half;
        try {
            Thread.sleep(slp);
        }
        catch (InterruptedException e) {
            // ingore
        }

        return R.okData(new Jn());
    }

    @ApiOperation("test speed")
    @GetMapping({"/test/load/speed.html", "/index.html"})
    public String speed() {
        return "speed";
    }
}
