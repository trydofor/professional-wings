package pro.fessional.wings.slardar.controller;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import pro.fessional.wings.slardar.security.SecurityContextUtil;
import pro.fessional.wings.slardar.security.WingsOAuth2xContext;
import pro.fessional.wings.slardar.security.WingsOAuth2xLogin;
import pro.fessional.wings.slardar.security.WingsTerminalContext;
import pro.fessional.wings.slardar.security.WingsTokenEnhancer;
import pro.fessional.wings.slardar.security.WingsTokenStore;
import pro.fessional.wings.slardar.service.TestUserDetailsService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author trydofor
 * @since 2019-11-13
 */
@Controller
public class TestOAuth2xController {

    @Setter(onMethod = @__({@Autowired}))
    private WingsOAuth2xLogin wingsOAuth2xLogin;

    @Setter(onMethod = @__({@Autowired}))
    private WingsTokenEnhancer wingsTokenEnhancer;

    @Setter(onMethod = @__({@Autowired}))
    private WingsTokenStore wingsTokenStore;

    @Setter(onMethod = @__({@Autowired}))
    private TestUserDetailsService testUserDetailsService;

    @RequestMapping(value = {"/index.html", "/"})
    @ResponseBody
    public String index() {
        return "index";
    }

    @RequestMapping("/user.html")
    @ResponseBody
    public String user() {
        return "user";
    }

    @RequestMapping({"/admin.html"})
    @ResponseBody
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


    @ExceptionHandler(Exception.class)
    public ResponseEntity<OAuth2Exception> handleException(Exception e) throws Exception {
        return new DefaultWebResponseExceptionTranslator().translate(e);
    }

    @RequestMapping(value = {"/test/forward-login.html"})
    public void forwardLogin(HttpServletRequest request, HttpServletResponse response) {
        WingsOAuth2xLogin.Login info = new WingsOAuth2xLogin.Login();
        info.setLoginUrl("/oauth/token");
        info.setClientId("wings-slardar-id");
        info.setUsername(request.getParameter("username"));
        info.setPassword(request.getParameter("password"));
        info.setOauthPasswordAlias("passwordx");
        info.setAccessToken3rd("WG-G3Q-GQWNF-N55XP779TVCQZB");

        testUserDetailsService.setAccountNonExpired("1".equals(request.getParameter("AccountNonExpired")));
        testUserDetailsService.setAccountNonLocked("1".equals(request.getParameter("AccountNonLocked")));
        testUserDetailsService.setCredentialsNonExpired("1".equals(request.getParameter("CredentialsNonExpired")));
        testUserDetailsService.setEnabled("1".equals(request.getParameter("Enabled")));

        wingsOAuth2xLogin.login(request, response, info);
    }

    @RequestMapping(value = {"/test/rest-login.html"})
    @ResponseBody
    public OAuth2AccessToken restLogin(HttpServletRequest request) {
        WingsOAuth2xLogin.Login info = new WingsOAuth2xLogin.Login();
        info.setLoginUrl("http://localhost:8080/oauth/token");
        info.setClientId("wings-slardar-id");
        info.setUsername(request.getParameter("username"));
        info.setPassword(request.getParameter("password"));
        info.setOauthPasswordAlias("passwordx");
        info.setClientIdAlias("cid");
        info.setGrantTypeAlias("gtp");
        info.setAccessToken3rd("bac0c873-e1cc-4740-8b9b-a903dcaaedfe");

        testUserDetailsService.setAccountNonExpired("1".equals(request.getParameter("AccountNonExpired")));
        testUserDetailsService.setAccountNonLocked("1".equals(request.getParameter("AccountNonLocked")));
        testUserDetailsService.setCredentialsNonExpired("1".equals(request.getParameter("CredentialsNonExpired")));
        testUserDetailsService.setEnabled("1".equals(request.getParameter("Enabled")));

        OAuth2AccessToken login = wingsOAuth2xLogin.login(new RestTemplate(), info);
        return login;
    }

    @RequestMapping(value = {"/test/store-logout.html"})
    @ResponseBody
    public String tokenStoreLogout() {
        wingsOAuth2xLogin.logout(wingsTokenStore, "bac0c873-e1cc-4740-8b9b-a903dcaaedfe");
        return "logout bac0c873-e1cc-4740-8b9b-a903dcaaedfe";
    }
}
