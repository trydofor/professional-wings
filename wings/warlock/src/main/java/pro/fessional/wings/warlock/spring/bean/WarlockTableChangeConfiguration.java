package pro.fessional.wings.warlock.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.faceless.spring.bean.FacelessJooqCudConfiguration;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.event.EventPublishHelper;
import pro.fessional.wings.warlock.service.event.impl.TableChangePublisherImpl;
import pro.fessional.wings.warlock.service.event.impl.WingsTableCudHandlerImpl;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled(and = FacelessJooqCudConfiguration.CudListenerBean.class)
public class WarlockTableChangeConfiguration {

    private final static Log log = LogFactory.getLog(WarlockTableChangeConfiguration.class);

    @Bean
    @ConditionalWingsEnabled
    public TableChangePublisherImpl tableChangePublisher() {
        log.info("Warlock spring-bean tableChangePublisher");
        final ApplicationEventPublisher publisher;
        if (EventPublishHelper.hasAsyncGlobal()) {
            publisher = EventPublishHelper.AsyncGlobal;
            log.info("Warlock conf tableChangePublisher with async global");
        }
        else {
            publisher = EventPublishHelper.AsyncSpring;
            log.info("Warlock conf tableChangePublisher with async spring");
        }
        return new TableChangePublisherImpl(publisher);
    }

    @Bean
    @ConditionalWingsEnabled
    public WingsTableCudHandlerImpl wingsTableCudHandler() {
        log.info("Warlock spring-bean wingsTableCudHandler");
        return new WingsTableCudHandlerImpl();
    }
}
