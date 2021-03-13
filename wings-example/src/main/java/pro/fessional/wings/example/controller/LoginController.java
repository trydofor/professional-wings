package pro.fessional.wings.example.controller;

import io.swagger.annotations.ApiOperation;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import pro.fessional.wings.slardar.servlet.request.RequestHelper;

import javax.servlet.http.HttpServletRequest;

/**
 * @author trydofor
 * @since 2019-11-13
 */
@Controller
public class LoginController {

    @Setter(onMethod_ = {@Autowired})
    private RestTemplate restTemplate;

    @Setter(onMethod_ = {@Value("${wings.oauth.token.url}")})
    private String oauthTokenUrl;

    @Setter(onMethod_ = {@Value("${wings.slardar.oauth2x.client.staff.client-id}")})
    private String oauthClientId;

    @ApiOperation("用户登录")
    @PostMapping(value = {"/login.json"})
    @ResponseBody
    public String login(@RequestParam("username") String username, @RequestParam("password") String password) {
        return "废弃了";
    }

    @ApiOperation("用户登出")
    @RequestMapping(value = {"/logout.json"})
    @ResponseBody
    public String logout(HttpServletRequest request) {
        String accessToken = RequestHelper.getAccessToken(request);
        return "logout " + accessToken;
    }
}
