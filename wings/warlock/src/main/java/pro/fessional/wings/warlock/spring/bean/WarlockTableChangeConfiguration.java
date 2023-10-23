package pro.fessional.wings.warlock.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.faceless.database.WingsTableCudHandler;
import pro.fessional.wings.slardar.event.EventPublishHelper;
import pro.fessional.wings.warlock.service.event.TableChangePublisher;
import pro.fessional.wings.warlock.service.event.impl.TableChangePublisherImpl;
import pro.fessional.wings.warlock.service.event.impl.WingsTableCudHandlerImpl;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = WarlockEnabledProp.Key$tableChange, havingValue = "true")
public class WarlockTableChangeConfiguration {

    private final static Log log = LogFactory.getLog(WarlockTableChangeConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(TableChangePublisher.class)
    public TableChangePublisher tableChangePublisher() {
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
    @ConditionalOnBean(TableChangePublisher.class)
    public WingsTableCudHandler wingsTableCudHandler() {
        log.info("Warlock spring-bean wingsTableCudHandler");
        return new WingsTableCudHandlerImpl();
    }
}
