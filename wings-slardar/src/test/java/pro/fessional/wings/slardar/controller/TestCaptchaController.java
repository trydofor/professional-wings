package pro.fessional.wings.slardar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.fessional.wings.slardar.security.WingsCaptchaContext;

import java.io.IOException;
import java.io.PrintWriter;

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
        WingsCaptchaContext.set(code, (req, res) -> {
                    if (code.equals(req.getParameter("vc"))) {
                        return WingsCaptchaContext.R.PASS;
                    } else {
                        if (req.getRequestURI().contains("/vcode.html")) {
                            return WingsCaptchaContext.R.NOOP;
                        } else {
                            try {
                                PrintWriter writer = res.getWriter();
                                writer.write("bad captcha");
                                writer.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            return WingsCaptchaContext.R.FAIL;
                        }
                    }
                }
        );

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
}
