package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import pro.fessional.mirana.code.RandCode;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.concur.impl.FirstBloodHandler;
import pro.fessional.wings.slardar.concur.impl.FirstBloodImageHandler;
import pro.fessional.wings.slardar.concur.impl.FirstBloodInterceptor;
import pro.fessional.wings.slardar.servlet.resolver.WingsRemoteResolver;
import pro.fessional.wings.slardar.servlet.response.view.PlainTextView;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.slardar.spring.prop.SlardarFirstBloodProp;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled(abs = SlardarEnabledProp.Key$firstBlood, value = false)
public class SlardarFirstBloodConfiguration {

    private static final Log log = LogFactory.getLog(SlardarFirstBloodConfiguration.class);

    @Bean
    @ConditionalWingsEnabled(abs = SlardarEnabledProp.Key$firstBloodImage)
    public FirstBloodImageHandler firstBloodImageHandler(@Autowired(required = false) WingsRemoteResolver remoteResolver, SlardarFirstBloodProp prop) {
        log.info("SlardarWebmvc spring-bean firstBloodImageHandler");
        final FirstBloodImageHandler handler = new FirstBloodImageHandler();
        handler.setScenePrefix(prop.getCaptchaPrefix());
        handler.setClientTicketKey(prop.getClientTicketKey());
        handler.setQuestCaptchaKey(prop.getQuestCaptchaKey());
        handler.setCheckCaptchaKey(prop.getCheckCaptchaKey());
        handler.setBase64CaptchaKey(prop.getBase64CaptchaKey());
        handler.setBase64CaptchaBody(prop.getBase64CaptchaBody());

        ModelAndView mav = new ModelAndView();
        PlainTextView pv = new PlainTextView(prop.getContentType(), prop.getResponseBody());
        mav.setStatus(HttpStatus.valueOf(prop.getHttpStatus()));
        mav.setView(pv);

        handler.setNeedCaptchaResponse(mav);
        handler.setWingsRemoteResolver(remoteResolver);
        handler.setCaseIgnore(prop.isCaseIgnore());
        if (prop.isChineseCaptcha()) {
            log.info("SlardarWebmvc conf firstBloodImageHandler ChineseCaptcha");
            handler.setCaptchaSupplier(() -> RandCode.mix(4));
        }
        return handler;
    }

    @Bean
    @ConditionalWingsEnabled
    public FirstBloodInterceptor firstBloodInterceptor(ObjectProvider<FirstBloodHandler> providers) {
        final List<FirstBloodHandler> handlers = providers.orderedStream().collect(Collectors.toList());
        log.info("SlardarWebmvc spring-bean firstBloodInterceptor, handlers count=" + handlers.size());
        return new FirstBloodInterceptor(handlers);
    }
}
