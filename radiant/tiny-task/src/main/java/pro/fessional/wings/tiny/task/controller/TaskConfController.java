package pro.fessional.wings.tiny.task.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.Diff;
import pro.fessional.mirana.data.Q;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.tiny.task.schedule.conf.TaskerProp;
import pro.fessional.wings.tiny.task.service.TinyTaskConfService;
import pro.fessional.wings.tiny.task.spring.prop.TinyTaskEnabledProp;
import pro.fessional.wings.tiny.task.spring.prop.TinyTaskUrlmapProp;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2022-12-26
 */
@RestController
@ConditionalWingsEnabled(abs = TinyTaskEnabledProp.Key$mvcConf)
public class TaskConfController {

    @Setter(onMethod_ = {@Autowired})
    protected TinyTaskConfService tinyTaskConfService;

    @Data
    public static class In1 {
        private long id;
        private boolean enable;
    }

    @Operation(summary = "enable or disable a task.")
    @PostMapping(value = "${" + TinyTaskUrlmapProp.Key$taskEnable + "}")
    @ResponseBody
    public R<Boolean> taskEnable(@RequestBody In1 ins) {
        final boolean ok = tinyTaskConfService.enable(ins.id, tinyTaskConfService.enable(ins.id, ins.enable));
        return R.okData(ok);
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class In2 extends TaskerProp {
        private long id;
    }

    @Operation(summary = "update the task config.")
    @PostMapping(value = "${" + TinyTaskUrlmapProp.Key$taskPropSave + "}")
    @ResponseBody
    public R<Boolean> taskPropSave(@RequestBody In2 ins) {
        final boolean ok = tinyTaskConfService.replace(ins.id, ins);
        return R.okData(ok);
    }

    @Operation(summary = "load the task config.")
    @PostMapping(value = "${" + TinyTaskUrlmapProp.Key$taskPropLoad + "}")
    @ResponseBody
    public R<TaskerProp> taskPropLoad(@RequestBody Q.Id ins) {
        final TaskerProp pp = tinyTaskConfService.database(ins.getId(), false);
        return R.okData(pp);
    }

    @Operation(summary = "show the prop of task conf.")
    @PostMapping(value = "${" + TinyTaskUrlmapProp.Key$taskPropConf + "}")
    @ResponseBody
    public R<TaskerProp> taskPropConf(@RequestBody Q.Id ins) {
        final TaskerProp pp = tinyTaskConfService.property(ins.getId(), false);
        return R.okData(pp);
    }

    @Operation(summary = "show the diff of task conf.")
    @PostMapping(value = "${" + TinyTaskUrlmapProp.Key$taskPropDiff + "}")
    @ResponseBody
    public R<Map<String, Diff.V<?>>> taskPropDiff(@RequestBody Q.Id ins) {
        LinkedHashMap<String, Diff.V<?>> df = tinyTaskConfService.diffProp(ins.getId());
        return R.okData(df);
    }
}
