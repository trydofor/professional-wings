package pro.fessional.wings.silencer.runner;

import lombok.Data;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.core.Ordered;

/**
 * specially use to init Helper.
 * run on ApplicationStartedEvent is sent
 * after the context has been refreshed
 * but before any application and command-line runners have been called.
 *
 * @author trydofor
 * @see ApplicationRunner
 * @see ApplicationStartedEvent
 * @since 2023-02-06
 */
@Data
public class ApplicationStartedEventRunner implements Ordered, BeanNameAware {

    private final int order;
    private final ApplicationRunner runner;
    private String beanName;

    public void run(ApplicationArguments args) throws Exception {
        runner.run(args);
    }
}
