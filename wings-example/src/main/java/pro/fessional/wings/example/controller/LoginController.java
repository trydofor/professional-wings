package pro.fessional.wings.example.controller;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import pro.fessional.wings.slardar.security.WingsOAuth2xHelper;
import pro.fessional.wings.slardar.security.WingsTokenStore;
import pro.fessional.wings.slardar.servlet.TypedRequestUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * @author trydofor
 * @since 2019-11-13
 */
@Controller
public class LoginController {

    @Setter(onMethod = @__({@Autowired}))
    private WingsOAuth2xHelper wingsOAuth2XHelper;

    @Setter(onMethod = @__({@Autowired}))
    private WingsTokenStore wingsTokenStore;

    @Setter(onMethod = @__({@Autowired}))
    private RestTemplate restTemplate;

    @Setter(onMethod = @__({@Value("${wings.oauth.token.url}")}))
    private String oauthTokenUrl;

    @Setter(onMethod = @__({@Value("${wings.slardar.oauth2x.client.staff.client-id}")}))
    private String oauthClientId;

    @PostMapping(value = {"/login.json"})
    @ResponseBody
    public OAuth2AccessToken login(@RequestParam("username") String username, @RequestParam("password") String password) {
        WingsOAuth2xHelper.Login info = new WingsOAuth2xHelper.Login();
        info.setLoginUrl(oauthTokenUrl);
        info.setClientId(oauthClientId); // 必须配置中有，否则失败
        info.setUsername(username);
        info.setPassword(password);
        info.setOauthPasswordAlias("name_pass");
        info.setClientIdAlias("web_admin");
        info.setGrantTypeAlias("admin");
        //info.setAccessToken3rd("bac0c873-e1cc-4740-8b9b-a903dcaaedfe");

        return wingsOAuth2XHelper.login(restTemplate, info);
    }

    @RequestMapping(value = {"/logout.json"})
    @ResponseBody
    public String logout(HttpServletRequest request) {
        String accessToken = TypedRequestUtil.getAccessToken(request);
        wingsOAuth2XHelper.logout(wingsTokenStore, accessToken);
        return "logout " + accessToken;
    }
}
