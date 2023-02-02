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
import pro.fessional.mirana.page.PageQuery;
import pro.fessional.mirana.page.PageResult;
import pro.fessional.wings.tiny.task.database.autogen.tables.pojos.WinTaskResult;
import pro.fessional.wings.tiny.task.service.TinyTaskListService;
import pro.fessional.wings.tiny.task.spring.prop.TinyTaskEnabledProp;
import pro.fessional.wings.tiny.task.spring.prop.TinyTaskUrlmapProp;

/**
 * @author trydofor
 * @since 2022-12-26
 */
@RestController
@ConditionalOnProperty(name = TinyTaskEnabledProp.Key$controllerList, havingValue = "true")
public class TaskListController {

    @Setter(onMethod_ = {@Autowired})
    protected TinyTaskListService tinyTaskListService;

    @Operation(summary = "列出当前运行中的任务")
    @PostMapping(value = "${" + TinyTaskUrlmapProp.Key$taskRunning + "}")
    @ResponseBody
    public PageResult<TinyTaskListService.Item> taskRunning(PageQuery pq) {
        return tinyTaskListService.listRunning(pq);
    }

    @Operation(summary = "列出已定义的任务")
    @PostMapping(value = "${" + TinyTaskUrlmapProp.Key$taskDefined + "}")
    @ResponseBody
    public PageResult<TinyTaskListService.Item> taskDefined(PageQuery pq) {
        return tinyTaskListService.listDefined(pq);
    }

    @Operation(summary = "列出任务的结果", description = """
            # Usage
            列出任务的结果。
            ## Params
            * @param id - 必填，任务id
            """)
    @PostMapping(value = "${" + TinyTaskUrlmapProp.Key$taskResult + "}")
    @ResponseBody
    public PageResult<WinTaskResult> taskResult(@RequestBody Q.Id ins, PageQuery pq) {
        return tinyTaskListService.listResult(ins.getId(), pq);
    }
}
