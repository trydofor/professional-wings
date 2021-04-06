package pro.fessional.wings.warlock.controller.other;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.pain.CodeException;
import pro.fessional.wings.warlock.enums.errcode.CommonErrorEnum;

/**
 * @author trydofor
 * @since 2021-02-27
 */
@RestController
@Slf4j
public class OtherControllerTest {

    @Setter(onMethod_ = {@Autowired})
    private ApplicationEventPublisher applicationEventPublisher;

    @RequestMapping("/test/code-exception.json")
    public String loginPageDefault() {
        throw new CodeException(CommonErrorEnum.AssertEmpty1, "test");
    }
}
