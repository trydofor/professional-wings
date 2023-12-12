package pro.fessional.wings.slardar.app.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * @author trydofor
 * @since 2021-02-01
 */
@Controller
@Slf4j
public class TestCookieController {

    @Data
    public static class Ins {
        private String ck1;
        private String ck2;
        private String b64;
        private String aes;
        private String oth;
    }

    @PostMapping("/test/cookie.json")
    @ResponseBody
    public String getCookie(@CookieValue(value = "ck1", required = false) String ck1,
                            @CookieValue(value = "ck2", required = false) String ck2,
                            @RequestBody Ins ins, HttpServletResponse res, HttpServletRequest req) {
        res.addCookie(newCookie("b64", ins.b64, true, true));
        res.addCookie(newCookie("aes", ins.aes, true, true));
        res.addCookie(newCookie("ck1", ins.ck1, false, true));
        res.addCookie(newCookie("ck2", ins.ck2, false, true));
        res.addCookie(newCookie("oth", ins.oth, false, false));
        log.info("/test/cookie.json ck1={} ck2={}", ck1, ck2);
        return ck1 + ck2;
    }

    @PostMapping("/test/cookie-forward.json")
    public void getCookieForward(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.getRequestDispatcher("/test/cookie.json").forward(req, res);
    }

    @PostMapping("/test/cookie-forward2.json")
    public String getSpringForward() {
        return "forward:/test/cookie.json";
    }

    private Cookie newCookie(String n, String v, boolean h, boolean s) {
        final Cookie ck = new Cookie(n, v);
        ck.setSecure(s);
        ck.setHttpOnly(h);
        return ck;
    }
}
