package pro.fessional.wings.slardar.spring.bean;

import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.notify.DingTalkNotifier;
import de.codecentric.boot.admin.server.notify.Notifier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import pro.fessional.wings.slardar.spring.prop.SlardarMonitorProp;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration
@ConditionalOnClass(Notifier.class)
public class SlardarBootAdminConfiguration {
    private final static Log logger = LogFactory.getLog(SlardarBootAdminConfiguration.class);

    @Bean
    public DingTalkNotifier dingTalkNotifier(SlardarMonitorProp conf,
                                             InstanceRepository repository,
                                             RestTemplate restTemplate
    ) {
        final SlardarMonitorProp.DingTalkConf dingTalk = conf.getDingTalk();
        final boolean enabled = StringUtils.hasText(dingTalk.getAccessToken());
        logger.info("Wings conf BootAdmin DingTalkNotifier, enable=" + enabled);
        final DingTalkNotifier bean = new DingTalkNotifier(repository, restTemplate);
        bean.setWebhookUrl(dingTalk.getWebhookUrl());
        bean.setSecret(dingTalk.getDigestSecret());
        bean.setMessage("#{instance.registration.name} #{instance.id} is #{event.statusInfo.status}. " + dingTalk.getReportKeyword());
        bean.setEnabled(enabled);
        return bean;
    }

//    @Bean
//    public InstanceExchangeFilterFunction bootAdminSessionFilter() {
////        return (instance, request, next) -> {
////            request.headers().add("123","");
////            return next.exchange(request).doOnSuccess(res ->{
////                res.headers().header("");
////            });
////        };
//        return (instance, request, next) -> next.exchange(request).doOnSubscribe((s) -> {
//            logger.info(">>>" + request.url());
//            logger.info(">>>" + request.headers());
//            logger.info(">>>" + request.cookies());
//        });
//    }
}
