package pro.fessional.wings.warlock.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.event.tweak.TweakClockEvent;
import pro.fessional.wings.slardar.event.tweak.TweakLoggerEvent;
import pro.fessional.wings.slardar.event.tweak.TweakStackEvent;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockUrlmapProp;

import static pro.fessional.wings.slardar.event.EventPublishHelper.SyncSpring;

/**
 * @author trydofor
 * @since 2022-10-31
 */
@RestController
@ConditionalWingsEnabled(abs = WarlockEnabledProp.Key$mvcTweak, value = false)
public class AdminTweakController {

    @Operation(summary = "Tweak the logging level at the thread level", description = """
            # Usage
            set log level by userId, reset to the original setting if level==OFF.
            ## Params
            * @param userId - required, MAX_VALUE for all user
            * @param level - optional, e.g. TRACE, DEBUG, INFO, WARN, ERROR and OFF
            """)
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$adminTweakLogger + "}")
    @ResponseBody
    public R<Void> adminTweakLogger(@RequestBody TweakLoggerEvent ev) {
        SyncSpring.publishEvent(ev);
        return R.OK;
    }

    @Operation(summary = "Tweak the clock at the thread level", description = """
            # Usage
            Set Clock by userId, reset to the original setting if mills==0
            Condition, mills in the next 3650 days (315360000000), before 1980
            (1) milliseconds difference from the system clock
            (2) fixed time (from 1970-01-01, after 1980)
            (3) 0 means reset setting, restores the original system settings.
            ## Params
            * @param userId - required, MAX_VALUE for all user
            * @param mills - required, millisecond
            """)
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$adminTweakClock + "}")
    @ResponseBody
    public R<Void> adminTweakClock(@RequestBody TweakClockEvent ev) {
        SyncSpring.publishEvent(ev);
        return R.OK;
    }

    @Operation(summary = "Tweak ExceptionStack at the thread level", description = """
            # Usage
            Tweak Stack of Exception by userId, reset to the original setting if stack==null
            ## Params
            * @param userId - required, MAX_VALUE for all user
            * @param stack - optional, whether have stack
            """)
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$adminTweakStack + "}")
    @ResponseBody
    public R<Void> adminTweakStack(@RequestBody TweakStackEvent ev) {
        SyncSpring.publishEvent(ev);
        return R.OK;
    }
}
