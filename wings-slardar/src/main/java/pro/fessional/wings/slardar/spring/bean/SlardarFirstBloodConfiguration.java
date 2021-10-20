package pro.fessional.wings.slardar.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import pro.fessional.mirana.code.RandCode;
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
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = SlardarEnabledProp.Key$firstBlood, havingValue = "true")
public class SlardarFirstBloodConfiguration {

    private static final Log logger = LogFactory.getLog(SlardarFirstBloodConfiguration.class);
    private final SlardarFirstBloodProp firstBloodProp;

    @Bean
    @ConditionalOnProperty(name = SlardarEnabledProp.Key$firstBloodImage, havingValue = "true")
    public FirstBloodImageHandler firstBloodImageHandler(@Autowired(required = false) WingsRemoteResolver remoteResolver) {
        logger.info("Wings conf firstBloodImageHandler");
        final FirstBloodImageHandler handler = new FirstBloodImageHandler();
        handler.setClientTicketKey(firstBloodProp.getClientTicketKey());
        handler.setFreshCaptchaKey(firstBloodProp.getFreshCaptchaKey());
        handler.setCheckCaptchaKey(firstBloodProp.getCheckCaptchaKey());
        handler.setScenePrefix(firstBloodProp.getCaptchaPrefix());

        ModelAndView mav = new ModelAndView();
        PlainTextView pv = new PlainTextView(firstBloodProp.getContentType(), firstBloodProp.getResponseBody());
        mav.setStatus(HttpStatus.valueOf(firstBloodProp.getHttpStatus()));
        mav.setView(pv);

        handler.setNeedCaptchaResponse(mav);
        handler.setWingsRemoteResolver(remoteResolver);
        handler.setCaseIgnore(firstBloodProp.isCaseIgnore());
        if (firstBloodProp.isChineseCaptcha()) {
            logger.info("Wings conf firstBloodImageHandler ChineseCaptcha");
            handler.setCaptchaSupplier(() -> RandCode.mix(4));
        }
        return handler;
    }

    @Bean
    @ConditionalOnMissingBean(FirstBloodInterceptor.class)
    public FirstBloodInterceptor firstBloodInterceptor(ObjectProvider<FirstBloodHandler> providers) {
        final List<FirstBloodHandler> handlers = providers.orderedStream().collect(Collectors.toList());
        logger.info("Wings conf firstBloodInterceptor, handlers count=" + handlers.size());
        return new FirstBloodInterceptor(handlers);
    }
}
