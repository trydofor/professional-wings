package pro.fessional.wings.silencer.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import pro.fessional.wings.silencer.spring.bean.SpringOrderConfiguration;

/**
 * @author trydofor
 * @since 2023-06-26
 */
public class ApplicationEventLogger implements ApplicationListener<ApplicationEvent> {

    private static final Log log = LogFactory.getLog(SpringOrderConfiguration.class);

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        log.info(">>>>> " + event.getClass().getSimpleName() + "(spring.factories) timestamp=" + event.getTimestamp());
    }
}
