package pro.fessional.wings.slardar.spring.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.mirana.code.RandCode;
import pro.fessional.wings.slardar.security.WingsCaptchaContext;
import pro.fessional.wings.slardar.servlet.WingsCaptchaFilter;

/**
 * @author trydofor
 * @since 2019-12-12
 */
@Configuration
public class TestCaptchaTriggerConfiguration {

    @Bean
    public WingsCaptchaFilter.CaptchaTrigger skipAllCaptchaTrigger() {
        return (request, sessions) -> null;
    }

    @Bean
    public WingsCaptchaFilter.CaptchaTrigger requestCaptchaTrigger() {
        return (request, sessions) -> {
            if (request.getParameter("ct") != null) {
                String code = RandCode.number(10);
                return WingsCaptchaContext.Context.of(code, "vc", "bad captcha", "/test/vcode.html");
            }
            return null;
        };
    }
}
