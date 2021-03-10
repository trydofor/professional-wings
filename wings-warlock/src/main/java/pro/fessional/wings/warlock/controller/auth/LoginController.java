package pro.fessional.wings.warlock.controller.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.slardar.security.WingsAuthPageHandler;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author trydofor
 * @since 2021-02-16
 */
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final WingsAuthPageHandler wingsAuthPageHandler;
    private final WingsAuthTypeParser wingsAuthTypeParser;

    @RequestMapping("/auth/login-page.json")
    public ResponseEntity<?> loginPageDefault(HttpServletRequest request, HttpServletResponse response) {
        return wingsAuthPageHandler.response(Null.Enm, MediaType.APPLICATION_JSON, request, response);
    }

    @RequestMapping("/auth/{authType}/login-page.html")
    public ResponseEntity<?> LoginPageRedirect(@PathVariable String authType, HttpServletRequest request, HttpServletResponse response) {
        final Enum<?> em = wingsAuthTypeParser.parse(authType);
        return wingsAuthPageHandler.response(em, MediaType.TEXT_HTML, request, response);
    }

    @RequestMapping("/auth/{authType}/login-page.json")
    public ResponseEntity<?> loginPage(@PathVariable String authType, HttpServletRequest request, HttpServletResponse response) {
        final Enum<?> em = wingsAuthTypeParser.parse(authType);
        return wingsAuthPageHandler.response(em, MediaType.APPLICATION_JSON, request, response);
    }
}
