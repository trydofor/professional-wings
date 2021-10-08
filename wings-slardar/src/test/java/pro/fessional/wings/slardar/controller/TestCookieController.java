package pro.fessional.wings.slardar.controller;

import lombok.Data;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author trydofor
 * @since 2021-02-01
 */
@RestController
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
    public String getCookie(@CookieValue("ck1") String ck1,
                            @CookieValue("ck2") String ck2,
                            @RequestBody Ins ins, HttpServletResponse res) {
        res.addCookie(new Cookie("b64", ins.b64));
        res.addCookie(new Cookie("aes", ins.aes));
        res.addCookie(new Cookie("ck1", ins.ck1));
        res.addCookie(new Cookie("ck2", ins.ck2));
        res.addCookie(new Cookie("oth", ins.oth));
        return ck1 + ck2;
    }
}
