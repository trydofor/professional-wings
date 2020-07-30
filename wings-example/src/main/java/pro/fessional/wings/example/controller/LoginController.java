package pro.fessional.wings.example.controller;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import pro.fessional.wings.slardar.servlet.TypedRequestUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * @author trydofor
 * @since 2019-11-13
 */
@Controller
public class LoginController {

    @Setter(onMethod = @__({@Autowired}))
    private RestTemplate restTemplate;

    @Setter(onMethod = @__({@Value("${wings.oauth.token.url}")}))
    private String oauthTokenUrl;

    @Setter(onMethod = @__({@Value("${wings.slardar.oauth2x.client.staff.client-id}")}))
    private String oauthClientId;

    @PostMapping(value = {"/login.json"})
    @ResponseBody
    public String login(@RequestParam("username") String username, @RequestParam("password") String password) {
        return "废弃了";
    }

    @RequestMapping(value = {"/logout.json"})
    @ResponseBody
    public String logout(HttpServletRequest request) {
        String accessToken = TypedRequestUtil.getAccessToken(request);
        return "logout " + accessToken;
    }
}
