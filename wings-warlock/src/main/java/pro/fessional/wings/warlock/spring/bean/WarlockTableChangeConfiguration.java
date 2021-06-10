package pro.fessional.wings.warlock.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.event.EventPublishHelper;
import pro.fessional.wings.warlock.service.event.TableChangePublisher;
import pro.fessional.wings.warlock.service.event.impl.TableChangePublisherImpl;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration
public class WarlockTableChangeConfiguration {

    private final static Log logger = LogFactory.getLog(WarlockTableChangeConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(TableChangePublisher.class)
    public TableChangePublisher tableChangePublisher() {
        final ApplicationEventPublisher publisher;
        if (EventPublishHelper.hasHazelcast()) {
            publisher = EventPublishHelper.AsyncHazelcast;
            logger.info("Wings conf tableChangePublisher");
        }
        else {
            publisher = EventPublishHelper.AsyncSpring;
            logger.info("Wings conf tableChangePublisher");
        }
        return new TableChangePublisherImpl(publisher);
    }

}
