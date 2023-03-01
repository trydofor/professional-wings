package pro.fessional.wings.silencer.runner;

import lombok.Data;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;

/**
 * CommandLineRunner with order
 *
 * @author trydofor
 * @see CommandLineRunner
 * @since 2023-01-09
 */
@Data
public class CommandLineRunnerOrdered implements CommandLineRunner, Ordered, BeanNameAware {
    private final int order;
    private final CommandLineRunner runner;

    private String beanName;

    @Override
    public void run(String... args) throws Exception {
        runner.run(args);
    }
}
