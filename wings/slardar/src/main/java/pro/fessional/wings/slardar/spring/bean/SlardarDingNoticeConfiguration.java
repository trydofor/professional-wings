package pro.fessional.wings.slardar.spring.bean;

import lombok.Setter;
import okhttp3.OkHttpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import pro.fessional.wings.slardar.notice.DingTalkNotice;
import pro.fessional.wings.slardar.spring.prop.SlardarDingNoticeProp;

import static org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor.DEFAULT_TASK_SCHEDULER_BEAN_NAME;

/**
 * @author trydofor
 * @since 2019-06-29
 */
@Configuration(proxyBeanMethods = false)
public class SlardarDingNoticeConfiguration {

    private static final Log log = LogFactory.getLog(SlardarDingNoticeConfiguration.class);

    @Setter(onMethod_ = {@Autowired})
    private SlardarDingNoticeProp slardarDingNoticeProp;

    @Bean
    @ConditionalOnMissingBean
    public DingTalkNotice dingTalkNotice(OkHttpClient okHttpClient, @Qualifier(DEFAULT_TASK_SCHEDULER_BEAN_NAME) ThreadPoolTaskScheduler executor) {
        log.info("Slardar spring-bean dingTalkNotice");
        final DingTalkNotice bean = new DingTalkNotice(okHttpClient, slardarDingNoticeProp.getDefault(), executor);
        bean.setConfigs(slardarDingNoticeProp);
        return bean;
    }

}
