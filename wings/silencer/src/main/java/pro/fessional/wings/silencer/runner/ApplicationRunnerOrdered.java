package pro.fessional.wings.silencer.runner;

import lombok.Data;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;

/**
 * ApplicationRunner with order
 *
 * @author trydofor
 * @see ApplicationRunner
 * @since 2023-01-09
 */
@Data
public class ApplicationRunnerOrdered implements ApplicationRunner, Ordered, BeanNameAware {
    private final int order;
    private final ApplicationRunner runner;

    private String beanName;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        runner.run(args);
    }
}
