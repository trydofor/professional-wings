package pro.fessional.wings.example.controller;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import pro.fessional.wings.slardar.security.WingsOAuth2xLogin;

import javax.servlet.http.HttpServletRequest;

/**
 * @author trydofor
 * @since 2019-06-30
 */

@Controller
public class TestOauth2Controller {

    @Setter(onMethod = @__({@Autowired}))
    private WingsOAuth2xLogin wingsOAuth2xLogin;


    @RequestMapping("/oauth2.html")
    @ResponseBody
    public String oauth2() {
        return "oauth2 ok";
    }

    @RequestMapping(value = {"/oauth2-login.html"})
    @ResponseBody
    public OAuth2AccessToken restLogin() {
        WingsOAuth2xLogin.Login info = new WingsOAuth2xLogin.Login();
        info.setLoginUrl("http://localhost:8081/oauth/token");
        info.setClientId("wings-slardar-id");
        info.setUsername("wings-slardar-user2");
        info.setPassword("wings-slardar-pass");
        info.setOauthPasswordAlias("passwordx");
        info.setClientIdAlias("cid");
        info.setGrantTypeAlias("gtp");
        info.setAccessToken3rd("bac0c873-e1cc-4740-8b9b-a903dcaaedfe");

        OAuth2AccessToken login = wingsOAuth2xLogin.login(new RestTemplate(), info);
        return login;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<OAuth2Exception> handleException(Exception e) throws Exception {
        return new DefaultWebResponseExceptionTranslator().translate(e);
    }
}
