package pro.fessional.wings.slardar.spring.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.mirana.code.RandCode;
import pro.fessional.wings.slardar.security.util.WingsCaptchaUtil;
import pro.fessional.wings.slardar.servlet.filter.WingsCaptchaFilter;

/**
 * @author trydofor
 * @since 2019-12-12
 */
@Configuration
public class CaptchaTriggerConfigurationTest {

    @Bean
    public WingsCaptchaFilter.CaptchaTrigger skipAllCaptchaTrigger() {
        return (request, sessions) -> null;
    }

    @Bean
    public WingsCaptchaFilter.CaptchaTrigger requestCaptchaTrigger() {
        return (request, sessions) -> {
            if (request.getParameter("ct") != null) {
                String code = RandCode.number(10);
                return WingsCaptchaUtil.builder()
                                       .setCode(code)
                                       .setParam("vc")
                                       .setFails("bad captcha")
                                       .setAllowUri("/test/vcode.html")
                                       .buildContext();
            }
            return null;
        };
    }
}
