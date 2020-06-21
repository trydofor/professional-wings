package pro.fessional.wings.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author trydofor
 * @since 2019-11-13
 */
@Controller
public class UserController {


    @RequestMapping("/index.json")
    @ResponseBody
    public String index() {
        return "{'page':'/index.json'}";
    }

    @RequestMapping("/user/create.json")
    @ResponseBody
    public String userCreate() {
        return "{'page':'/user/create.json'}";
    }
}
