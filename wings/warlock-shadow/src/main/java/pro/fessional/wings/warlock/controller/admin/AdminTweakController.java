package pro.fessional.wings.warlock.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @Operation(summary = "线程级设置日志级别", description = """
            # Usage
            根据userId设置日志级别，level==OFF时，为关闭线程设定，复原系统原设置。
            ## Params
            * @param userId - 必填，用户id，MAX_VALUE为全部用户
            * @param level - 选填，日志级别，TRACE, DEBUG, INFO, WARN, ERROR和OFF
            ## Returns
            * @return {401} 权限不够时
            * @return {200} 直接访问或redirect时
            """)
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$adminTweakLogger + "}")
    @ResponseBody
    public R<Void> adminTweakLogger(@RequestBody TweakLoggerEvent ev) {
        SyncSpring.publishEvent(ev);
        return R.OK;
    }

    @Operation(summary = "线程级设置时钟级别", description = """
            # Usage
            根据userId设时钟志级别，mills==OFF时，为关闭线程设定，复原系统原设置。
            判断条件，mills在未来3650天(315360000000)，约1980前
             * ①与系统时钟相差的毫秒数
             * ②固定时间(1970-01-01)
             * ③0表示reset
            ## Params
            * @param userId - 必填，用户id，MAX_VALUE为全部用户
            * @param mills - 必填，毫秒数
            ## Returns
            * @return {401} 权限不够时
            * @return {200} 直接访问或redirect时
            """)
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$adminTweakClock + "}")
    @ResponseBody
    public R<Void> adminTweakClock(@RequestBody TweakClockEvent ev) {
        SyncSpring.publishEvent(ev);
        return R.OK;
    }

    @Operation(summary = "线程级设置时钟级别", description = """
            # Usage
            根据userId设时钟志级别，stack==null时，为关闭线程设定，复原系统原设置。
            ## Params
            * @param userId - 必填，用户id，MAX_VALUE为全部用户
            * @param stack - 选填，是否有堆栈
            ## Returns
            * @return {401} 权限不够时
            * @return {200} 直接访问或redirect时
            """)
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$adminTweakStack + "}")
    @ResponseBody
    public R<Void> adminTweakStack(@RequestBody TweakStackEvent ev) {
        SyncSpring.publishEvent(ev);
        return R.OK;
    }
}
