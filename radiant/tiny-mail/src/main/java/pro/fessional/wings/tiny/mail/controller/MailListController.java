package pro.fessional.wings.tiny.mail.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.Setter;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.Q;
import pro.fessional.mirana.page.PageQuery;
import pro.fessional.mirana.page.PageResult;
import pro.fessional.wings.tiny.mail.service.TinyMailListService;
import pro.fessional.wings.tiny.mail.service.TinyMailPlain;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailUrlmapProp;

/**
 * @author trydofor
 * @since 2023-01-13
 */
@RestController
public class MailListController {

    @Setter(onMethod_ = {@Autowired})
    protected TinyMailListService tinyMailListService;

    @Operation(summary = "获取全部邮件的简要信息，默认倒序")
    @PostMapping(value = "${" + TinyMailUrlmapProp.Key$listAll + "}")
    @ResponseBody
    public PageResult<TinyMailPlain> listAll(@ParameterObject PageQuery pq) {
        return tinyMailListService.listAll(pq);
    }

    @Operation(summary = "获取失败邮件的简要信息，默认倒序")
    @PostMapping(value = "${" + TinyMailUrlmapProp.Key$listFailed + "}")
    @ResponseBody
    public PageResult<TinyMailPlain> listFailed(@ParameterObject PageQuery pq) {
        return tinyMailListService.listFailed(pq);
    }

    @Operation(summary = "获取未成功邮件的简要信息，默认倒序")
    @PostMapping(value = "${" + TinyMailUrlmapProp.Key$listUndone + "}")
    @ResponseBody
    public PageResult<TinyMailPlain> listUndone(@ParameterObject PageQuery pq) {
        return tinyMailListService.listUndone(pq);
    }

    @Operation(summary = "根据Biz-Mark获取邮件的简要信息，默认倒序")
    @PostMapping(value = "${" + TinyMailUrlmapProp.Key$byBizmark + "}")
    @ResponseBody
    public PageResult<TinyMailPlain> byBizMark(@RequestBody Q<String> q, @ParameterObject PageQuery pq) {
        return tinyMailListService.listByBizMark(q.getQ(), pq);
    }

    @Operation(summary = "根据正则比较收件人to/cc/bcc获取邮件的简要信息，默认倒序")
    @PostMapping(value = "${" + TinyMailUrlmapProp.Key$byRecipient + "}")
    @ResponseBody
    public PageResult<TinyMailPlain> byRecipient(@RequestBody Q<String> q, @ParameterObject PageQuery pq) {
        return tinyMailListService.listByRecipient(q.getQ(), pq);
    }

    @Operation(summary = "根据收件人from获取邮件的简要信息，默认倒序")
    @PostMapping(value = "${" + TinyMailUrlmapProp.Key$bySender + "}")
    @ResponseBody
    public PageResult<TinyMailPlain> bySender(@RequestBody Q<String> q, @ParameterObject PageQuery pq) {
        return tinyMailListService.listBySender(q.getQ(), pq);
    }

    @Operation(summary = "根据正则比较邮件标题获取邮件的简要信息，默认倒序")
    @PostMapping(value = "${" + TinyMailUrlmapProp.Key$bySubject + "}")
    @ResponseBody
    public PageResult<TinyMailPlain> bySubject(@RequestBody Q<String> q, @ParameterObject PageQuery pq) {
        return tinyMailListService.listBySubject(q.getQ(), pq);
    }

    @Operation(summary = "获取邮件详情", description =
            "# Usage \n"
            + "获取邮件详情。\n"
            + "## Params \n"
            + "* @param id - 必填，Mailid\n"
            + "")
    @PostMapping(value = "${" + TinyMailUrlmapProp.Key$loadDetail + "}")
    @ResponseBody
    public TinyMailPlain loadDetail(@RequestBody Q.Id ins) {
        return tinyMailListService.loadDetail(ins.getId());
    }
}
