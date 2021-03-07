package pro.fessional.wings.example.controller;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.example.service.user.UserError;
import pro.fessional.wings.example.service.user.UserService;

/**
 * @author trydofor
 * @since 2019-11-13
 */
@Controller
@Slf4j
public class UserController {

    @Setter(onMethod_ = {@Autowired})
    private UserService userService;

    @RequestMapping("/index.json")
    @ResponseBody
    public String index() {
        return "{'page':'/index.json'}";
    }

    @RequestMapping("/user/create.json")
    @ResponseBody
    public R<Long> userCreate(@RequestBody UserService.UserCreate user) {
        try {
            return userService.create(user);
        } catch (Exception e) {
            log.error("failed to create user", e);
            return R.ng(UserError.UNKNOWN);
        }
    }
}
