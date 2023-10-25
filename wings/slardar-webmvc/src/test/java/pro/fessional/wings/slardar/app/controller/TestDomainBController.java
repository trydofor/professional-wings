package pro.fessional.wings.slardar.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author trydofor
 * @since 2020-09-26
 */
@Controller
@RequestMapping("/domain/b")
public class TestDomainBController {

    @ResponseBody
    @RequestMapping("/user-list.json")
    public String userList() {
        return "b.com/user-list.json";
    }
}
