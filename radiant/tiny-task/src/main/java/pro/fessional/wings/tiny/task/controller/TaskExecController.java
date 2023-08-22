package pro.fessional.wings.tiny.task.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.Q;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.tiny.task.service.TinyTaskExecService;
import pro.fessional.wings.tiny.task.spring.prop.TinyTaskEnabledProp;
import pro.fessional.wings.tiny.task.spring.prop.TinyTaskUrlmapProp;

/**
 * @author trydofor
 * @since 2022-12-26
 */
@RestController
@ConditionalOnProperty(name = TinyTaskEnabledProp.Key$controllerExec, havingValue = "true")
public class TaskExecController {

    @Setter(onMethod_ = {@Autowired})
    protected TinyTaskExecService tinyTaskExecService;


    @Operation(summary = "cancel a task.")
    @PostMapping(value = "${" + TinyTaskUrlmapProp.Key$taskCancel + "}")
    @ResponseBody
    public R<Boolean> taskCancel(@RequestBody Q.Id ins) {
        final boolean cancel = tinyTaskExecService.cancel(ins.getId());
        return R.okData(cancel);
    }

    @Operation(summary = "start a task.")
    @PostMapping(value = "${" + TinyTaskUrlmapProp.Key$taskLaunch + "}")
    @ResponseBody
    public R<Boolean> taskLaunch(@RequestBody Q.Id ins) {
        final boolean cancel = tinyTaskExecService.launch(ins.getId());
        return R.okData(cancel);
    }

    @Operation(summary = "force to start a task.")
    @PostMapping(value = "${" + TinyTaskUrlmapProp.Key$taskForce + "}")
    @ResponseBody
    public R<Boolean> taskForce(@RequestBody Q.Id ins) {
        final boolean cancel = tinyTaskExecService.force(ins.getId());
        return R.okData(cancel);
    }
}
