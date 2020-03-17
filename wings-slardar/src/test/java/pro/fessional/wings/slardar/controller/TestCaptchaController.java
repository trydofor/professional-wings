package pro.fessional.wings.slardar.controller;

import lombok.val;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.fessional.wings.slardar.security.WingsCaptchaContext;

/**
 * @author trydofor
 * @since 2019-11-13
 */
@Controller
public class TestCaptchaController {

    @RequestMapping({"/test/captcha.html"})
    @ResponseBody
    public String captcha() {
        String code = "123";
        val ctx = WingsCaptchaContext.Context.of(code, "vc", "bad captcha", "/test/vcode.html");
        WingsCaptchaContext.set(ctx);
        return "captcha";
    }

    @RequestMapping({"/test/verify.html"})
    @ResponseBody
    public String verify() {
        return "/test/verify.html?vc=123";
    }


    @RequestMapping({"/test/vcode.html"})
    @ResponseBody
    public String vcode() {
        WingsCaptchaContext.Context ctx = WingsCaptchaContext.get();
        return "code=" + ctx.code;
    }

    @RequestMapping(value = "/test/mmethod.html", method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
    public String postPut(){
        return "postPut";
    }
}
