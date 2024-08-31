package pro.fessional.wings.silencer.app.conf;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import pro.fessional.wings.silencer.app.TestSilencerCurseApplication;

/**
 * @author trydofor
 * @since 2023-06-26
 */
public class TestApplicationEventLogger implements ApplicationListener<ApplicationEvent> {

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        TestSilencerCurseApplication.log("(spring.factories): " + event.getClass().getSimpleName());
    }
}
