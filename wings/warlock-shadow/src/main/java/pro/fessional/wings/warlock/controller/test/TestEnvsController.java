package pro.fessional.wings.warlock.controller.test;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.warlock.service.conf.mode.RunMode;
import pro.fessional.wings.warlock.service.conf.mode.RuntimeMode;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockUrlmapProp;

/**
 * @author trydofor
 * @since 2022-07-22
 */
@RestController
@ConditionalOnProperty(name = WarlockEnabledProp.Key$controllerTest, havingValue = "true")
public class TestEnvsController {

    @Operation(summary = "获取 RunMode", description =
            "# Usage \n"
            + "无参数Get取得 Product, Test, Develop, Local\n"
            + "")
    @RequestMapping(value = "${" + WarlockUrlmapProp.Key$testRunMode + "}", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public R<String> testRunMode() {
        final RunMode rm = RuntimeMode.getRunMode();
        final String rt = rm == null ? null : rm.name();
        return R.okData(rt);
    }

    @Operation(summary = "获取系统 Timestamp", description =
            "# Usage \n"
            + "无参数Get取得 1970毫秒数的Timestamp\n"
            + "")
    @RequestMapping(value = "${" + WarlockUrlmapProp.Key$testTimestamp + "}", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public R<Long> testTimestamp() {
        final Long ms = System.currentTimeMillis();
        return R.okData(ms);
    }
}
