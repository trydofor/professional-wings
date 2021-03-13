package pro.fessional.wings.slardar.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.R;

/**
 * @author trydofor
 * @since 2021-02-01
 */
@RestController
public class TestAuthedController {

    @GetMapping("/user/login.json")
    public R<String> login() {
        return R.ok("login page");
    }

    @GetMapping("/authed/see-user.json")
    public R<String> seeUser() {
        return R.ok("saw it");
    }
}
