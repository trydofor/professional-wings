package pro.fessional.wings.slardar.app.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.fessional.mirana.code.RandCode;
import pro.fessional.wings.slardar.concur.FirstBlood;
import pro.fessional.wings.slardar.servlet.response.ResponseHelper;

/**
 * @author trydofor
 * @since 2019-11-13
 */
@Controller
public class TestCaptchaController {

    @RequestMapping({"/test/captcha.jpg"})
    public void show(HttpServletResponse response, @RequestParam(value = "code", required = false) String code) {
        if (code == null || code.isEmpty()) code = RandCode.mix(4);
        ResponseHelper.showCaptcha(response, code);
    }

    @RequestMapping({"/test/captcha.json"})
    @ResponseBody
    @FirstBlood
    public String captcha() {
        return "captcha";
    }

    @RequestMapping({"/test/captcha-30.json"})
    @ResponseBody
    @FirstBlood(first = 30)
    public String captcha30() {
        return "captcha-30";
    }
}
