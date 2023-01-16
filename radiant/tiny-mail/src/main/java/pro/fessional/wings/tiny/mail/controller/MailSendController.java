package pro.fessional.wings.tiny.mail.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.Data;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.tiny.mail.service.TinyMailPlain;
import pro.fessional.wings.tiny.mail.service.TinyMailService;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailEnabledProp;
import pro.fessional.wings.tiny.mail.spring.prop.TinyMailUrlmapProp;

/**
 * @author trydofor
 * @since 2023-01-13
 */
@RestController
@ConditionalOnProperty(name = TinyMailEnabledProp.Key$controllerSend, havingValue = "true")
public class MailSendController {

    @Setter(onMethod_ = {@Autowired})
    protected TinyMailService tinyMailService;

    @Operation(summary = "新建或编辑邮件，并同步立即或异步定时发送")
    @PostMapping(value = "${" + TinyMailUrlmapProp.Key$sendMail + "}")
    @ResponseBody
    public R<Boolean> sendMail(@RequestBody TinyMailPlain mail) {
        final boolean ok = tinyMailService.send(mail);
        return R.okData(ok);
    }

    @Operation(summary = "仅新建或编辑邮件，但并不发送")
    @PostMapping(value = "${" + TinyMailUrlmapProp.Key$sendSave + "}")
    @ResponseBody
    public R<Long> sendSave(@RequestBody TinyMailPlain mail) {
        final long id = tinyMailService.save(mail);
        return R.okData(id);
    }

    @Operation(summary = "同步扫需要描补发的邮件，并异步发送，返回补发的件数")
    @PostMapping(value = "${" + TinyMailUrlmapProp.Key$sendScan + "}")
    @ResponseBody
    public R<Integer> sendScan() {
        final int cnt = tinyMailService.scan();
        return R.okData(cnt);
    }

    @Data
    public static class Ins {
        private long id;
        private boolean retry;
        private boolean check;
    }

    @Operation(summary = "仅新建或编辑邮件，但并不发送")
    @PostMapping(value = "${" + TinyMailUrlmapProp.Key$sendRetry + "}")
    @ResponseBody
    public R<Boolean> sendRetry(@RequestBody Ins mail) {
        final boolean ok = tinyMailService.send(mail.id, mail.retry, mail.check);
        return R.okData(ok);
    }
}
