package pro.fessional.wings.tiny.mail.controller;

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
import pro.fessional.mirana.page.PageQuery;
import pro.fessional.mirana.page.PageResult;
import pro.fessional.wings.tiny.mail.service.TinyMailListService;
import pro.fessional.wings.tiny.mail.service.TinyMailPlain;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailEnabledProp;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailUrlmapProp;

/**
 * @author trydofor
 * @since 2023-01-13
 */
@RestController
@ConditionalOnProperty(name = TinyMailEnabledProp.Key$controllerList, havingValue = "true")
public class MailListController {

    @Setter(onMethod_ = {@Autowired})
    protected TinyMailListService tinyMailListService;

    @Operation(summary = "list summary of all messages, in reverse order by default.")
    @PostMapping(value = "${" + TinyMailUrlmapProp.Key$listAll + "}")
    @ResponseBody
    public PageResult<TinyMailPlain> listAll(PageQuery pq) {
        return tinyMailListService.listAll(pq);
    }

    @Operation(summary = "list summary of failed emails, in reverse order by default.")
    @PostMapping(value = "${" + TinyMailUrlmapProp.Key$listFailed + "}")
    @ResponseBody
    public PageResult<TinyMailPlain> listFailed(PageQuery pq) {
        return tinyMailListService.listFailed(pq);
    }

    @Operation(summary = "list summary of unsuccessful emails, in reverse order by default.")
    @PostMapping(value = "${" + TinyMailUrlmapProp.Key$listUndone + "}")
    @ResponseBody
    public PageResult<TinyMailPlain> listUndone(PageQuery pq) {
        return tinyMailListService.listUndone(pq);
    }

    @Operation(summary = "find summary of the email by Biz-Mark, in reverse order by default.")
    @PostMapping(value = "${" + TinyMailUrlmapProp.Key$byBizmark + "}")
    @ResponseBody
    public PageResult<TinyMailPlain> byBizMark(@RequestBody Q<String> q, PageQuery pq) {
        return tinyMailListService.listByBizMark(q.getQ(), pq);
    }

    @Operation(summary = "find summary of the email by RegExp of to/cc/bcc, reverse order by default.")
    @PostMapping(value = "${" + TinyMailUrlmapProp.Key$byRecipient + "}")
    @ResponseBody
    public PageResult<TinyMailPlain> byRecipient(@RequestBody Q<String> q, PageQuery pq) {
        return tinyMailListService.listByRecipient(q.getQ(), pq);
    }

    @Operation(summary = "find summary of the email by from, in reverse order by default.")
    @PostMapping(value = "${" + TinyMailUrlmapProp.Key$bySender + "}")
    @ResponseBody
    public PageResult<TinyMailPlain> bySender(@RequestBody Q<String> q, PageQuery pq) {
        return tinyMailListService.listBySender(q.getQ(), pq);
    }

    @Operation(summary = "find summary of the email by RegExp of subject, reverse order by default.")
    @PostMapping(value = "${" + TinyMailUrlmapProp.Key$bySubject + "}")
    @ResponseBody
    public PageResult<TinyMailPlain> bySubject(@RequestBody Q<String> q, PageQuery pq) {
        return tinyMailListService.listBySubject(q.getQ(), pq);
    }

    @Operation(summary = "get mail detail", description = """
            # Usage
            get mail detail
            ## Params
            * @param id - required, Mailid
            """)
    @PostMapping(value = "${" + TinyMailUrlmapProp.Key$loadDetail + "}")
    @ResponseBody
    public R<TinyMailPlain> loadDetail(@RequestBody Q.Id ins) {
        return R.okData(tinyMailListService.loadDetail(ins.getId()));
    }
}
