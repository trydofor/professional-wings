package pro.fessional.wings.warlock.controller.other;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.best.AssertArgs;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.pain.CodeException;
import pro.fessional.mirana.pain.MessageException;
import pro.fessional.wings.warlock.enums.errcode.CommonErrorEnum;
import pro.fessional.wings.warlock.service.watching.WatchingService;

/**
 * @author trydofor
 * @since 2021-02-27
 */
@RestController
@Slf4j
public class TestOtherController {

    @RequestMapping("/test/code-exception.json")
    public String codeException() {
        AssertArgs.isTrue(true, CommonErrorEnum.AssertEmpty1, "args");
        throw new CodeException(false, CommonErrorEnum.AssertEmpty1, "test");
    }

    @RequestMapping("/test/message-exception.json")
    public String messageException() {
        throw new MessageException(CommonErrorEnum.AssertEmpty1, "test");
    }

    @Data
    public static class Ins {
        @NotBlank(message = "{test.name.empty}")
        private String name;
        @NotBlank(message = "{test.age.empty}")
        private String age;
        @Email(message = "{test.email.invalid}")
        private String email;
    }

    @RequestMapping("/test/binding-error-from.json")
    public R<?> bindingErrorFrom(@Valid Ins ins) {
        log.info(">>>" + ins.toString());
        return R.okData(ins);
    }

    @RequestMapping("/test/binding-error-json.json")
    public R<?> bindingErrorJson(@Valid @RequestBody Ins ins) {
        log.info(">>>" + ins.toString());
        return R.okData(ins);
    }


    @Setter(onMethod_ = {@Autowired})
    protected WatchingService watchingService;

    @RequestMapping("/test/watching.json")
    public R<?> watching() throws InterruptedException {
        watchingService.asyncWatch();
        watchingService.asyncAwait();
        watchingService.normalFetch();
        watchingService.errorFetch();
        return R.ok();
    }
}
