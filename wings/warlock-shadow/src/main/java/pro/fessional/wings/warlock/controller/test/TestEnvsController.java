package pro.fessional.wings.warlock.controller.test;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.silencer.modulate.RunMode;
import pro.fessional.wings.silencer.modulate.RuntimeMode;
import pro.fessional.wings.slardar.context.Now;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockUrlmapProp;

/**
 * @author trydofor
 * @since 2022-07-22
 */
@RestController
@ConditionalOnProperty(name = WarlockEnabledProp.Key$controllerTest, havingValue = "true")
public class TestEnvsController {

    @Operation(summary = "Get RunMode", description = """
            # Usage
            Return Product, Test, Develop, Local
            """)
    @RequestMapping(value = "${" + WarlockUrlmapProp.Key$testRunMode + "}", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public R<String> testRunMode() {
        final RunMode rm = RuntimeMode.getRunMode();
        return R.okData(rm.name());
    }

    @Operation(summary = "Get system Timestamp", description = """
            # Usage
            Get the Timestamp from 1970 in mills
            """)
    @RequestMapping(value = "${" + WarlockUrlmapProp.Key$testSystemMills + "}", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public R<Long> testSystemMills() {
        final Long ms = System.currentTimeMillis();
        return R.okData(ms);
    }

    @Operation(summary = "Get thread Timestamp", description = """
            # Usage
            Get the Timestamp from 1970 in mills
            """)
    @RequestMapping(value = "${" + WarlockUrlmapProp.Key$testThreadMills + "}", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public R<Long> testThreadMills() {
        final Long ms = Now.millis();
        return R.okData(ms);
    }
}
