package pro.fessional.wings.example.controller;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.fessional.wings.example.auth.SecuredService;

/**
 * @author trydofor
 * @since 2020-06-21
 */
@Controller
public class TestSecuredController {

    @Setter(onMethod_ = {@Autowired})
    SecuredService securedService;

    @RequestMapping("/test/secured-test.json")
    @ResponseBody
    public String securedTest() {
        return securedService.secured();
    }
}
