package pro.fessional.wings.silencer.runner;

import lombok.Data;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.core.Ordered;

/**
 * run on ApplicationReadyEvent is sent
 * after any application and command-line runners have been called.
 *
 * @author trydofor
 * @see ApplicationRunner
 * @see ApplicationReadyEvent
 * @since 2023-02-06
 */
@Data
public class ApplicationReadyEventRunner implements Ordered, BeanNameAware {

    private final int order;
    private final ApplicationRunner runner;
    private String beanName;

    public void run(ApplicationArguments args) throws Exception {
        runner.run(args);
    }
}
