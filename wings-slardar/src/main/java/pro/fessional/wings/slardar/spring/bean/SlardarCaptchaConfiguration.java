package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.captcha.CaptchaPicker;
import pro.fessional.wings.slardar.captcha.CaptchaTrigger;
import pro.fessional.wings.slardar.captcha.WingsCaptchaFilter;
import pro.fessional.wings.slardar.captcha.picker.CookiePicker;
import pro.fessional.wings.slardar.captcha.picker.HeaderPicker;
import pro.fessional.wings.slardar.captcha.picker.ParamsPicker;
import pro.fessional.wings.slardar.servlet.WingsServletConst;
import pro.fessional.wings.slardar.spring.prop.SlardarCaptchaProp;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author trydofor
 * @since 2019-07-09
 */
@Configuration
@ConditionalOnProperty(name = SlardarEnabledProp.Key$captcha, havingValue = "true")
public class SlardarCaptchaConfiguration {

    private final Log logger = LogFactory.getLog(SlardarCaptchaConfiguration.class);

    @Bean
    public WingsCaptchaFilter wingsCaptchaFilter(SlardarCaptchaProp config, ObjectProvider<CaptchaTrigger> cts) {
        logger.info("Wings conf Captcha filter");
        List<CaptchaTrigger> triggers = cts.orderedStream().collect(Collectors.toList());
        List<CaptchaPicker> pickers = new ArrayList<>();
        final List<String> cookie = config.getPickerCookie();
        if (cookie != null && cookie.size() > 0) {
            pickers.add(new CookiePicker(cookie));
        }
        final List<String> header = config.getPickerHeader();
        if (header != null && header.size() > 0) {
            pickers.add(new HeaderPicker(header));
        }
        final List<String> params = config.getPickerParams();
        if (params != null && params.size() > 0) {
            pickers.add(new ParamsPicker(params));
        }

        WingsCaptchaFilter filter = new WingsCaptchaFilter(pickers, triggers, config.getHolderSize(), config.getHolderLive());
        filter.setOrder(WingsServletConst.ORDER_FILTER_CAPTCHA);
        return filter;
    }
}
