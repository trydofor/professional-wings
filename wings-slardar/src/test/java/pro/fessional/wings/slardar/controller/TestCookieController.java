package pro.fessional.wings.slardar.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.fessional.wings.slardar.servlet.request.WingsRequestWrapper;
import pro.fessional.wings.slardar.servlet.response.WingsResponseWrapper;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
                            @CookieValue(value = "ck2",required = false) String ck2,
                            @RequestBody Ins ins, HttpServletResponse res, HttpServletRequest req) {
        res.addCookie(new Cookie("b64", ins.b64));
        res.addCookie(new Cookie("aes", ins.aes));
        res.addCookie(new Cookie("ck1", ins.ck1));
        res.addCookie(new Cookie("ck2", ins.ck2));
        res.addCookie(new Cookie("oth", ins.oth));
        log.info("/test/cookie.json ck1={} ck2={}", ck1, ck2);
        final WingsRequestWrapper irq = WingsRequestWrapper.infer(req);
        log.info("wings req wrapper={}", irq);
        final WingsResponseWrapper irs = WingsResponseWrapper.infer(res);
        log.info("wings res wrapper={}", irs);
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
}
