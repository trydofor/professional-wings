package pro.fessional.wings.slardar.app.conf;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import pro.fessional.mirana.code.RandCode;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.concur.impl.FirstBloodImageHandler;
import pro.fessional.wings.slardar.servlet.resolver.WingsRemoteResolver;
import pro.fessional.wings.slardar.servlet.response.ResponseHelper;
import pro.fessional.wings.slardar.servlet.response.view.PlainTextView;
import pro.fessional.wings.slardar.spring.prop.SlardarFirstBloodProp;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled(false)
public class SlardarFirstBloodTestConfiguration {

    private static final Log log = LogFactory.getLog(SlardarFirstBloodTestConfiguration.class);

    @Bean
    @ConditionalWingsEnabled
    public FirstBloodImageHandler firstBloodImageHandler(@Autowired(required = false) WingsRemoteResolver remoteResolver,
                                                         SlardarFirstBloodProp firstBloodProp) {
        log.info("Wings conf firstBloodImageHandler for test");
        final FirstBloodImageHandler handler = new Test();
        handler.setClientTicketKey(firstBloodProp.getClientTicketKey());
        handler.setQuestCaptchaKey(firstBloodProp.getQuestCaptchaKey());
        handler.setCheckCaptchaKey(firstBloodProp.getCheckCaptchaKey());
        handler.setBase64CaptchaKey(firstBloodProp.getBase64CaptchaKey());
        handler.setBase64CaptchaBody(firstBloodProp.getBase64CaptchaBody());

        ModelAndView mav = new ModelAndView();
        PlainTextView pv = new PlainTextView(firstBloodProp.getContentType(), firstBloodProp.getResponseBody());
        mav.setStatus(HttpStatus.valueOf(firstBloodProp.getHttpStatus()));
        mav.setView(pv);

        handler.setNeedCaptchaResponse(mav);
        handler.setWingsRemoteResolver(remoteResolver);
        handler.setCaptchaSupplier(() -> RandCode.mix(4));
        return handler;
    }

    private static class Test extends FirstBloodImageHandler {

        @Override
        protected void showCaptcha(@NotNull HttpServletResponse response, String code, String fmt) {
            log.warn("set captcha code=" + code + ", fmt=" + fmt);
            ResponseHelper.writeBodyUtf8(response, code);
        }
    }
}
