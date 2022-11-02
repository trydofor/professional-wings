package pro.fessional.wings.warlock.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.logging.LogLevel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.R;
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
@ConditionalOnProperty(name = WarlockEnabledProp.Key$controllerTweak, havingValue = "true")
public class AdminTweakController {

    @Operation(summary = "线程级设置日志级别", description =
            "# Usage \n"
            + "根据userId设置日志级别，level==OFF时，为关闭线程设定，复原系统原设置。\n"
            + "## Params \n"
            + "* @param userId - 必填，用户id，MAX_VALUE为全部用户\n"
            + "* @param level - 选填，日志级别，TRACE, DEBUG, INFO, WARN, ERROR和OFF\n"
            + "## Returns \n"
            + "* @return {401} 权限不够时 \n"
            + "* @return {200} 直接访问或redirect时 \n"
            + "")
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$adminTweakLogger + "}")
    @ResponseBody
    public R<Void> adminTweakLogger(@RequestParam("userId") long uid, @RequestParam("level") LogLevel level) {
        TweakLoggerEvent ev = new TweakLoggerEvent();
        ev.setUserId(uid);
        ev.setLevel(level);
        SyncSpring.publishEvent(ev);
        return R.ok();
    }

    @Operation(summary = "线程级设置时钟级别", description =
            "# Usage \n"
            + "根据userId设时钟志级别，mills==OFF时，为关闭线程设定，复原系统原设置。\n"
            + "判断条件，mills在未来3650天(315360000000)，约1980前\n"
            + " * ①与系统时钟相差的毫秒数\n"
            + " * ②固定时间(1970-01-01)\n"
            + " * ③0表示reset\n"
            + "## Params \n"
            + "* @param userId - 必填，用户id，MAX_VALUE为全部用户\n"
            + "* @param mills - 必填，毫秒数\n"
            + "## Returns \n"
            + "* @return {401} 权限不够时 \n"
            + "* @return {200} 直接访问或redirect时 \n"
            + "")
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$adminTweakClock + "}")
    @ResponseBody
    public R<Void> adminTweakClock(@RequestParam("userId") long uid, @RequestParam("mills") long mills) {
        TweakClockEvent ev = new TweakClockEvent();
        ev.setUserId(uid);
        ev.setMills(mills);
        SyncSpring.publishEvent(ev);
        return R.ok();
    }

    @Operation(summary = "线程级设置时钟级别", description =
            "# Usage \n"
            + "根据userId设时钟志级别，stack==null时，为关闭线程设定，复原系统原设置。\n"
            + "## Params \n"
            + "* @param userId - 必填，用户id，MAX_VALUE为全部用户\n"
            + "* @param stack - 选填，是否有堆栈\n"
            + "## Returns \n"
            + "* @return {401} 权限不够时 \n"
            + "* @return {200} 直接访问或redirect时 \n"
            + "")
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$adminTweakStack + "}")
    @ResponseBody
    public R<Void> adminTweakStack(@RequestParam("userId") long uid, @RequestParam(value = "stack", required = false) Boolean stack) {
        TweakStackEvent ev = new TweakStackEvent();
        ev.setUserId(uid);
        ev.setStack(stack);
        SyncSpring.publishEvent(ev);
        return R.ok();
    }
}
