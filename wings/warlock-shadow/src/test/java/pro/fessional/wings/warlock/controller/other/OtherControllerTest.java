package pro.fessional.wings.warlock.controller.other;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.best.ArgsAssert;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.pain.CodeException;
import pro.fessional.wings.warlock.enums.errcode.CommonErrorEnum;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * @author trydofor
 * @since 2021-02-27
 */
@RestController
@Slf4j
public class OtherControllerTest {

    @RequestMapping("/test/code-exception.json")
    public String codeException() {
        ArgsAssert.isTrue(true,CommonErrorEnum.AssertEmpty1,"args");
        throw new CodeException(false, CommonErrorEnum.AssertEmpty1, "test");
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
}
