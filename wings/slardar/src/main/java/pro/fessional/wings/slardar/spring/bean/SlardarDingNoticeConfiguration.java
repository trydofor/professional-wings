package pro.fessional.wings.slardar.spring.bean;

import okhttp3.OkHttpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.notice.DingTalkNotice;
import pro.fessional.wings.slardar.spring.prop.SlardarDingNoticeProp;

/**
 * @author trydofor
 * @since 2019-06-29
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class SlardarDingNoticeConfiguration {

    private static final Log log = LogFactory.getLog(SlardarDingNoticeConfiguration.class);

    @Bean
    @ConditionalWingsEnabled
    public DingTalkNotice dingTalkNotice(OkHttpClient okHttpClient, SlardarDingNoticeProp prop) {
        log.info("Slardar spring-bean dingTalkNotice");
        return new DingTalkNotice(okHttpClient, prop);
    }

}
