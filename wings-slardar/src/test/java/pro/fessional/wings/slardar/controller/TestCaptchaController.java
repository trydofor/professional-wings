package pro.fessional.wings.slardar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.fessional.wings.slardar.security.WingsCaptchaContext;
import pro.fessional.wings.slardar.security.WingsCaptchaUtil;

import javax.servlet.http.HttpServletResponse;

/**
 * @author trydofor
 * @since 2019-11-13
 */
@Controller
public class TestCaptchaController {

    @RequestMapping({"/test/captcha.html"})
    @ResponseBody
    public String captcha() {
        String code = "ABC123";
        WingsCaptchaUtil.builder()
                        .setCode(code)
                        .setParam("vc")
                        .setFails("bad captcha")
                        .setAllowUri("/test/vcode.html")
                        .buildContext();
        return "captcha";
    }

    @RequestMapping({"/test/image.html"})
    public void image(HttpServletResponse response) {
        WingsCaptchaUtil.builder()
                        .setParam("vc")
                        .setFails("bad captcha")
                        .setAllowUri("/test/vcode.html")
                        .buildCaptcha(response);
    }

    @RequestMapping({"/test/verify.html"})
    @ResponseBody
    public String verify() {
        return "/test/verify.html?vc=ABC123";
    }


    @RequestMapping({"/test/vcode.html"})
    @ResponseBody
    public String vcode() {
        WingsCaptchaContext.Context ctx = WingsCaptchaContext.get();
        return "code=" + ctx.code;
    }

    @RequestMapping(value = "/test/mmethod.html", method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
    public String postPut() {
        return "postPut";
    }
}
