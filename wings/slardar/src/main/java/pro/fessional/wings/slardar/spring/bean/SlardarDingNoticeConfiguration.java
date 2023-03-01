package pro.fessional.wings.slardar.spring.bean;

import lombok.Setter;
import okhttp3.OkHttpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.notice.DingTalkNotice;
import pro.fessional.wings.slardar.spring.prop.SlardarDingNoticeProp;
import pro.fessional.wings.spring.consts.OrderedSlardarConst;

/**
 * @author trydofor
 * @since 2019-06-29
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(OrderedSlardarConst.DingNoticeConfiguration)
public class SlardarDingNoticeConfiguration {

    private static final Log log = LogFactory.getLog(SlardarDingNoticeConfiguration.class);

    @Setter(onMethod_ = {@Autowired})
    private SlardarDingNoticeProp slardarDingNoticeProp;

    @Bean
    @ConditionalOnMissingBean(DingTalkNotice.class)
    public DingTalkNotice dingTalkNotice(OkHttpClient okHttpClient) {
        log.info("Slardar spring-bean dingTalkNotice");
        return new DingTalkNotice(okHttpClient, slardarDingNoticeProp);
    }

}
