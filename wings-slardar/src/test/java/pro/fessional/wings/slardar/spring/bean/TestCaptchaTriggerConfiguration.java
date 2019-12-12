package pro.fessional.wings.slardar.spring.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.mirana.code.RandCode;
import pro.fessional.wings.slardar.security.WingsCaptchaContext;
import pro.fessional.wings.slardar.servlet.WingsCaptchaFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * @author trydofor
 * @since 2019-12-12
 */
@Configuration
public class TestCaptchaTriggerConfiguration {

    @Bean
    public WingsCaptchaFilter.CaptchaTrigger skipAllCaptchaTrigger() {
        return new WingsCaptchaFilter.CaptchaTrigger() {
            @Override
            public WingsCaptchaContext.Context trigger(HttpServletRequest request, Set<String> sessions) {
                return null;
            }
        };
    }

    @Bean
    public WingsCaptchaFilter.CaptchaTrigger requestCaptchaTrigger() {
        return new WingsCaptchaFilter.CaptchaTrigger() {
            @Override
            public WingsCaptchaContext.Context trigger(HttpServletRequest request, Set<String> sessions) {
                if (request.getParameter("ct") != null) {
                    String code = RandCode.number(10);
                    return WingsCaptchaContext.Context.of(code, "vc", "bad captcha", "/test/vcode.html");
                }
                return null;
            }
        };
    }
}
