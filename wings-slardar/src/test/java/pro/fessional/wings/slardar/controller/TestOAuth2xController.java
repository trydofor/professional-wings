package pro.fessional.wings.slardar.controller;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.fessional.wings.slardar.security.SecurityContextUtil;
import pro.fessional.wings.slardar.security.WingsOAuth2xContext;
import pro.fessional.wings.slardar.security.WingsTerminalContext;
import pro.fessional.wings.slardar.security.WingsTokenEnhancer;

/**
 * @author trydofor
 * @since 2019-11-13
 */
@Controller
public class TestOAuth2xController {

    @Setter(onMethod = @__({@Autowired}))
    private WingsTokenEnhancer wingsTokenEnhancer;

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
        WingsTerminalContext.Context tcx = SecurityContextUtil.getTerminalContext();
        WingsOAuth2xContext.Context ocx = SecurityContextUtil.getOauth2xContext();
        Authentication aut = SecurityContextHolder.getContext().getAuthentication();
        Object at1 = SecurityContextUtil.getCredentials();
        WingsTokenEnhancer.Info info = wingsTokenEnhancer.getWingsTokenInfo("WG-GWDFQBNEJB-GKJ8DFENZW-NR5L9HNCTVC");

        return "admin";
    }

    @RequestMapping(value = {"/anyone.html"})
    @ResponseBody
    public String anyone() {
        return "anyone";
    }


    @RequestMapping(value = {"/forward.html"})
    public String forward(){
        return "forward:/oauth/token";
    }
}
