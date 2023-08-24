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

    @Operation(summary = "Create mail and send it sync or async", description = """
            # Usage
            Create the mail, and auto send it in sync or async way.
            ## Params
            * @param - request body
            ## Returns
            * @return {200 | Result(-1)} failure
            * @return {200 | Result(0)} sync send
            * @return {200 | Result(mills)} async send at mills time
            """)
    @PostMapping(value = "${" + TinyMailUrlmapProp.Key$sendMail + "}")
    @ResponseBody
    public R<Long> sendMail(@RequestBody TinyMailPlain mail) {
        final long ms = tinyMailService.auto(mail);
        return R.okData(ms);
    }


    @Operation(summary = "Save the mail only, do not send", description = """
            # Usage
            Save the new mail, return the id.
            ## Params
            * @param - request body
            ## Returns
            * @return {200 | Result(id)} mail id
            """)
    @PostMapping(value = "${" + TinyMailUrlmapProp.Key$sendSave + "}")
    @ResponseBody
    public R<Long> sendSave(@RequestBody TinyMailPlain mail) {
        final long id = tinyMailService.save(mail);
        return R.okData(id);
    }


    @Operation(summary = "sync scan and resend mail async", description = """
            # Usage
            sync scan the mail to resend, return the count, and send them async
            ## Returns
            * @return {200 | Result(count)} mail cou t
            """)
    @PostMapping(value = "${" + TinyMailUrlmapProp.Key$sendScan + "}")
    @ResponseBody
    public R<Integer> sendScan() {
        final int cnt = tinyMailService.scan();
        return R.okData(cnt);
    }

    @Data
    public static class Ins {
        /**
         * Mail id
         */
        private long id;
        /**
         * Whether retry async if fail
         */
        private boolean retry;
        /**
         * Whether to check the send condition, otherwise force send
         */
        private boolean check;
    }


    @Operation(summary = "Sync resend failed mail", description = """
            # Usage
            Sync resend the  failed email, return success/fail, or throw exception
            ## Params
            * @param - request body
            ## Returns
            * @return {200 | Result(bool)} success or not
            """)
    @PostMapping(value = "${" + TinyMailUrlmapProp.Key$sendRetry + "}")
    @ResponseBody
    public R<Boolean> sendRetry(@RequestBody Ins mail) {
        final boolean ok = tinyMailService.send(mail.id, mail.retry, mail.check);
        return R.okData(ok);
    }
}
