package pro.fessional.wings.silencer.runner;

import lombok.Data;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;

/**
 * run in ApplicationRunner witch's Ordered.LOWEST_PRECEDENCE
 *
 * @author trydofor
 * @since 2023-02-05
 */
@Data
public class ApplicationInspectRunner implements Ordered, BeanNameAware {

    private final int order;
    private final ApplicationRunner runner;

    private String beanName;

    public void run(ApplicationArguments args) throws Exception {
        runner.run(args);
    }
}
