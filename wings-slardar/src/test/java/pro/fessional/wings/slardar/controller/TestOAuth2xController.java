package pro.fessional.wings.slardar.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.fessional.wings.slardar.security.SecurityContextUtil;
import pro.fessional.wings.slardar.security.WingsOAuth2xContext;

/**
 * @author trydofor
 * @since 2019-11-13
 */
@Controller
public class TestOAuth2xController {

    @RequestMapping(value = {"/index.html", "/"})
    @ResponseBody
    @Secured("ROLE_USER")
    public String index() {
        return "index";
    }

    @RequestMapping("/user.html")
    @ResponseBody
    @Secured("ROLE_USER")
    public String user() {
        return "user";
    }

    @RequestMapping({"/admin.html"})
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public String admin() {
        WingsOAuth2xContext.Context ocx = SecurityContextUtil.getOauth2xContext();
        Authentication aut = SecurityContextHolder.getContext().getAuthentication();
        Object at1 = SecurityContextUtil.getCredentials();
        return "admin";
    }

    @RequestMapping(value = {"/anyone.html"})
    @ResponseBody
    public String anyone() {
        return "anyone";
    }
}
